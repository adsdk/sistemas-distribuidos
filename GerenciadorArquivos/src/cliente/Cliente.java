package cliente;

import comunicacao.ControladorConexao;
import comunicacao.mensagens.Mensagem;
import comunicacao.mensagens.MensagemListaGerenciadores;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @created 11/04/2017
 * @author alencar.hentges (CWI Software)
 */
public class Cliente {

    private static ControladorConexao<Mensagem> controladorConexao;
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

    private static void conectarViaPrincipal(MensagemListaGerenciadores msgLista) throws IOException {
        if (!msgLista.getGerenciadoresInfo().isEmpty()) {
            System.out.println("\nLISTA DE SERVIDORES DE GERENCIAMENTO ATIVOS: ");

            msgLista.getGerenciadoresInfo().stream().forEach((gerenciadorInfo) -> {
                System.out.println(gerenciadorInfo);
            });

            System.out.print("\nInforme o nome do servidor de gerenciamento que você deseja conectar-se: ");
            String nome = KEYBOARD_INPUT.readLine();

            System.out.println("\nCONECTADO AO SERVIDOR DE GERENCIAMENTO " + nome + "! ");
        } else {
            System.out.println("\nNENHUM SERVIDOR DE GERENCIAMENTO ATIVO! ");
        }
    }

    private static MensagemListaGerenciadores buscarServidorDeGerenciamentoAtivos() throws ClassNotFoundException {
        try {
            controladorConexao = new ControladorConexao<>(SERVIDOR_PRINCIPAL, 6789);
            Mensagem msg = controladorConexao.receber();
            controladorConexao.close();
            MensagemListaGerenciadores msgLista = (MensagemListaGerenciadores) msg;
            return msgLista;
        } catch (IOException ex) {
            System.out.println("Servidor principal não está ativo!");
            throw new RuntimeException();
        }
    }

    private static void conectarDiretamente() throws IOException {
        System.out.print("\nInforme o ip do servidor de gerenciamento que você deseja conectar-se: ");
        String ip = KEYBOARD_INPUT.readLine();

        System.out.print("\nInforme a porta do servidor de gerenciamento que você deseja conectar-se: ");
        String port = KEYBOARD_INPUT.readLine();

        System.out.println("\nCONECTADO AO SERVIDOR DE GERENCIAMENTO! (" + ip + ":" + port + ") ");
    }

}
