package cn.enderrealm.cumuline.api;

import cn.enderrealm.cumuline.exception.NotBedrockPlayerException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Public entry point for sending Bedrock-specific packets through Floodgate.
 */
public interface CumulineApi {
    /**
     * Looks up the active Cumuline service from Bukkit's service registry.
     *
     * @return the active Cumuline service
     * @throws IllegalStateException if Cumuline is not enabled
     */
    static CumulineApi get() {
        CumulineApi service = Bukkit.getServicesManager().load(CumulineApi.class);
        if (service == null) {
            throw new IllegalStateException("Cumuline is not enabled");
        }
        return service;
    }

    /**
     * Sends a server-provided popup message to a Bedrock player.
     *
     * @param player target player
     * @param message message displayed by the client
     * @throws NotBedrockPlayerException if the target is not a Bedrock player
     */
    void sendPopup(Player player, String message);

    /**
     * Sends a server-provided jukebox popup message to a Bedrock player.
     *
     * @param player target player
     * @param message message displayed by the client
     * @throws NotBedrockPlayerException if the target is not a Bedrock player
     */
    void sendJukeboxPopup(Player player, String message);

    /**
     * Sends a translated jukebox popup and lets the Bedrock client resolve the translation key.
     *
     * @param player target player
     * @param translationKey client translation key
     * @param parameters translation parameters
     * @throws NotBedrockPlayerException if the target is not a Bedrock player
     */
    void sendTranslatedJukeboxPopup(Player player, String translationKey, List<String> parameters);

    /**
     * Sends a toast notification with server-provided title and content to a Bedrock player.
     *
     * <p>Toast packets require a client version whose Bedrock codec defines the toast packet.
     * Older clients are rejected with {@code CumulineException}.</p>
     *
     * @param player target player
     * @param title toast title
     * @param content toast body text
     * @throws NotBedrockPlayerException if the target is not a Bedrock player
     */
    void sendToast(Player player, String title, String content);

    /**
     * Sends a raw Bedrock packet payload through Floodgate's unsafe API.
     *
     * <p>This is an experimental and unsafe API. Invalid payloads may disconnect
     * or crash the Bedrock client.</p>
     *
     * @param player target player
     * @param packetId Bedrock packet ID without transport flags
     * @param payload packet payload without the packet ID
     * @throws NotBedrockPlayerException if the target is not a Bedrock player
     */
    void sendRawPacket(Player player, int packetId, byte[] payload);
}
