package cn.enderrealm.cumuline.exception;

/**
 * Indicates that Floodgate rejected or failed to send a packet.
 */
public final class PacketSendException extends CumulineException {
    /**
     * Creates an exception with a message and cause.
     *
     * @param message failure message
     * @param cause underlying failure
     */
    public PacketSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
