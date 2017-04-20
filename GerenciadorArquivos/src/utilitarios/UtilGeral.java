package utilitarios;

import comunicacao.mensagens.Mensagem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import servidores.pojo.Usuario;

/**
 *
 * @author adrisson.silva
 */
public class UtilGeral {

    private static final BufferedReader KEYBOARD_INPUT = new BufferedReader(new InputStreamReader(System.in));
    public static final Random RANDOM_GENERATOR = new Random();

    public static void limparCMD() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (IOException | InterruptedException ex) {
        }
    }

    public static void pausar() throws IOException {
        System.out.println("\n\nPressione 'Enter' para continuar...");
        KEYBOARD_INPUT.readLine();
    }
    
    public static String gerarNomeAleatorio() {
        int numeroPalavras = showRandomInteger(5, 10);
        final StringBuilder nomeAleatorio = new StringBuilder();
        for (int i = 0; i < numeroPalavras; i++) {
            if (showRandomInteger(5, 10) % 2 == 0) {
                nomeAleatorio.append(((char) showRandomInteger(65, 90)));
            } else {
                nomeAleatorio.append(((char) showRandomInteger(97, 119)));
            }
        }
        return nomeAleatorio.toString();
    }
    
    /**
     * Código copiado de http://www.javapractices.com/topic/TopicAction.do?Id=62
     * Generate random numbers > Example 2
     *
     * @param aStart
     * @param aEnd
     * @return
     */
    public static int showRandomInteger(int aStart, int aEnd) {
        if (aStart > aEnd) {
            throw new IllegalArgumentException("Start cannot exceed End.");
        }
        //get the range, casting to long to avoid overflow problems
        long range = (long) aEnd - (long) aStart + 1;
        // compute a fraction of the range, 0 <= frac < range
        long fraction = (long) (range * RANDOM_GENERATOR.nextDouble());
        int randomNumber = (int) (fraction + aStart);
        return randomNumber;
    }
    
    public static void printarEnvioInfo(Usuario usuario, Mensagem msg) {
        System.out.println("Servidor enviando(#" + usuario.getNomeUsuario() + "): " + msg);
    }

    public static void printarRecebimentoInfo(Usuario usuario, Mensagem msg) {
        System.out.println("Servidor recebeu(#" + usuario.getNomeUsuario() + "): " + msg);
    }
}
