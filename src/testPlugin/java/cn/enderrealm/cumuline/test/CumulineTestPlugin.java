package cn.enderrealm.cumuline.test;

import cn.enderrealm.cumuline.api.CumulineApi;
import cn.enderrealm.cumuline.exception.CumulineException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

/**
 * Manual test plugin for observing Cumuline packets in a real Bedrock client.
 */
public final class CumulineTestPlugin extends JavaPlugin implements CommandExecutor {
    /**
     * Registers the test command.
     */
    @Override
    public void onEnable() {
        if (getCommand("cumulinetest") != null) {
            getCommand("cumulinetest").setExecutor(this);
        }
    }

    /**
     * Handles the popup and jukebox popup test commands.
     *
     * @param sender command sender
     * @param command command
     * @param label command label
     * @param args command arguments
     * @return whether the command was handled
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by a player.");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("Usage: /cumulinetest popup <message>");
            player.sendMessage("Usage: /cumulinetest jukebox-raw <message>");
            player.sendMessage("Usage: /cumulinetest jukebox <translation-key> [parameters...]");
            return true;
        }

        try {
            CumulineApi api = CumulineApi.get();
            switch (args[0].toLowerCase()) {
                case "popup" -> api.sendPopup(player, joinArguments(args, 1));
                case "jukebox-raw" -> api.sendJukeboxPopup(player, joinArguments(args, 1));
                case "jukebox" -> api.sendTranslatedJukeboxPopup(
                        player,
                        args[1],
                        Arrays.asList(args).subList(2, args.length)
                );
                default -> {
                    player.sendMessage("Unknown test type.");
                    return true;
                }
            }
            player.sendMessage("Packet sent.");
        } catch (CumulineException | IllegalArgumentException exception) {
            player.sendMessage("Packet was not sent: " + exception.getMessage());
        }
        return true;
    }

    /**
     * Joins command arguments into a message.
     *
     * @param args command arguments
     * @param start first argument to include
     * @return joined message
     */
    private static String joinArguments(String[] args, int start) {
        return String.join(" ", Arrays.copyOfRange(args, start, args.length));
    }
}
