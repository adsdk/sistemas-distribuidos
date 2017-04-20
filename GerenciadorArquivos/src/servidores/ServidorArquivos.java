package servidores;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import servidores.enums.TipoArquivo;
import servidores.info.GerenciadorInfo;
import servidores.pojo.Arquivo;

/**
 *
 * @author adrisson.silva
 */
public class ServidorArquivos implements Comparable<ServidorArquivos> {

    private final GerenciadorInfo info;
    private final List<Arquivo> arquivos;
    private boolean ativo;

    public ServidorArquivos(String nome) throws UnknownHostException {
        arquivos = new ArrayList<>();
        info = new GerenciadorInfo(nome, InetAddress.getLocalHost().getHostAddress(), -1);
        this.ativo = true;
    }

    public Arquivo buscarArquivo(String nome) {
        int pos = arquivos.indexOf(new Arquivo(nome, TipoArquivo.DOCUMENTO));
        if (pos != -1) {
            return arquivos.get(pos);
        }
        return null;
    }

    @Override
    public int compareTo(ServidorArquivos o) {
        if (ativo) {
            return new Integer(arquivos.size()).compareTo(o.arquivos.size());
        }
        return 1;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
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
        final ServidorArquivos other = (ServidorArquivos) obj;
        return Objects.equals(this.info.getNome(), other.info.getNome());
    }

    public void inativar() {
        ativo = false;
    }

    public void ativar() {
        ativo = true;
    }

    public GerenciadorInfo getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return "GerenciadorArquivos{" + "info=" + info + ", ativo=" + ativo + '}';
    }

}
