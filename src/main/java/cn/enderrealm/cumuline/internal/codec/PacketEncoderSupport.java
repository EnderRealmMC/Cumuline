package cn.enderrealm.cumuline.internal.codec;

import cn.enderrealm.cumuline.exception.CumulineException;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.BedrockPacketDefinition;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.util.Objects;

/**
 * Shared encoding helpers used by the packet encoders in this package.
 */
public final class PacketEncoderSupport {
    /**
     * Encodes a packet and extracts the payload without the packet ID.
     *
     * @param codecProvider codec provider
     * @param clientVersion target client version
     * @param packet packet to encode
     * @return encoded packet
     */
    public static EncodedPacket encode(BedrockCodecProvider codecProvider, String clientVersion, BedrockPacket packet) {
        String packetName = packet.getClass().getSimpleName();
        try {
            BedrockCodec codec = Objects.requireNonNull(codecProvider.getCodec(clientVersion), "codec");
            BedrockPacketDefinition<?> definition = codec.getPacketDefinition(packet.getClass());
            if (definition == null) {
                throw new CumulineException("The configured Bedrock codec does not support " + packetName);
            }

            ByteBuf buffer = Unpooled.buffer();
            try {
                codec.tryEncode(codec.createHelper(), buffer, packet);
                return new EncodedPacket(definition.getId(), ByteBufUtil.getBytes(buffer));
            } finally {
                buffer.release();
            }
        } catch (CumulineException exception) {
            throw exception;
        } catch (RuntimeException | LinkageError exception) {
            throw new CumulineException("Failed to encode Bedrock " + packetName, exception);
        }
    }

    /**
     * Validates text fields before handing them to a protocol serializer.
     *
     * @param value text value
     * @param fieldName field name
     */
    public static void validateText(String value, String fieldName) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be null or empty");
        }
    }
}
