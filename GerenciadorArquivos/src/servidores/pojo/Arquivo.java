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
    private final String conteudo;
    private final TipoArquivo tipo;
    private ServidorArquivos localArmazenado;
    private boolean arquivoDisponivel;

    public Arquivo(String nome, TipoArquivo tipoArquivo, String contudo) {
        this.nome = nome;
        this.tipo = tipoArquivo;
        this.conteudo = contudo;
        this.arquivoDisponivel = true;
    }

    public void salvar() {
        //TODO: fazer o arquivo ser salvo em disco.
    }

    public void deletar() {
        //TODO: apagar o arquivo do disco
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

    public String getConteudo() {
        return conteudo;
    }

    public boolean isArquivoDisponivel() {
        return arquivoDisponivel;
    }

    public void setArquivoDisponivel(boolean arquivoDisponivel) {
        this.arquivoDisponivel = arquivoDisponivel;
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
