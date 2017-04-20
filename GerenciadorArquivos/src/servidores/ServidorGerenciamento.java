package servidores;

import comunicacao.ControladorConexao;
import comunicacao.mensagens.Mensagem;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import servidores.info.GerenciadorInfo;
import servidores.pojo.Usuario;
import utilitarios.UtilGeral;

/**
 *
 * @author adrisson.silva
 */
public class ServidorGerenciamento extends Thread {

    //lista estática pois pode ser acessada por qualquer servidor de gerenciamento
    private static final List<ServidorArquivos> GERENCIADORES_ARQUIVOS = new ArrayList<>();
    
    private final List<UsuarioListener> USUARIOS = new ArrayList<>();

    private Long idGerenciador;
    private GerenciadorInfo info;
    private ServerSocket serverSocket;
    private boolean rodando;

    public ServidorGerenciamento(Long idGerenciador, String nome, int port) throws IOException {
        this.idGerenciador = idGerenciador;
        this.serverSocket = new ServerSocket(port);
        this.info = new GerenciadorInfo(nome, InetAddress.getLocalHost().getHostAddress(), port);
        
        Thread startServer = new Thread(() -> {
            try {
                this.iniciarServidor();
            } catch (IOException ex) {
                Logger.getLogger(ServidorGerenciamento.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        startServer.start();
    }

    public ServidorGerenciamento(int port) {
    }

    public void iniciarServidor() throws IOException {
        if (isRodando()) {
            return;
        }
        this.rodando = Boolean.TRUE;
        System.out.println("Servidor Iniciado...");
        while (isRodando()) {
            ControladorConexao<Mensagem> novaConexao = new ControladorConexao(serverSocket.accept());
            UsuarioListener temp = new UsuarioListener(new Usuario(null, novaConexao));
            if (!USUARIOS.contains(temp)) {
                Usuario novoJogador = new Usuario(UtilGeral.gerarNomeAleatorio(), novaConexao);
                UsuarioListener novoJogadorListener = new UsuarioListener(novoJogador);
                USUARIOS.add(novoJogadorListener);
                novoJogadorListener.start();
            }
        }
    }

    public static synchronized void addGerenciadorArquivos(ServidorArquivos gerenciadorArquivos) {
        //TODO: verificar se o gerenciador de arquivos com o mesmo nome já existe,
        //por causa que cada gerenciador de arquivos cria uma pasta com seu nome;
        GERENCIADORES_ARQUIVOS.add(gerenciadorArquivos);
        Collections.sort(GERENCIADORES_ARQUIVOS);
    }

    public static synchronized void inativarGerenciadorDeArquivo(String nome) throws UnknownHostException {
        int pos = GERENCIADORES_ARQUIVOS.indexOf(new ServidorArquivos(nome));
        if (pos != -1) {
            GERENCIADORES_ARQUIVOS.get(pos).inativar();
        }
    }

    public static void mostrarGerenciadoresDeArquivoAtivos() {
        if (GERENCIADORES_ARQUIVOS.isEmpty()) {
            System.out.println("\nNENHUM SERVIDOR DE ARQUIVOS ATIVO! ");
        } else {
            GERENCIADORES_ARQUIVOS.stream()
                    .filter(x -> x.isAtivo())
                    .collect(Collectors.toList())
                    .forEach(System.out::println);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ServidorGerenciamento other = (ServidorGerenciamento) obj;
        return Objects.equals(this.idGerenciador, other.idGerenciador);
    }

    @Override
    public String toString() {
        return "GerenciadorSecundario{" + "idGerenciador=" + idGerenciador + ", info=" + info + '}';
    }

    public Long getIdGerenciador() {
        return idGerenciador;
    }

    public void setIdGerenciador(Long idGerenciador) {
        this.idGerenciador = idGerenciador;
    }

    public GerenciadorInfo getInfo() {
        return info;
    }

    public boolean isRodando() {
        return rodando;
    }
}
