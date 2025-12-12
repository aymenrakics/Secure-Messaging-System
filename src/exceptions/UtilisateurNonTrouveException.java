package exceptions;

/**
 * Exception personnalisée levée quand un utilisateur recherché n'existe pas
 */
public class UtilisateurNonTrouveException extends Exception {

    /**
     * Constructeur avec message personnalisé
     */
    public UtilisateurNonTrouveException(String message) {
        super(message);
    }

    /**
     * Constructeur avec message et cause
     */
    public UtilisateurNonTrouveException(String message, Throwable cause) {
        super(message, cause);
    }
}

