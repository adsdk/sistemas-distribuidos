package comunicacao.mensagens;

import comunicacao.enums.Acao;
import java.io.Serializable;

/**
 *
 * @author adrisson.silva
 */
public class Mensagem implements Serializable {

    private final Acao acao;

    public Mensagem(Acao acao) {
        this.acao = acao;
    }

    public Acao getAcao() {
        return acao;
    }

}
