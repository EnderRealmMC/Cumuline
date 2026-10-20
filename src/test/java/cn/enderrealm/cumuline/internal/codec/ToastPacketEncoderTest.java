package cn.enderrealm.cumuline.internal.codec;

import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.packet.ToastRequestPacket;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifies Cumuline's ToastRequestPacket encoding contract.
 */
class ToastPacketEncoderTest {
    private final ToastPacketEncoder encoder = new ToastPacketEncoder(new FixedBedrockCodecProvider());
    private final BedrockCodec codec = new FixedBedrockCodecProvider().getCodec();

    /**
     * Verifies that toast payloads decode as toast packets without an embedded packet ID.
     */
    @Test
    void encodesToastPayload() {
        EncodedPacket encoded = encoder.toast("Ender Realm", "Welcome to the server");
        ToastRequestPacket decoded = decode(encoded);

        assertEquals("Ender Realm", decoded.getTitle());
        assertEquals("Welcome to the server", decoded.getContent());
        assertEquals(codec.getPacketDefinition(ToastRequestPacket.class).getId(), encoded.packetId());
        assertNotEquals((byte) encoded.packetId(), encoded.payload()[0]);
    }

    /**
     * Verifies input validation for the toast title and content.
     */
    @Test
    void rejectsInvalidToastArguments() {
        assertThrows(IllegalArgumentException.class, () -> encoder.toast("", "content"));
        assertThrows(IllegalArgumentException.class, () -> encoder.toast("title", ""));
        assertThrows(IllegalArgumentException.class, () -> encoder.toast("title", null));
        assertThrows(IllegalArgumentException.class, () -> encoder.toast(null, "content"));
    }

    /**
     * Decodes a payload using the same codec family used for encoding.
     *
     * @param encoded encoded packet
     * @return decoded packet
     */
    private ToastRequestPacket decode(EncodedPacket encoded) {
        io.netty.buffer.ByteBuf buffer = io.netty.buffer.Unpooled.wrappedBuffer(encoded.payload());
        try {
            return (ToastRequestPacket) codec.tryDecode(
                    codec.createHelper(),
                    buffer,
                    encoded.packetId()
            );
        } finally {
            buffer.release();
        }
    }
}
