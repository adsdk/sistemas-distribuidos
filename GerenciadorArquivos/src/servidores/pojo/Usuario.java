package servidores.pojo;

import comunicacao.ControladorConexao;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author adrisson.silva
 */
public class Usuario implements Serializable {

    private final ControladorConexao<String> conexao;
    private String nomeUsuario;

    public Usuario(String nome, ControladorConexao conexao) {
        this.nomeUsuario = nome;
        this.conexao = conexao;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public ControladorConexao<String> getConexao() {
        return conexao;
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
        final Usuario other = (Usuario) obj;
        return Objects.equals(this.conexao, other.conexao);
    }

    @Override
    public String toString() {
        return "Usuario{nomeJogador=" + nomeUsuario + "}";
    }

}
