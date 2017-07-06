package com.ulbra.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.ulbra.config.SpringMongoConfig;
import com.ulbra.model.Airport;
import com.ulbra.model.Carrier;
import com.ulbra.model.ListAirport;
import com.ulbra.model.ListCarrier;
import com.ulbra.model.ListServer;
import com.ulbra.model.ListYear;
import com.ulbra.model.RetornoPesquisa;
import com.ulbra.model.Server;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collections;
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
public class UsuarioListener extends Thread implements Serializable {

    private final Usuario usuario;
    private final Gson gson = new GsonBuilder().create();
    private final XMemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses("localhost:11211"));
    private final MemcachedClient clientMemcached;

    private final ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
    private final MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");

    public UsuarioListener(Usuario usuario) throws IOException {
        this.usuario = usuario;
        clientMemcached = builder.build();
    }

    @Override
    public void run() {
        try {
            this.usuario.getConexao().enviar("Informe sua requisicao (exit para sair):");
        } catch (IOException ex) {
        }

        while (this.usuario.getConexao().isConectionOpen()) {
            try {
                String msg = this.usuario.getConexao().receber();
                if (msg == null) {
                    interrupt();
                } else {
                    String[] request = msg.split(" ");
                    tratarMensagem(request, msg);
                }
            } catch (IOException | ClassNotFoundException ex) {
                System.out.println("faio");
            }
        }
    }

    @Override
    public void interrupt() {
        try {
            this.usuario.getConexao().close();
        } catch (IOException ex) {
            Logger.getLogger(UsuarioListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void tratarMensagem(String[] msg, String msgOriginal) throws IOException {
        String result;
        String json = null;
        ListServer listServer = new ListServer();
        switch (msg[0].toUpperCase()) {
            case "GETAVAILABLEYEARS":
                
                try {
                    String jsonListServers = clientMemcached.get("SD_ListServers");
                    listServer = gson.fromJson(jsonListServers, ListServer.class);
                } catch (Exception e) {
                    result = "{\"errorCode\": 500, \"errorDescription\": \"InternalServerError\"}";
                }
                
                ListYear listYears = new ListYear();
                for (Server server : listServer.getServers()) {
                    if (server.isActive()) {
                        for (int i = 0; i < server.getYear().length; i++) {
                            if (!listYears.getYears().contains(server.getYear()[i])) {
                                listYears.getYears().add(server.getYear()[i]);
                            }
                        }
                    }
                }
                
                Collections.sort(listYears.getYears());
                
                result = gson.toJson(listYears);
                this.usuario.getConexao().enviar(result);
                break;
            case "GETAIRPORTS":
                try {
                    json = clientMemcached.get("SD_Airports");
                } catch (Exception e) {
                    System.out.println(e);
                }

                if (json != null) {
                    result = json;
                } else {
                    ListAirport airports = new ListAirport();
                    List<Airport> listAirport = this.mongoOperation.findAll(Airport.class);
                    airports.setAirports(listAirport);
                    result = gson.toJson(airports);
                    
                    try {
                        clientMemcached.set("SD_Airports", 0, result);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
                
                this.usuario.getConexao().enviar(result);
                break;
            case "GETCARRIERS":
                try {
                    json = clientMemcached.get("SD_Carriers");
                } catch (Exception e) {
                    System.out.println(e);
                }

                if (json != null) {
                    result = json;
                } else {
                    ListCarrier carriers = new ListCarrier();
                    List<Carrier> listCarrier = this.mongoOperation.findAll(Carrier.class);
                    carriers.setCarriers(listCarrier);
                    result = gson.toJson(carriers);
                    
                    try {
                        clientMemcached.set("SD_Carriers", 0, result);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
                
                this.usuario.getConexao().enviar(result);
                break;
            case "GETDELAYDATA":
                if (msg.length < 2) {
                    this.usuario.getConexao().enviar("{\"errorCode\": 404, \"errorDescription\": \"Requisicao nao encontrada\"}");
                    break;
                }
                
                String getCache = "SD_Data_";
                for (int i = 1; i < msg.length; i++) {
                    if (!msg[i].equals("***")) {
                        getCache += msg[i];
                    }
                    if ((i + 1) < msg.length) {
                        getCache += "_";
                    }
                }
                
                try {
                    json = clientMemcached.get(getCache);
                } catch (Exception e) {
                    System.out.println(e);
                }
                
                if (json != null) {
                    result = json;
                } else {
                    //SE NÃO POSSUIR CACHE E É UM DOS MEUS ANOS, BUSCAR NO BANCO
                    boolean meuAno = false;
                    for (int i = 0; i < this.usuario.getConfiguracaoServer().getYearData().length; i++) {
                        if (String.valueOf(this.usuario.getConfiguracaoServer().getYearData()[i]).equals(msg[1].substring(0, 4))) {
                            meuAno = true;
                        }
                    }
                    if (meuAno) {
                        BasicDBObject command = new BasicDBObject();
                        String ano = null, mes = null, dia = null, carrier = null, airport = null;
                        if (msg[1].length() > 6) {
                            ano = msg[1].substring(0, 4);
                            mes = msg[1].substring(4, 6);
                            dia = msg[1].substring(6);
                        } else if (msg[1].length() > 4) {
                            ano = msg[1].substring(0, 4);
                            mes = msg[1].substring(4);
                        } else {
                            ano = msg[1];
                        }

                        String matchMes = mes != null ? ", {Month: " + mes + " } " : "";
                        String matchDia = dia != null ? ", {DayofMonth: " + dia + " } " : "";

                        if (msg.length > 2) {
                            if (!msg[2].equals("***")) {
                                airport = msg[2];
                            }

                            if (msg.length == 4) {
                                carrier = msg[3];
                            }
                        }

                        String matchAiport = airport != null ? ", {Origin: '" + airport + "' } " : "";
                        String matchCarrier = carrier != null ? ", {UniqueCarrier: '" + carrier + "' } " : "";

                        String matches = "{$match: {$and: [ {Year: " + ano + " } " + matchMes + matchDia + matchAiport + matchCarrier + " ] } }";

                        String instrucao = "db.ontime.aggregate([ " + matches + ", {$group: {_id: null, arrivalOnTimeFlights: {$sum: {$cond: [{$lt: ['$ArrDelay', 1]}, 1, 0]}}, arrivalDelayedFlights: {$sum: {$cond: [{$gt: ['$ArrDelay', 0]}, 1, 0]}}, arrivalDelayedAverageTime: {$avg: '$ArrDelay'}, departureOnTimeFlights: {$sum: {$cond: [{$lt: ['$DepDelay', 1]}, 1, 0]}}, departureDelayedFlights: {$sum: {$cond: [{$gt: ['$DepDelay', 0]}, 1, 0]}}, departureDelayedAverageTime: {$avg: '$DepDelay'} }}]);";
                        command.put("eval", "function() { return " + instrucao + " }");

                        CommandResult executeCommand = this.mongoOperation.executeCommand(command);
                        BasicDBObject retval = (BasicDBObject) executeCommand.get("retval");
                        BasicDBList _batch = (BasicDBList) retval.get("_batch");
                        
                        if (_batch.isEmpty()) {
                            result = "{\"errorCode\": 2, \"errorDescription\": \"Dados Inexistentes\"}";
                        } else {
                        
                            RetornoPesquisa retorno = gson.fromJson(_batch.get(0).toString(), RetornoPesquisa.class);

                            retorno.setArrivalDelayedAverageTime(toTime(Double.valueOf(retorno.getArrivalDelayedAverageTime())));
                            retorno.setDepartureDelayedAverageTime(toTime(Double.valueOf(retorno.getDepartureDelayedAverageTime())));

                            result = gson.toJson(retorno);

                            try {
                                clientMemcached.set(getCache, 0, result);
                            } catch (Exception e) {
                                System.out.println(e);
                            }
                        }
                    } else {
                        
                        //SE NÃO FOR MEU ANO, VERIFICAR SE EXISTE ALGUM ANO RESPONSÁVEL
                        try {
                            String jsonListServers = clientMemcached.get("SD_ListServers");
                            listServer = gson.fromJson(jsonListServers, ListServer.class);
                        } catch (Exception e) {
                            result = "{\"errorCode\": 500, \"errorDescription\": \"InternalServerError\"}";
                        }
                        
                        Server serverWithData = null;
                        for (Server server : listServer.getServers()) {
                            for (int i = 0; i < server.getYear().length; i++) {
                                if (String.valueOf(server.getYear()[0]).equals(msg[1].substring(0, 4))) {
                                    serverWithData = server;
                                    break;
                                }
                            }
                        }
                        
                        if (serverWithData != null) {
                            if (serverWithData.isActive()) {
                                String[] location = serverWithData.getLocation().split(":");
                                try {
                                    result = getDelayServerFromOtherServer(msgOriginal, location[0], location[1]);
                                } catch (Exception ex) {
                                    result = "{\"errorCode\": 3, \"errorDescription\": \"Falha no server do coleguinha\"}";
                                }
                            } else {
                                result = "{\"errorCode\": 1, \"errorDescription\": \"Servidor Indisponível\"}";
                            }
                        } else {
                            result = "{\"errorCode\": 2, \"errorDescription\": \"Dados Inexistentes\"}";
                        }
                    }
                    
                }
                this.usuario.getConexao().enviar(result);
                break;
            case "EXIT":
                interrupt();
            case "GETROLA":
                try {
                    msgOriginal = "GETDELAYDATA 2002";
                    result = getDelayServerFromOtherServer(msgOriginal, "localhost", "6790");
                } catch (Exception ex) {
                    result = "faio";
                }
                this.usuario.getConexao().enviar(result);
                break;
            default:
                this.usuario.getConexao().enviar("{\"errorCode\": 404, \"errorDescription\": \"Requisicao nao encontrada\"}");
        }
    }

    private String toTime(Double doubleValue) {
        long timeInMilliseconds = (long) Math.floor(doubleValue * 60 * 1000);
        Date date = new Date(timeInMilliseconds);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }
    
    private String getDelayServerFromOtherServer(String message, String ip, String port) throws Exception {
        ControladorConexao conexao = new ControladorConexao(ip, Integer.parseInt(port));
        conexao.limparBuffer();
        conexao.enviar(message);
        String result = conexao.receber();
        conexao.close();
        return result;
    }
}
