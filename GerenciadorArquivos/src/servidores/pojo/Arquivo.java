package servidores.pojo;

import java.util.Objects;
import servidores.GerenciadorArquivos;
import servidores.enums.TipoArquivo;

/**
 *
 * @created 11/04/2017
 * @author alencar.hentges (CWI Software)
 */
public class Arquivo {

    private final String nome;
    private final TipoArquivo tipo;
    private GerenciadorArquivos localArmazenado;

    public Arquivo(String nome, TipoArquivo tipoArquivo) {
        this.nome = nome;
        this.tipo = tipoArquivo;
    }

    public String getNome() {
        return nome;
    }

    public GerenciadorArquivos getLocalArmazenado() {
        return localArmazenado;
    }

    public void setLocalArmazenado(GerenciadorArquivos localArmazenado) {
        this.localArmazenado = localArmazenado;
    }

    public TipoArquivo getTipo() {
        return tipo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        final Arquivo other = (Arquivo) obj;
        if (!Objects.equals(this.nome, other.nome)) {
            return false;
        }
        return this.tipo == other.tipo;
    }

}
