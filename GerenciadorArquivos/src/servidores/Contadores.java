package servidores;

/**
 *
 * @author adrisson.silva
 */
public class Contadores {

    private static Long idGerenciadorGerenciamento = new Long(1);
    private static int proximaPorta = 6789;

    public static synchronized Long getIdGerenciadorGerenciamento() {
        return idGerenciadorGerenciamento++;
    }

    public static synchronized int getProximaPorta() {
        return proximaPorta++;
    }

}
