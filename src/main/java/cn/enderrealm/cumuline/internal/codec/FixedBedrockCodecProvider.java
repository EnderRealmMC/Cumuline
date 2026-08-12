package cn.enderrealm.cumuline.internal.codec;

import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v898.Bedrock_v898;

/**
 * Provides the fixed codec used by the first Cumuline release.
 *
 * <p>Cloudburst codec v898 targets Bedrock 1.21.130.</p>
 */
public final class FixedBedrockCodecProvider implements BedrockCodecProvider {
    /**
     * Returns the fixed Bedrock 1.21.130 codec.
     *
     * @return fixed codec
     */
    @Override
    public BedrockCodec getCodec() {
        return Bedrock_v898.CODEC;
    }
}
