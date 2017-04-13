package servidores;

import utilitarios.UtilGeral;
import comunicacao.mensagens.Mensagem;
import comunicacao.ControladorConexao;
import comunicacao.enums.Acao;
import comunicacao.mensagens.MensagemListaGerenciadores;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import servidores.info.GerenciadorInfo;

/**
 *
 * @created 11/04/2017
 * @author alencar.hentges (CWI Software)
 */
public class GerenciadorPrincipal implements Serializable {

    private static final List<GerenciadorSecundario> GERENCIADORES_SECUNDARIOS = new ArrayList<>();
    private static ServerSocket serverSocket;
    private static ControladorConexao<Mensagem> controladorConexao;
    private static GerenciadorInfo info;
    private static final BufferedReader KEYBOARD_INPUT = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        serverSocket = new ServerSocket(Inconstantes.getProximaPorta());
        info = new GerenciadorInfo("Servidor Principal - Manager", InetAddress.getLocalHost().getHostAddress(), 6789);

        Thread escutar = criaEscuta();
        escutar.start();

        menuServidorPrincipal();

        serverSocket.close();
    }

    // <editor-fold defaultstate="collapsed" desc="Métodos escuta de novas conexões">
    private static Thread criaEscuta() {
        return new Thread(() -> {
            try {
                while (true) {
                    Socket socket = serverSocket.accept();
                    controladorConexao = new ControladorConexao<>(socket);
                    MensagemListaGerenciadores msg = new MensagemListaGerenciadores(Acao.LISTAGEM_GERENCIADORES);
                    msg.setGerenciadoresInfo(montarListaGerenciadores());
                    controladorConexao.enviar(msg);
                    controladorConexao.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(GerenciadorPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private static List<GerenciadorInfo> montarListaGerenciadores() {
        List<GerenciadorInfo> temp = new ArrayList<>();
        GERENCIADORES_SECUNDARIOS.stream().forEach(gerenciador -> {
            temp.add(gerenciador.getInfo());
        });
        return temp;
    }

    // </editor-fold>
    //        
    // <editor-fold defaultstate="collapsed" desc="Menus">
    private static void menuServidorPrincipal() throws IOException {
        String op;
        do {
            op = abrirMenu("P");
            executarAcaoServidorPrincipal(op);
        } while (!op.equals("0"));
    }

    private static void menuServidorSecundario() throws IOException {
        String op;
        do {
            op = abrirMenu("S");
            executarAcaoServidorSecundario(op);
        } while (!op.equals("0"));
    }

    private static void menuServidorArquivos() throws IOException {
        String op;
        do {
            op = abrirMenu("A");
            executarAcaoServidorDeArquivos(op);
        } while (!op.equals("0"));
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Abrir Menus">
    private static String abrirMenu(String tipo) throws IOException {
        switch (tipo) {
            case "P":
                mostrarMenuServidorPrincipal();
                break;
            case "S":
                mostrarMenuServidorSecundario();
                break;
            case "A":
                mostrarMenuServidorDeArquivos();
                break;
        }
        return KEYBOARD_INPUT.readLine();
    }

    // </editor-fold>
    //    
    // <editor-fold defaultstate="collapsed" desc="Mostrar Menus">
    private static void mostrarMenuServidorPrincipal() {
        UtilGeral.limparCMD();
        StringBuilder sb = new StringBuilder();
        sb.append("***** MENU SERVIDOR PRINCIPAL *****\n")
                .append("1 - Servidor Secundário\n")
                .append("2 - Servidor de Arquivos\n")
                .append("3 - Mostrar configurações\n")
                .append("0 - Sair");
        System.out.println(sb);
        System.out.print("Opção: ");
    }

    private static void mostrarMenuServidorSecundario() {
        UtilGeral.limparCMD();
        StringBuilder sb = new StringBuilder();
        sb.append("***** MENU SERVIDOR SECUNDÁRIO *****\n")
                .append("1 - Iniciar novo servidor\n")
                .append("2 - Listar ativos\n")
                .append("3 - Parar servidor\n")
                .append("0 - Voltar");
        System.out.println(sb);
        System.out.print("Opção: ");
    }

    private static void mostrarMenuServidorDeArquivos() {
        UtilGeral.limparCMD();
        StringBuilder sb = new StringBuilder();
        sb.append("***** MENU SERVIDOR DE ARQUIVOS *****\n")
                .append("1 - Iniciar novo servidor\n")
                .append("2 - Listar ativos\n")
                .append("3 - Parar servidor\n")
                .append("0 - Voltar");
        System.out.println(sb);
        System.out.print("Opção: ");
    }

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Executar ação dos menus">
    private static void executarAcaoServidorPrincipal(String op) throws IOException {
        switch (op) {
            case "0":
                System.out.println("Saindo...");
                return;
            case "1":
                menuServidorSecundario();
                return;
            case "2":
                menuServidorArquivos();
                return;
            case "3":
                mostrarConfiguracoes();
                break;
            default:
                System.out.println("Opção Inválida!");
                break;
        }
        UtilGeral.pausar();
    }

    private static void executarAcaoServidorSecundario(String op) throws IOException {
        switch (op) {
            case "0":
                return;
            case "1":
                GERENCIADORES_SECUNDARIOS.add(criarNovoGerenciadorSecundario());
                break;
            case "2":
                printarGerenciadoresSecundariosAtivos();
                break;
            case "3":
                break;
            default:
                System.out.println("Opção Inválida!");
                break;
        }
        UtilGeral.pausar();
    }

    private static void executarAcaoServidorDeArquivos(String op) throws IOException {
        switch (op) {
            case "0":
                return;
            case "1":
                if (GERENCIADORES_SECUNDARIOS.isEmpty()) {
                    System.out.println("Nenhum servidor secundário está ativo. Inicie um para poder continuar...");
                } else {
                    GerenciadorSecundario.addGerenciadorArquivos(criarNovoGerenciadorDeArquivos());
                }
                break;
            case "2":
                printarGerenciadoresDeArquivosAtivos();
                break;
            case "3":
                break;
            default:
                System.out.println("Opção Inválida!");
                break;
        }
        UtilGeral.pausar();
    }

    // </editor-fold>
    //    
    // <editor-fold defaultstate="collapsed" desc="Métodos servidor principal">
    private static void mostrarConfiguracoes() {
        UtilGeral.limparCMD();
        System.out.println("***** CONFIGURAÇÕES SERVIDOR PRINCIPAL *****\n");
        System.out.println(info);
    }

    // </editor-fold>
    //    
    // <editor-fold defaultstate="collapsed" desc="Métodos servidor secundário">
    private static GerenciadorSecundario criarNovoGerenciadorSecundario() throws IOException {
        UtilGeral.limparCMD();
        System.out.println("***** INICIAR NOVO SERVIDOR SECUNDÁRIO *****\n");
        System.out.print("Nome/Apelido do servidor secundário: ");
        String nome = KEYBOARD_INPUT.readLine();
        GerenciadorSecundario gs = new GerenciadorSecundario(Inconstantes.getIdGerenciadorSecundario(), nome, Inconstantes.getProximaPorta());
        System.out.println("\n" + gs.getInfo());
        System.out.println("Servidor inciado com sucesso!");
        return gs;
    }

    private static void printarGerenciadoresSecundariosAtivos() {
        UtilGeral.limparCMD();
        System.out.println("***** SERVIDORES SECUNDÁRIOS *****\n");
        if (GERENCIADORES_SECUNDARIOS.isEmpty()) {
            System.out.println("\nNENHUM SERVIDOR DE GERENCIAMENTO ATIVO! ");
        } else {
            GERENCIADORES_SECUNDARIOS.stream().forEach(System.out::println);
        }
        System.out.println("\n");
    }

    // </editor-fold>
    //    
    // <editor-fold defaultstate="collapsed" desc="Métodos servidor de arquivos">
    private static GerenciadorArquivos criarNovoGerenciadorDeArquivos() throws IOException {
        UtilGeral.limparCMD();
        System.out.println("***** INICIAR NOVO SERVIDOR DE ARQUIVOS *****\n");
        System.out.print("Nome/Apelido do servidor de arquivos: ");
        String nome = KEYBOARD_INPUT.readLine();
        GerenciadorArquivos ga = new GerenciadorArquivos(nome);
        System.out.println("\n" + ga.getInfo());
        System.out.println("Servidor de arquivos iniciado com sucesso!");
        return ga;
    }

    private static void printarGerenciadoresDeArquivosAtivos() {
        UtilGeral.limparCMD();
        System.out.println("***** SERVIDORES DE ARQUIVOS ATIVOS *****\n");
        GerenciadorSecundario.mostrarGerenciadoresDeArquivoAtivos();
        System.out.println("\n");
    }
    // </editor-fold>

}
