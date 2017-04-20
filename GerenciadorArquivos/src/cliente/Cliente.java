package cliente;

import comunicacao.ControladorConexao;
import comunicacao.enums.Acao;
import comunicacao.mensagens.MensagemListaGerenciadores;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import servidores.info.GerenciadorInfo;

/**
 *
 * @author adrisson.silva
 */
public class Cliente {

    private static ControladorConexao<String> controladorConexao;
    private static final BufferedReader KEYBOARD_INPUT = new BufferedReader(new InputStreamReader(System.in));
    private static final String SERVIDOR_PRINCIPAL = "127.0.0.1";

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        System.out.print("Conectar ao servidor de gerenciamento via principal (S/N)? ");
        String opc = KEYBOARD_INPUT.readLine();

        if ("S".equalsIgnoreCase(opc)) {
            MensagemListaGerenciadores listaGerenciadores = buscarServidorDeGerenciamentoAtivos();
            conectarViaPrincipal(listaGerenciadores);
        } else {
            conectarDiretamente();
        }

    }

    private static void conectarViaPrincipal(MensagemListaGerenciadores msgLista) throws IOException, ClassNotFoundException {
        if (!msgLista.getGerenciadoresInfo().isEmpty()) {
            System.out.println("\nLISTA DE SERVIDORES DE GERENCIAMENTO ATIVOS: ");

            msgLista.getGerenciadoresInfo().stream().forEach((gerenciadorInfo) -> {
                System.out.println(gerenciadorInfo);
            });

            System.out.print("\nInforme o nome do servidor de gerenciamento que você deseja conectar-se: ");
            String nome = KEYBOARD_INPUT.readLine();

            for (GerenciadorInfo gerenciadorInfo : msgLista.getGerenciadoresInfo()) {
                if (gerenciadorInfo.getNome().equalsIgnoreCase(nome)) {
                    estabelecerConexaoComServidor(gerenciadorInfo.getIP(), gerenciadorInfo.getPort());
                    break;
                }
            }
        } else {
            System.out.println("\nNENHUM SERVIDOR DE GERENCIAMENTO ATIVO! ");
        }
    }

    private static MensagemListaGerenciadores buscarServidorDeGerenciamentoAtivos() throws ClassNotFoundException {
        try {
            controladorConexao = new ControladorConexao<>(SERVIDOR_PRINCIPAL, 6789);
            Object msg = controladorConexao.receber();
            controladorConexao.close();
            MensagemListaGerenciadores msgLista = (MensagemListaGerenciadores) msg;
            return msgLista;
        } catch (IOException ex) {
            System.out.println("Servidor principal não está ativo!");
            throw new RuntimeException();
        }
    }

    private static void conectarDiretamente() throws IOException, ClassNotFoundException {
        System.out.print("\nInforme o ip do servidor de gerenciamento que você deseja conectar-se: ");
        String ip = KEYBOARD_INPUT.readLine();

        System.out.print("\nInforme a porta do servidor de gerenciamento que você deseja conectar-se: ");
        String port = KEYBOARD_INPUT.readLine();

        estabelecerConexaoComServidor(ip, Integer.valueOf(port));
    }

    private static void estabelecerConexaoComServidor(String ip, int port) throws IOException, ClassNotFoundException {
        System.out.println("Estabelecendo conexão com o servidor...");
        controladorConexao = new ControladorConexao(ip, port);
        System.out.println("Conexão estabelecida com sucesso!");

        String msg = controladorConexao.receber();
        while (controladorConexao.isConectionOpen()) {
            tratarMensagem(msg);
            
            String retorno = controladorConexao.receber();
            System.out.println(retorno);
            
            System.out.println("Deseja fazer outra requisição? (S/N)");
            String opc = KEYBOARD_INPUT.readLine();
            
            if (!"S".equalsIgnoreCase(opc)) {
                controladorConexao.close();
            }
        }
    }

    private static void tratarMensagem(String msg) throws IOException {
        switch (Acao.valueOf(msg)) {
            case OBTER_REQUISICAO:
                System.out.print("\nInforme a sua requisição: ");
                String requisicao = KEYBOARD_INPUT.readLine();
                controladorConexao.enviar(requisicao);
                break;
        }
    }
}
