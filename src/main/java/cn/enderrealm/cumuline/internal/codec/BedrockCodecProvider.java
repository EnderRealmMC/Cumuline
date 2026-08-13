package cn.enderrealm.cumuline.internal.codec;

import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;

/**
 * Supplies the Bedrock codec used to encode packets before Floodgate forwards them.
 */
public interface BedrockCodecProvider {
    /**
     * Returns the Bedrock codec matching the target client version.
     *
     * @param clientVersion client version, or {@code null} for a provider default
     * @return matching codec
     */
    BedrockCodec getCodec(String clientVersion);

    /**
     * Returns the provider default codec for compatibility with fixed providers.
     *
     * @return default codec
     */
    default BedrockCodec getCodec() {
        return getCodec(null);
    }
}
