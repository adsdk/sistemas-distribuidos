package comunicacao.mensagens;

import comunicacao.enums.Acao;
import java.io.Serializable;

/**
 *
 * @created 11/04/2017
 * @author alencar.hentges (CWI Software)
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
