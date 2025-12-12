package exceptions;

/**
 * Exception personnalisée pour gérer les erreurs liées au chiffrement/déchiffrement
 */
public class CryptoException extends Exception {

    /**
     * Constructeur avec message personnalisé
     */
    public CryptoException(String message) {
        super(message);
    }

    /**
     * Constructeur avec message et cause pour enchaîner les exceptions
     */
    public CryptoException(String message, Throwable cause) {
        super(message, cause);
    }
}
