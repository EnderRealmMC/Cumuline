package cn.enderrealm.cumuline.exception;

/**
 * Indicates that a packet was requested for a player who is not connected through Floodgate.
 */
public final class NotBedrockPlayerException extends CumulineException {
    /**
     * Creates an exception for the given player name.
     *
     * @param playerName player name
     */
    public NotBedrockPlayerException(String playerName) {
        super("Player " + playerName + " is not a Bedrock player");
    }
}
