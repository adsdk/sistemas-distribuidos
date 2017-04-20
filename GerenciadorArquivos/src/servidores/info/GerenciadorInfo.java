package servidores.info;

import java.io.Serializable;

/**
 *
 * @author adrisson.silva
 */
public class GerenciadorInfo implements Serializable {

    private final String nome;
    private final String IP;
    private final int port;

    public GerenciadorInfo(String nome, String IP, int port) {
        this.nome = nome;
        this.IP = IP;
        this.port = port;
    }

    public String getNome() {
        return nome;
    }

    public String getIP() {
        return IP;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "GerenciadorInfo{" + "nome=" + nome + ", IP=" + IP + ", port=" + (port < 0 ? "NONE" : port) + '}';
    }

}
