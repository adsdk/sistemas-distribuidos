package com.ulbra.core;

import com.ulbra.config.Configuracao;
import java.io.Serializable;

public class Usuario implements Serializable {

    private final ControladorConexao conexao;
    private final Configuracao configuracaoServer;

    public Usuario(ControladorConexao conexao, Configuracao configuracaoServer) {
        this.conexao = conexao;
        this.configuracaoServer = configuracaoServer;
    }

    public ControladorConexao getConexao() {
        return conexao;
    }

    public Configuracao getConfiguracaoServer() {
        return configuracaoServer;
    }

}
