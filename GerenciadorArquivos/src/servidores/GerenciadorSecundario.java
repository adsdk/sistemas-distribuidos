package servidores;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import servidores.info.GerenciadorInfo;

/**
 *
 * @created 11/04/2017
 * @author alencar.hentges (CWI Software)
 */
public class GerenciadorSecundario extends Thread {

    private static final List<GerenciadorArquivos> GERENCIADORES_ARQUIVOS = new ArrayList<>();

    private Long idGerenciador;
    private GerenciadorInfo info;
    private ServerSocket serverSocket;

    public GerenciadorSecundario(Long idGerenciador, String nome, int port) throws IOException {
        this.idGerenciador = idGerenciador;
        this.serverSocket = new ServerSocket(port);
        this.info = new GerenciadorInfo(nome, InetAddress.getLocalHost().getHostAddress(), port);
    }

    public GerenciadorSecundario(int port) {
    }

    @Override
    public void run() {
        //TODO: lógica do servidor.
        //fica escutando por novas conexões;
        //quando uma novo conexão(não existente) conectar
        //adicionar a lista de conexões e criar uma thread única para ouvi-la.
    }

    public static synchronized void addGerenciadorArquivos(GerenciadorArquivos gerenciadorArquivos) {
        //TODO: verificar se o gerenciador de arquivos com o mesmo nome já existe,
        //por causa que cada gerenciador de arquivos cria uma pasta com seu nome;
        GERENCIADORES_ARQUIVOS.add(gerenciadorArquivos);
        Collections.sort(GERENCIADORES_ARQUIVOS);
    }

    public static synchronized void invativarGerenciadorDeArquivo(String nome) throws UnknownHostException {
        int pos = GERENCIADORES_ARQUIVOS.indexOf(new GerenciadorArquivos(nome));
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
        final GerenciadorSecundario other = (GerenciadorSecundario) obj;
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
}
