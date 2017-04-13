package comunicacao.mensagens;

import servidores.info.GerenciadorInfo;
import comunicacao.enums.Acao;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @created 11/04/2017
 * @author alencar.hentges (CWI Software)
 */
public class MensagemListaGerenciadores extends Mensagem implements Serializable {

    private List<GerenciadorInfo> gerenciadoresInfo;

    public MensagemListaGerenciadores(Acao acao) {
        super(acao);
    }

    public MensagemListaGerenciadores(List<GerenciadorInfo> gerenciadoresInfo, Acao acao) {
        super(acao);
        this.gerenciadoresInfo = gerenciadoresInfo;
    }

    public List<GerenciadorInfo> getGerenciadoresInfo() {
        return gerenciadoresInfo;
    }

    public void setGerenciadoresInfo(List<GerenciadorInfo> gerenciadoresInfo) {
        this.gerenciadoresInfo = gerenciadoresInfo;
    }

}
