package cn.enderrealm.cumuline.internal.codec;

import org.cloudburstmc.protocol.bedrock.packet.ToastRequestPacket;

import java.util.Objects;

/**
 * Encodes Bedrock toast request packets with Cloudburst Protocol.
 */
public final class ToastPacketEncoder {
    private final BedrockCodecProvider codecProvider;

    /**
     * Creates an encoder with the given codec provider.
     *
     * @param codecProvider codec provider
     */
    public ToastPacketEncoder(BedrockCodecProvider codecProvider) {
        this.codecProvider = Objects.requireNonNull(codecProvider, "codecProvider");
    }

    /**
     * Encodes a toast notification.
     *
     * @param title toast title
     * @param content toast body text
     * @return encoded packet
     */
    public EncodedPacket toast(String title, String content) {
        return toast(null, title, content);
    }

    /**
     * Encodes a toast notification using the codec selected for a client version.
     *
     * @param clientVersion target client version
     * @param title toast title
     * @param content toast body text
     * @return encoded packet
     */
    public EncodedPacket toast(String clientVersion, String title, String content) {
        PacketEncoderSupport.validateText(title, "title");
        PacketEncoderSupport.validateText(content, "content");

        ToastRequestPacket packet = new ToastRequestPacket();
        packet.setTitle(title);
        packet.setContent(content);
        return PacketEncoderSupport.encode(codecProvider, clientVersion, packet);
    }
}
