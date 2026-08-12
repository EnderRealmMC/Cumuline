package cn.enderrealm.cumuline.internal;

import cn.enderrealm.cumuline.exception.NotBedrockPlayerException;
import cn.enderrealm.cumuline.exception.PacketSendException;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.Arrays;
import java.util.Objects;

/**
 * Validates Bedrock players and forwards encoded packets to Floodgate.
 */
public final class BedrockPacketSender {
    private final FloodgateApi floodgateApi;

    /**
     * Creates a sender backed by the given Floodgate API.
     *
     * @param floodgateApi Floodgate API instance
     */
    public BedrockPacketSender(FloodgateApi floodgateApi) {
        this.floodgateApi = Objects.requireNonNull(floodgateApi, "floodgateApi");
    }

    /**
     * Sends a packet after checking that the target is a Floodgate player.
     *
     * @param player target player
     * @param packetId packet ID
     * @param payload payload without packet ID
     */
    public void send(Player player, int packetId, byte[] payload) {
        Objects.requireNonNull(player, "player");
        validatePacket(packetId, payload);

        if (!player.isOnline() || !floodgateApi.isFloodgatePlayer(player.getUniqueId())) {
            throw new NotBedrockPlayerException(player.getName());
        }

        try {
            floodgateApi.unsafe().sendPacket(player.getUniqueId(), packetId, Arrays.copyOf(payload, payload.length));
        } catch (RuntimeException | LinkageError exception) {
            throw new PacketSendException("Floodgate failed to send packet " + packetId, exception);
        }
    }

    /**
     * Validates a raw packet's transport fields.
     *
     * @param packetId packet ID
     * @param payload payload
     */
    private static void validatePacket(int packetId, byte[] payload) {
        if (packetId < 0 || packetId > 255) {
            throw new IllegalArgumentException("packetId must be between 0 and 255");
        }
        if (payload == null || payload.length == 0) {
            throw new IllegalArgumentException("payload must not be null or empty");
        }
    }
}
