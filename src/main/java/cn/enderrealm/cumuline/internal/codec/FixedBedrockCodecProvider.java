package cn.enderrealm.cumuline.internal.codec;

import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v1001.Bedrock_v1001;

/**
 * Provides the fixed codec used by the first Cumuline release.
 *
 * <p>Cloudburst codec v1001 targets Bedrock 1.26.30 through 1.26.33.</p>
 */
public final class FixedBedrockCodecProvider implements BedrockCodecProvider {
    /**
     * Returns the fixed Bedrock 1.26.30 through 1.26.33 codec.
     *
     * @return fixed codec
     */
    @Override
    public BedrockCodec getCodec(String clientVersion) {
        Thread currentThread = Thread.currentThread();
        ClassLoader previousClassLoader = currentThread.getContextClassLoader();
        ClassLoader cumulineClassLoader = FixedBedrockCodecProvider.class.getClassLoader();
        try {
            // Paper's server thread context loader may not expose plugin resources to ServiceLoader.
            currentThread.setContextClassLoader(cumulineClassLoader);
            return Bedrock_v1001.CODEC;
        } finally {
            currentThread.setContextClassLoader(previousClassLoader);
        }
    }
}
