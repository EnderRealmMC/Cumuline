package cn.enderrealm.cumuline.exception;

/**
 * Base exception for expected Cumuline failures.
 */
public class CumulineException extends RuntimeException {
    /**
     * Creates an exception with a message.
     *
     * @param message failure message
     */
    public CumulineException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a message and cause.
     *
     * @param message failure message
     * @param cause underlying failure
     */
    public CumulineException(String message, Throwable cause) {
        super(message, cause);
    }
}
