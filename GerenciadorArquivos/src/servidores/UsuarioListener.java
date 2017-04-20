package servidores;

import comunicacao.enums.Acao;
import comunicacao.enums.Status;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import servidores.pojo.Usuario;
import utilitarios.UtilGeral;

/**
 * Classe responsável por controlar uma única conexão (por objeto).
 *
 * @author adrisson.silva
 */
public class UsuarioListener extends Thread implements Serializable {

    private final Usuario usuario;

    public UsuarioListener(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Inicia a thread que faz o controle de uma conexão especifica.
     */
    @Override
    public void run() {
        try {
            enviarInformacoesDeUsuario();
            while (this.usuario.getConexao().isConectionOpen()) {
                String msg = this.usuario.getConexao().receber();
                UtilGeral.printarRecebimentoInfo(usuario, msg);
                String[] request = msg.split(" ");
                tratarMensagem(request);
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("faio");
        }
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
        final UsuarioListener other = (UsuarioListener) obj;
        return Objects.equals(this.usuario, other.usuario);
    }

    /**
     * Finaliza a conexão antes de finalizar a Thread.
     */
    @Override
    public void interrupt() {
        try {
            this.usuario.getConexao().close();
        } catch (IOException ex) {
            Logger.getLogger(UsuarioListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Faz o tratamento da mensagem recebida
     *
     * @param msg
     * @throws IOException
     */
    private void tratarMensagem(String[] msg) throws IOException {
        if (msg.length < 2) {
            this.usuario.getConexao().enviar(Status.REQUISICAO_NOK.getDescStatus());
        }

        switch (msg[0]) {
            case "PUT":
                if (msg.length < 3) {
                    this.usuario.getConexao().enviar(Status.REQUISICAO_NOK.getDescStatus());
                }
                this.usuario.getConexao().enviar(Status.REQUISICAO_OK.getDescStatus());
                break;
            case "GET":
                this.usuario.getConexao().enviar(Status.ARQUIVO_INEXISTENTE.getDescStatus());
                break;
            case "DELETE":
                this.usuario.getConexao().enviar(Status.ARQUIVO_INDISPONIVEL.getDescStatus());
                break;
            default:
                this.usuario.getConexao().enviar(Status.REQUISICAO_NOK.getDescStatus());
        }
    }

    /**
     * Envia as informações inicias(dados do usuário) para o cliente que abriu a
     * conexão
     *
     * @throws IOException
     */
    private void enviarInformacoesDeUsuario() throws IOException {
        String msg = Acao.OBTER_REQUISICAO.toString();
        UtilGeral.printarEnvioInfo(usuario, msg);
        this.usuario.getConexao().enviar(msg);
    }

}
