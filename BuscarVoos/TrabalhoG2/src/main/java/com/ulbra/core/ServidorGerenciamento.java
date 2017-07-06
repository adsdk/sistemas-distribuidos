package com.ulbra.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.ulbra.config.Configuracao;
import com.ulbra.config.SpringMongoConfig;
import com.ulbra.model.ListServer;
import com.ulbra.model.RetornoPesquisa;
import com.ulbra.model.Server;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;

/**
 * @author adrisson.silva
 */
public class ServidorGerenciamento extends Thread {

    private final List<UsuarioListener> USUARIOS = new ArrayList<>();
    
    private static XMemcachedClientBuilder builder;
    private static MemcachedClient clientMemcached;
    
    private static Configuracao configuracaoServer;
    
    private static final Gson gson = new GsonBuilder().create();
    private static final ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
    private static final MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");

    private final ServerSocket serverSocket;
    private boolean rodando;
    
    public static void main(String[] args) throws IOException {
        URL resource = ServidorGerenciamento.class.getResource("/config.json");
        BufferedReader br = new BufferedReader(new FileReader(resource.getFile()));
        configuracaoServer = gson.fromJson(br, Configuracao.class);
        
        builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(configuracaoServer.getMemcachedServer() + ":" + configuracaoServer.getMemcachedPort()));
        clientMemcached = builder.build();
        
        try {
            String jsonListServers = clientMemcached.get("SD_ListServers");
            ListServer listServer = new ListServer();
            if (jsonListServers != null) {
                listServer = gson.fromJson(jsonListServers, ListServer.class);
            }
            
            boolean serverCached = false;
            for (Server server : listServer.getServers()) {
                if (server.getName().equals(configuracaoServer.getServerName())) {
                    serverCached = true;
                    server.setActive(true);
                }
            }
            
            if (!serverCached) {
                Server server = new Server(configuracaoServer.getServerName(), configuracaoServer.getServerIP(), 
                        configuracaoServer.getPortListen(), configuracaoServer.getYearData());
                listServer.getServers().add(server);
            }
            
            clientMemcached.set("SD_ListServers", 0, gson.toJson(listServer));
            
        } catch (Exception e) {
            System.out.println(e);
        }
        
        for (int i = 0; i < configuracaoServer.getYearData().length; i++) {
            String json = null;
            try {
                //to review: colocar para cachear na inicialização os anos que o servidor é responsável
                json = clientMemcached.get("SD_Data_"+configuracaoServer.getYearData()[i]);
            } catch (Exception e) {
                System.out.println(e);
            }
            if (json == null) {
                try {
                    BasicDBObject command = new BasicDBObject();

                    String instrucao = "db.ontime.aggregate([{$match: {Year: " + configuracaoServer.getYearData()[i] + " }}, {$group: {_id: null, arrivalOnTimeFlights: {$sum: {$cond: [{$lt: ['$ArrDelay', 1]}, 1, 0]}}, arrivalDelayedFlights: {$sum: {$cond: [{$gt: ['$ArrDelay', 0]}, 1, 0]}}, arrivalDelayedAverageTime: {$avg: '$ArrDelay'}, departureOnTimeFlights: {$sum: {$cond: [{$lt: ['$DepDelay', 1]}, 1, 0]}}, departureDelayedFlights: {$sum: {$cond: [{$gt: ['$DepDelay', 0]}, 1, 0]}}, departureDelayedAverageTime: {$avg: '$DepDelay'} }}]);";
                    command.put("eval", "function() { return " + instrucao + " }");

                    CommandResult executeCommand = mongoOperation.executeCommand(command);
                    BasicDBObject retval = (BasicDBObject) executeCommand.get("retval");
                    BasicDBList _batch = (BasicDBList) retval.get("_batch");
                    RetornoPesquisa retorno = gson.fromJson(_batch.get(0).toString(), RetornoPesquisa.class);

                    retorno.setArrivalDelayedAverageTime(toTime(Double.valueOf(retorno.getArrivalDelayedAverageTime())));
                    retorno.setDepartureDelayedAverageTime(toTime(Double.valueOf(retorno.getDepartureDelayedAverageTime())));


                    clientMemcached.set("SD_Data_"+configuracaoServer.getYearData()[i], 0, gson.toJson(retorno));
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
        
        ServidorGerenciamento novoServidor = new ServidorGerenciamento(configuracaoServer.getPortListen());
        iniciarServidor(novoServidor);
    }

    public ServidorGerenciamento(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    private static void iniciarServidor(final ServidorGerenciamento novoServidor) {
        Thread startServer = new Thread(() -> {
            try {
                novoServidor.iniciarServidor();
            } catch (IOException ex) {
                Logger.getLogger(ServidorGerenciamento.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        startServer.start();
    }

    public void iniciarServidor() throws IOException {
        if (isRodando()) {
            return;
        }
        this.rodando = Boolean.TRUE;
        System.out.println("Servidor Iniciado...");
        while (isRodando()) {
            ControladorConexao novaConexao = new ControladorConexao(serverSocket.accept());
            UsuarioListener temp = new UsuarioListener(new Usuario(novaConexao, configuracaoServer));
            if (!USUARIOS.contains(temp)) {
                Usuario novoUsuario = new Usuario(novaConexao, configuracaoServer);
                UsuarioListener novoUsuarioListener = new UsuarioListener(novoUsuario);
                USUARIOS.add(novoUsuarioListener);
                novoUsuarioListener.start();
            }
        }
    }

    public boolean isRodando() {
        return rodando;
    }
    
    private static String toTime(Double doubleValue) {
        long timeInMilliseconds = (long) Math.floor(doubleValue * 60 * 1000);
        Date date = new Date(timeInMilliseconds);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }

}
