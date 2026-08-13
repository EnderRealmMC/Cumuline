package cn.enderrealm.cumuline.internal;

import cn.enderrealm.cumuline.api.CumulineApi;
import cn.enderrealm.cumuline.exception.CumulineException;
import cn.enderrealm.cumuline.exception.NotBedrockPlayerException;
import cn.enderrealm.cumuline.internal.codec.BedrockCodecProvider;
import cn.enderrealm.cumuline.internal.codec.CloudburstBedrockCodecProvider;
import cn.enderrealm.cumuline.internal.codec.EncodedPacket;
import cn.enderrealm.cumuline.internal.codec.TextPacketEncoder;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.List;
import java.util.Objects;

/**
 * Default Bukkit service implementation for Cumuline.
 */
public final class CumulineProvider implements CumulineApi {
    private final FloodgateApi floodgateApi;
    private final BedrockPacketSender packetSender;
    private final TextPacketEncoder textPacketEncoder;

    /**
     * Creates the default provider using the fixed Bedrock codec.
     */
    public CumulineProvider() {
        this(FloodgateApi.getInstance(), new CloudburstBedrockCodecProvider());
    }

    /**
     * Creates a provider with injectable dependencies.
     *
     * @param floodgateApi Floodgate API instance
     * @param codecProvider Bedrock codec provider
     */
    CumulineProvider(FloodgateApi floodgateApi, BedrockCodecProvider codecProvider) {
        this.floodgateApi = Objects.requireNonNull(floodgateApi, "floodgateApi");
        packetSender = new BedrockPacketSender(floodgateApi);
        textPacketEncoder = new TextPacketEncoder(codecProvider);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendPopup(Player player, String message) {
        EncodedPacket packet = textPacketEncoder.popup(getClientVersion(player), message);
        packetSender.send(player, packet.packetId(), packet.payload());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendJukeboxPopup(Player player, String message) {
        EncodedPacket packet = textPacketEncoder.jukeboxPopup(getClientVersion(player), message);
        packetSender.send(player, packet.packetId(), packet.payload());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendTranslatedJukeboxPopup(Player player, String translationKey, List<String> parameters) {
        EncodedPacket packet = textPacketEncoder.translatedJukeboxPopup(
                getClientVersion(player), translationKey, parameters);
        packetSender.send(player, packet.packetId(), packet.payload());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendRawPacket(Player player, int packetId, byte[] payload) {
        packetSender.send(player, packetId, payload);
    }

    /**
     * Reads and validates the Floodgate client version for a target player.
     *
     * @param player target player
     * @return Floodgate client version
     */
    private String getClientVersion(Player player) {
        Objects.requireNonNull(player, "player");
        if (!player.isOnline() || !floodgateApi.isFloodgatePlayer(player.getUniqueId())) {
            throw new NotBedrockPlayerException(player.getName());
        }
        FloodgatePlayer floodgatePlayer = floodgateApi.getPlayer(player.getUniqueId());
        if (floodgatePlayer == null || floodgatePlayer.getVersion() == null
                || floodgatePlayer.getVersion().isBlank()) {
            throw new CumulineException("Floodgate did not provide a Bedrock client version for " + player.getName());
        }
        return floodgatePlayer.getVersion();
    }
}
