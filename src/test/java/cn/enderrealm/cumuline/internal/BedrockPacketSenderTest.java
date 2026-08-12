package cn.enderrealm.cumuline.internal;

import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Verifies Bedrock player validation before raw packet delivery.
 */
class BedrockPacketSenderTest {
    /**
     * Verifies that Java players are rejected before Floodgate's unsafe API is called.
     */
    @Test
    void rejectsJavaPlayer() {
        FloodgateApi floodgateApi = mock(FloodgateApi.class);
        Player player = mock(Player.class);
        when(player.isOnline()).thenReturn(true);
        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player.getName()).thenReturn("JavaPlayer");
        when(floodgateApi.isFloodgatePlayer(player.getUniqueId())).thenReturn(false);

        BedrockPacketSender sender = new BedrockPacketSender(floodgateApi);

        assertThrows(
                cn.enderrealm.cumuline.exception.NotBedrockPlayerException.class,
                () -> sender.send(player, 9, new byte[]{1})
        );
    }

    /**
     * Verifies that invalid packet IDs and empty payloads are rejected.
     */
    @Test
    void rejectsInvalidRawPacket() {
        BedrockPacketSender sender = new BedrockPacketSender(mock(FloodgateApi.class));
        Player player = mock(Player.class);

        assertThrows(IllegalArgumentException.class, () -> sender.send(player, -1, new byte[]{1}));
        assertThrows(IllegalArgumentException.class, () -> sender.send(player, 256, new byte[]{1}));
        assertThrows(IllegalArgumentException.class, () -> sender.send(player, 9, new byte[0]));
    }
}
