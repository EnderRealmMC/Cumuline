package cn.enderrealm.cumuline.internal.codec;

import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.packet.TextPacket;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifies Cumuline's TextPacket encoding contract.
 */
class TextPacketEncoderTest {
    private final TextPacketEncoder encoder = new TextPacketEncoder(new FixedBedrockCodecProvider());
    private final BedrockCodec codec = new FixedBedrockCodecProvider().getCodec();

    /**
     * Verifies that popup payloads decode as popup packets without an embedded packet ID.
     */
    @Test
    void encodesPopupPayload() {
        EncodedPacket encoded = encoder.popup("Hello Bedrock");
        TextPacket decoded = decode(encoded);

        assertEquals(TextPacket.Type.POPUP, decoded.getType());
        assertEquals("Hello Bedrock", decoded.getMessage());
        assertEquals(codec.getPacketDefinition(TextPacket.class).getId(), encoded.packetId());
        assertNotEquals((byte) encoded.packetId(), encoded.payload()[0]);
    }

    /**
     * Verifies that raw jukebox popup text remains server-provided text.
     */
    @Test
    void encodesRawJukeboxPopup() {
        EncodedPacket encoded = encoder.jukeboxPopup("Now playing");
        TextPacket decoded = decode(encoded);

        assertEquals(TextPacket.Type.JUKEBOX_POPUP, decoded.getType());
        assertEquals("Now playing", decoded.getMessage());
        assertEquals(List.of(), decoded.getParameters());
    }

    /**
     * Verifies that translated jukebox popups preserve the key and parameters.
     */
    @Test
    void encodesTranslatedJukeboxPopup() {
        EncodedPacket encoded = encoder.translatedJukeboxPopup("record.nowPlaying", List.of("Artist", "Track"));
        TextPacket decoded = decode(encoded);

        assertEquals(TextPacket.Type.JUKEBOX_POPUP, decoded.getType());
        assertEquals("record.nowPlaying", decoded.getMessage());
        assertEquals(List.of("Artist", "Track"), decoded.getParameters());
        assertEquals(true, decoded.isNeedsTranslation());
    }

    /**
     * Verifies input validation for text and translation parameters.
     */
    @Test
    void rejectsInvalidTextArguments() {
        assertThrows(IllegalArgumentException.class, () -> encoder.popup(""));
        assertThrows(IllegalArgumentException.class, () -> encoder.translatedJukeboxPopup("", List.of()));
        assertThrows(IllegalArgumentException.class, () -> encoder.translatedJukeboxPopup(
                "key",
                java.util.Arrays.asList("ok", null)
        ));
    }

    /**
     * Decodes a payload using the same codec family used for encoding.
     *
     * @param encoded encoded packet
     * @return decoded packet
     */
    private TextPacket decode(EncodedPacket encoded) {
        io.netty.buffer.ByteBuf buffer = io.netty.buffer.Unpooled.wrappedBuffer(encoded.payload());
        try {
            return (TextPacket) codec.tryDecode(
                    codec.createHelper(),
                    buffer,
                    encoded.packetId()
            );
        } finally {
            buffer.release();
        }
    }
}
