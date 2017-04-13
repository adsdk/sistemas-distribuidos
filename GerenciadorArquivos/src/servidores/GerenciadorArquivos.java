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
 * @created 11/04/2017
 * @author alencar.hentges (CWI Software)
 */
public class GerenciadorArquivos implements Comparable<GerenciadorArquivos> {

    private final GerenciadorInfo info;
    private final List<Arquivo> arquivos;
    private boolean ativo;

    public GerenciadorArquivos(String nome) throws UnknownHostException {
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
    public int compareTo(GerenciadorArquivos o) {
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
        final GerenciadorArquivos other = (GerenciadorArquivos) obj;
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
