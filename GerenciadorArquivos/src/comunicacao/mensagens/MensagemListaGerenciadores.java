package comunicacao.mensagens;

import comunicacao.enums.Acao;
import java.io.Serializable;
import java.util.List;
import servidores.info.GerenciadorInfo;

/**
 *
 * @author adrisson.silva
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
