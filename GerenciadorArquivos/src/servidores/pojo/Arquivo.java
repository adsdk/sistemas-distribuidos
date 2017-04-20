package servidores.pojo;

import java.util.Objects;
import servidores.ServidorArquivos;
import servidores.enums.TipoArquivo;

/**
 *
 * @author adrisson.silva
 */
public class Arquivo {

    private final String nome;
    private final TipoArquivo tipo;
    private ServidorArquivos localArmazenado;

    public Arquivo(String nome, TipoArquivo tipoArquivo) {
        this.nome = nome;
        this.tipo = tipoArquivo;
    }

    public String getNome() {
        return nome;
    }

    public ServidorArquivos getLocalArmazenado() {
        return localArmazenado;
    }

    public void setLocalArmazenado(ServidorArquivos localArmazenado) {
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
