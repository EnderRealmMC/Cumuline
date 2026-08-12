package cn.enderrealm.cumuline.internal;

import cn.enderrealm.cumuline.api.CumulineApi;
import cn.enderrealm.cumuline.internal.codec.BedrockCodecProvider;
import cn.enderrealm.cumuline.internal.codec.EncodedPacket;
import cn.enderrealm.cumuline.internal.codec.FixedBedrockCodecProvider;
import cn.enderrealm.cumuline.internal.codec.TextPacketEncoder;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.List;

/**
 * Default Bukkit service implementation for Cumuline.
 */
public final class CumulineProvider implements CumulineApi {
    private final BedrockPacketSender packetSender;
    private final TextPacketEncoder textPacketEncoder;

    /**
     * Creates the default provider using the fixed Bedrock codec.
     */
    public CumulineProvider() {
        this(FloodgateApi.getInstance(), new FixedBedrockCodecProvider());
    }

    /**
     * Creates a provider with injectable dependencies.
     *
     * @param floodgateApi Floodgate API instance
     * @param codecProvider Bedrock codec provider
     */
    CumulineProvider(FloodgateApi floodgateApi, BedrockCodecProvider codecProvider) {
        packetSender = new BedrockPacketSender(floodgateApi);
        textPacketEncoder = new TextPacketEncoder(codecProvider);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendPopup(Player player, String message) {
        EncodedPacket packet = textPacketEncoder.popup(message);
        packetSender.send(player, packet.packetId(), packet.payload());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendJukeboxPopup(Player player, String message) {
        EncodedPacket packet = textPacketEncoder.jukeboxPopup(message);
        packetSender.send(player, packet.packetId(), packet.payload());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendTranslatedJukeboxPopup(Player player, String translationKey, List<String> parameters) {
        EncodedPacket packet = textPacketEncoder.translatedJukeboxPopup(translationKey, parameters);
        packetSender.send(player, packet.packetId(), packet.payload());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendRawPacket(Player player, int packetId, byte[] payload) {
        packetSender.send(player, packetId, payload);
    }
}
