package cn.enderrealm.cumuline.internal.codec;

import cn.enderrealm.cumuline.exception.CumulineException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests dynamic Cloudburst codec resolution.
 */
class CloudburstBedrockCodecProviderTest {
    /**
     * Verifies that a mapped client version resolves to its protocol codec.
     */
    @Test
    void resolvesMappedCodec() {
        CloudburstBedrockCodecProvider provider = new CloudburstBedrockCodecProvider();

        assertEquals(1001, provider.getCodec("1.26.31").getProtocolVersion());
        assertEquals(2168, provider.getCodec("1.26.41").getProtocolVersion());
        assertEquals(2168, provider.getCodec("1.26.44").getProtocolVersion());
    }

    /**
     * Verifies that unknown versions are rejected without a fallback codec.
     */
    @Test
    void rejectsUnknownVersionWithoutFallback() {
        CloudburstBedrockCodecProvider provider = new CloudburstBedrockCodecProvider();

        assertThrows(CumulineException.class, () -> provider.getCodec("1.21.999"));
    }
}
