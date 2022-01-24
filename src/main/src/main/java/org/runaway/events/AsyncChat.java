package org.runaway.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Utils;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.enums.FactionType;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/*
 * Created by _RunAway_ on 25.1.2019
 */

public class AsyncChat implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();
        Gamer gamer = GamerManager.getGamer(player);
        String message = event.getMessage();
        if ((message.contains("&") || message.contains("|")) && !player.hasPermission("prison.moder")) {
            gamer.sendMessage(EMessage.AMPERSAND);
            return;
        }
        if(gamer.getChatConsumer() != null) {
            gamer.getChatConsumer().accept(player, message);
            gamer.setChatConsumer(null);
            event.setCancelled(true);
            return;
        }
        String faction = "";
        String rebirth = "";
        String prefix = "";
        if (!gamer.getFaction().equals(FactionType.DEFAULT)) faction = Utils.colored("&7[" + gamer.getFaction().getColor() + gamer.getFaction().getName() + "&7] &f");
        if (gamer.getIntStatistics(EStat.REBIRTH) > 0) rebirth = ChatColor.YELLOW + gamer.getDisplayRebirth() + " " + ChatColor.GRAY;
        String level = Utils.colored("&7[" + gamer.getLevelColor() + "" + gamer.getDisplayLevel() + "&7] ");
        if (Prison.usePermissionsEx) prefix = Utils.colored(PermissionsEx.getUser(player.getPlayer().getName()).getPrefix());

        boolean start = message.startsWith("!");
        if (start || !message.startsWith("@")) {
            if (start && message.length() == 1) {
                gamer.sendMessage(EMessage.NOMESSAGE);
                return;
            }
            String local = start ? ChatColor.BLUE + "G " : ChatColor.GREEN + "L ";

            String format = local + level + rebirth + prefix + player.getName() + " " + faction + ChatColor.GRAY + "> " + ChatColor.GRAY;
            Bukkit.getConsoleSender().sendMessage(format + message.replaceFirst("!", ""));
            if (!start) {
                event.getRecipients().forEach(players -> {
                    if (Boolean.TRUE.equals(inLocal(player, players))) {
                        send(GamerManager.getGamer(players.getUniqueId()), format + message);
                    }
                });
            } else {
                event.getRecipients().forEach(players ->
                        send(GamerManager.getGamer(players.getUniqueId()), format + message.replaceFirst("!", "")));
            }
            return;
        }
        if (gamer.getFaction().equals(FactionType.DEFAULT)) {
            gamer.sendMessage(EMessage.NOFACTION);
            event.setCancelled(true);
            return;
        }
        if (message.length() == 1) {
            gamer.sendMessage(EMessage.NOMESSAGE);
            return;
        }
        final String format = gamer.getFaction().getColor() + gamer.getFaction().getName() + ChatColor.GRAY + " | " + prefix + player.getName() + ChatColor.GRAY + " > " + ChatColor.BLUE;
        Bukkit.getConsoleSender().sendMessage(format + message.replace("@", ""));
        event.getRecipients().forEach(players -> {
            Gamer g = GamerManager.getGamer(players.getUniqueId());
            if (gamer.getFaction().equals(g.getFaction()) || players.hasPermission("prison.spy")) {
                send(g, format + message.replace("@", ""));
            }
        });
    }

    private void send(Gamer consumer, String format) {
        consumer.getPlayer().sendMessage(Utils.colored(format));
    }

    private Boolean inLocal(Player sender, Player receiver) {
        return receiver.hasPermission("prison.spy") ||
                (sender.getLocation().getWorld().equals(receiver.getLocation().getWorld()) &&
                        (sender.getName().equals(receiver.getName()) ||
                                sender.getLocation().distance(receiver.getLocation()) <= 100.0));
    }
}
