package cn.enderrealm.cumuline.internal.codec;

import cn.enderrealm.cumuline.exception.CumulineException;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.BedrockPacketDefinition;
import org.cloudburstmc.protocol.bedrock.packet.TextPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.util.List;
import java.util.Objects;

/**
 * Encodes Bedrock text packets with Cloudburst Protocol.
 */
public final class TextPacketEncoder {
    private final BedrockCodecProvider codecProvider;

    /**
     * Creates an encoder with the given codec provider.
     *
     * @param codecProvider codec provider
     */
    public TextPacketEncoder(BedrockCodecProvider codecProvider) {
        this.codecProvider = Objects.requireNonNull(codecProvider, "codecProvider");
    }

    /**
     * Encodes a popup message.
     *
     * @param message message text
     * @return encoded packet
     */
    public EncodedPacket popup(String message) {
        return popup(null, message);
    }

    /**
     * Encodes a popup message using the codec selected for a client version.
     *
     * @param clientVersion target client version
     * @param message message text
     * @return encoded packet
     */
    public EncodedPacket popup(String clientVersion, String message) {
        validateText(message, "message");

        TextPacket packet = basePacket(TextPacket.Type.POPUP, message);
        return encode(clientVersion, packet);
    }

    /**
     * Encodes a raw jukebox popup message.
     *
     * @param message message text
     * @return encoded packet
     */
    public EncodedPacket jukeboxPopup(String message) {
        return jukeboxPopup(null, message);
    }

    /**
     * Encodes a raw jukebox popup using the codec selected for a client version.
     *
     * @param clientVersion target client version
     * @param message message text
     * @return encoded packet
     */
    public EncodedPacket jukeboxPopup(String clientVersion, String message) {
        validateText(message, "message");

        TextPacket packet = basePacket(TextPacket.Type.JUKEBOX_POPUP, message);
        return encode(clientVersion, packet);
    }

    /**
     * Encodes a client-translated jukebox popup.
     *
     * @param translationKey client translation key
     * @param parameters translation parameters
     * @return encoded packet
     */
    public EncodedPacket translatedJukeboxPopup(String translationKey, List<String> parameters) {
        return translatedJukeboxPopup(null, translationKey, parameters);
    }

    /**
     * Encodes a client-translated jukebox popup using a version-specific codec.
     *
     * @param clientVersion target client version
     * @param translationKey client translation key
     * @param parameters translation parameters
     * @return encoded packet
     */
    public EncodedPacket translatedJukeboxPopup(String clientVersion, String translationKey, List<String> parameters) {
        validateText(translationKey, "translationKey");
        Objects.requireNonNull(parameters, "parameters");
        if (parameters.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("parameters must not contain null values");
        }

        TextPacket packet = basePacket(TextPacket.Type.JUKEBOX_POPUP, translationKey);
        packet.setNeedsTranslation(true);
        packet.setParameters(List.copyOf(parameters));
        return encode(clientVersion, packet);
    }

    /**
     * Creates the common fields required by a server-to-client text packet.
     *
     * @param type packet text type
     * @param message packet message
     * @return initialized packet
     */
    private static TextPacket basePacket(TextPacket.Type type, String message) {
        TextPacket packet = new TextPacket();
        packet.setType(type);
        packet.setMessage(message);
        packet.setNeedsTranslation(false);
        packet.setXuid("");
        packet.setPlatformChatId("");
        return packet;
    }

    /**
     * Encodes a packet and extracts the payload without the packet ID.
     *
     * @param packet packet to encode
     * @return encoded packet
     */
    private EncodedPacket encode(String clientVersion, TextPacket packet) {
        try {
            BedrockCodec codec = Objects.requireNonNull(codecProvider.getCodec(clientVersion), "codec");
            BedrockPacketDefinition<TextPacket> definition = codec.getPacketDefinition(TextPacket.class);
            if (definition == null) {
                throw new CumulineException("The configured Bedrock codec does not support TextPacket");
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
            throw new CumulineException("Failed to encode Bedrock TextPacket", exception);
        }
    }

    /**
     * Validates text fields before handing them to a protocol serializer.
     *
     * @param value text value
     * @param fieldName field name
     */
    private static void validateText(String value, String fieldName) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be null or empty");
        }
    }
}
