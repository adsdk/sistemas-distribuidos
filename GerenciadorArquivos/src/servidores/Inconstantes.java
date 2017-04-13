package servidores;

/**
 *
 * @created 12/04/2017
 * @author alencar.hentges (CWI Software)
 */
public class Inconstantes {

    private static Long idGerenciadorSecundario = new Long(1);
    private static int proximaPorta = 6789;

    public static synchronized Long getIdGerenciadorSecundario() {
        return idGerenciadorSecundario++;
    }

    public static synchronized int getProximaPorta() {
        return proximaPorta++;
    }

}
