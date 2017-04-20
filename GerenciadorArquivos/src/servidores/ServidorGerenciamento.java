package servidores;

import comunicacao.ControladorConexao;
import comunicacao.enums.Status;
import comunicacao.mensagens.Mensagem;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import servidores.enums.TipoArquivo;
import servidores.info.GerenciadorInfo;
import servidores.pojo.Arquivo;
import servidores.pojo.Usuario;
import utilitarios.UtilGeral;

/**
 *
 * @author adrisson.silva
 */
public class ServidorGerenciamento extends Thread {

    //lista estática pois pode ser acessada por qualquer servidor de gerenciamento
    private static final List<ServidorArquivos> SERVIDORES_ARQUIVO = new ArrayList<>();

    private final List<UsuarioListener> USUARIOS = new ArrayList<>();

    private Long idGerenciador;
    private GerenciadorInfo info;
    private ServerSocket serverSocket;
    private boolean rodando;

    public ServidorGerenciamento(Long idGerenciador, String nome, int port) throws IOException {
        this.idGerenciador = idGerenciador;
        this.serverSocket = new ServerSocket(port);
        this.info = new GerenciadorInfo(nome, InetAddress.getLocalHost().getHostAddress(), port);
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

    public static synchronized void addGerenciadorArquivos(ServidorArquivos servidorArquivo) {
        //TODO: verificar se o gerenciador de arquivos com o mesmo nome já existe,
        //por causa que cada gerenciador de arquivos cria uma pasta com seu nome;
        if (!SERVIDORES_ARQUIVO.contains(servidorArquivo)) {
            SERVIDORES_ARQUIVO.add(servidorArquivo);
            System.out.println("Servidor de arquivo criado com sucesso!");
            System.out.println(servidorArquivo);
            Collections.sort(SERVIDORES_ARQUIVO);
        } else {
            System.out.println("Servidor de arquivo com o nome '" + servidorArquivo.getInfo().getNome() + "' já existe!");
        }
    }

    public static void mostrarGerenciadoresDeArquivoAtivos() {
        if (SERVIDORES_ARQUIVO.isEmpty()) {
            System.out.println("\nNENHUM SERVIDOR DE ARQUIVOS ATIVO! ");
        } else {
            SERVIDORES_ARQUIVO.stream()
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

    public static synchronized void inativarGerenciadorDeArquivo(String nome) throws UnknownHostException {
        int pos = SERVIDORES_ARQUIVO.indexOf(new ServidorArquivos(nome));
        if (pos != -1) {
            SERVIDORES_ARQUIVO.get(pos).inativar();
        }
    }

    public static synchronized void ativarGerenciadorDeArquivo(String nome) throws UnknownHostException {
        int pos = SERVIDORES_ARQUIVO.indexOf(new ServidorArquivos(nome));
        if (pos != -1) {
            SERVIDORES_ARQUIVO.get(pos).ativar();
        }
    }

    public static Arquivo buscarArquivo(String nomeArquivo) {
        Arquivo arquivo = null;
        for (ServidorArquivos servidorArquivos : SERVIDORES_ARQUIVO) {
            arquivo = servidorArquivos.buscarArquivo(nomeArquivo);
            if (arquivo != null) {
                return arquivo;
            }
        }
        return arquivo;
    }

    public synchronized static Status adicionarNovoArquivo(String nome, String conteudo) {
        Arquivo arquivo = buscarArquivo(nome);
        if (arquivo == null) {
            if (existeServidorDeArquivosAtivo()) {
                SERVIDORES_ARQUIVO.get(0).adicionarArquivo(new Arquivo(nome, TipoArquivo.DOCUMENTO, conteudo));
                Collections.sort(SERVIDORES_ARQUIVO);
                return Status.REQUISICAO_OK;
            } else {
                return Status.SERVIDOR_INDISPONIVEL;
            }
        }
        return Status.ARQUIVO_JA_EXISTE;
    }

    public static boolean existeServidorDeArquivosAtivo() {
        return SERVIDORES_ARQUIVO.stream().anyMatch((servidorArquivos) -> (servidorArquivos.isAtivo()));
    }

}
