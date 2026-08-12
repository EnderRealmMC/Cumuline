package cn.enderrealm.cumuline.internal.codec;

import cn.enderrealm.cumuline.exception.CumulineException;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;

/**
 * Supplies the Bedrock codec used to encode packets before Floodgate forwards them.
 */
public interface BedrockCodecProvider {
    /**
     * Returns the configured Bedrock codec.
     *
     * @return configured codec
     * @throws CumulineException if no codec is available
     */
    BedrockCodec getCodec();
}
