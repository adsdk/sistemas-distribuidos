package comunicacao.enums;

import java.io.Serializable;

/**
 *
 * @author adrisson.silva
 */
public enum Status implements Serializable{

    REQUISICAO_OK(0, "Requisição OK"),
    REQUISICAO_NOK(404, "Requisição não encontrada"),
    SERVIDOR_INDISPONIVEL(1, "Servidor Indisponível"),
    ARQUIVO_INEXISTENTE(2, "Arquivo Inexistente"),
    ARQUIVO_JA_EXISTE(3, "Arquivo Já Existe"),
    ARQUIVO_INDISPONIVEL(4, "Arquivo Indisponível");
    
    private int codStatus;
    private String descStatus;

    Status(int codStatus, String descStatus) {
        this.codStatus = codStatus;
        this.descStatus = descStatus;
    }
    
    public int getCodStatus() {
        return codStatus;
    }

    public void setCodStatus(int codStatus) {
        this.codStatus = codStatus;
    }

    public String getDescStatus() {
        return descStatus;
    }

    public void setDescStatus(String descStatus) {
        this.descStatus = descStatus;
    }
    
}
