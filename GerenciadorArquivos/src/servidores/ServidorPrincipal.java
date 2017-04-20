package servidores;

import comunicacao.ControladorConexao;
import comunicacao.enums.Acao;
import comunicacao.mensagens.Mensagem;
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
import utilitarios.UtilGeral;

/**
 *
 * @author adrisson.silva
 */
public class ServidorPrincipal implements Serializable {

    private static final List<ServidorGerenciamento> SERVIDORES_GERENCIADORES = new ArrayList<>();
    private static ServerSocket serverSocket;
    private static ControladorConexao<Mensagem> controladorConexao;
    private static GerenciadorInfo info;
    private static final BufferedReader KEYBOARD_INPUT = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        serverSocket = new ServerSocket(Contadores.getProximaPorta());
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
                Logger.getLogger(ServidorPrincipal.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private static List<GerenciadorInfo> montarListaGerenciadores() {
        List<GerenciadorInfo> temp = new ArrayList<>();
        SERVIDORES_GERENCIADORES.stream().forEach(gerenciador -> {
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

    private static void menuServidorGerenciador() throws IOException {
        String op;
        do {
            op = abrirMenu("S");
            executarAcaoServidorGerenciamento(op);
        } while (!op.equals("0"));
    }

    private static void menuServidorArquivos() throws IOException {
        String op;
        do {
            op = abrirMenu("A");
            executarAcaoServidorArquivos(op);
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
                mostrarMenuServidorDeGerenciamento();
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
                .append("1 - Servidor de Gerenciamento\n")
                .append("2 - Servidor de Arquivos\n")
                .append("3 - Mostrar configurações\n")
                .append("0 - Sair");
        System.out.println(sb);
        System.out.print("Opção: ");
    }

    private static void mostrarMenuServidorDeGerenciamento() {
        UtilGeral.limparCMD();
        StringBuilder sb = new StringBuilder();
        sb.append("***** MENU SERVIDOR DE GERENCIAMENTO *****\n")
                .append("1 - Iniciar novo servidor\n")
                .append("2 - Listar ativos\n")
                //.append("3 - Parar servidor\n")
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
                //.append("3 - Parar servidor\n")
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
                menuServidorGerenciador();
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

    private static void executarAcaoServidorGerenciamento(String op) throws IOException {
        switch (op) {
            case "0":
                return;
            case "1":
                SERVIDORES_GERENCIADORES.add(criarNovoServidorGerenciador());
                break;
            case "2":
                printarServidoresGerenciadoresAtivos();
                break;
            case "3":
                break;
            default:
                System.out.println("Opção Inválida!");
                break;
        }
        UtilGeral.pausar();
    }

    private static void executarAcaoServidorArquivos(String op) throws IOException {
        switch (op) {
            case "0":
                return;
            case "1":
                if (SERVIDORES_GERENCIADORES.isEmpty()) {
                    System.out.println("Nenhum servidor secundário está ativo. Inicie um para poder continuar...");
                } else {
                    ServidorGerenciamento.addGerenciadorArquivos(criarNovoGerenciadorDeArquivos());
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
    private static ServidorGerenciamento criarNovoServidorGerenciador() throws IOException {
        UtilGeral.limparCMD();
        System.out.println("***** INICIAR NOVO SERVIDOR GERENCIADOR *****\n");
        System.out.print("Nome/Apelido do servidor gerenciador: ");
        String nome = KEYBOARD_INPUT.readLine();
        ServidorGerenciamento gs = new ServidorGerenciamento(Contadores.getIdGerenciadorGerenciamento(), nome, Contadores.getProximaPorta());
        System.out.println("\n" + gs.getInfo());
        System.out.println("Servidor iniciado com sucesso!");
        return gs;
    }

    private static void printarServidoresGerenciadoresAtivos() {
        UtilGeral.limparCMD();
        System.out.println("***** SERVIDORES GERENCIADORES *****\n");
        if (SERVIDORES_GERENCIADORES.isEmpty()) {
            System.out.println("\nNENHUM SERVIDOR DE GERENCIAMENTO ATIVO! ");
        } else {
            SERVIDORES_GERENCIADORES.stream().forEach(System.out::println);
        }
    }

    // </editor-fold>
    //    
    // <editor-fold defaultstate="collapsed" desc="Métodos servidor de arquivos">
    private static ServidorArquivos criarNovoGerenciadorDeArquivos() throws IOException {
        UtilGeral.limparCMD();
        System.out.println("***** INICIAR NOVO SERVIDOR DE ARQUIVOS *****\n");
        System.out.print("Nome/Apelido do servidor de arquivos: ");
        String nome = KEYBOARD_INPUT.readLine();
        ServidorArquivos ga = new ServidorArquivos(nome);
        System.out.println("\n" + ga.getInfo());
        System.out.println("Servidor de arquivos iniciado com sucesso!");
        return ga;
    }

    private static void printarGerenciadoresDeArquivosAtivos() {
        UtilGeral.limparCMD();
        System.out.println("***** SERVIDORES DE ARQUIVOS ATIVOS *****\n");
        ServidorGerenciamento.mostrarGerenciadoresDeArquivoAtivos();
        System.out.println("\n");
    }
    // </editor-fold>

}
