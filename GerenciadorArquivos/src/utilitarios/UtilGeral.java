package utilitarios;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @created 12/04/2017
 * @author alencar.hentges (CWI Software)
 */
public class UtilGeral {

    private static final BufferedReader KEYBOARD_INPUT = new BufferedReader(new InputStreamReader(System.in));

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
}
