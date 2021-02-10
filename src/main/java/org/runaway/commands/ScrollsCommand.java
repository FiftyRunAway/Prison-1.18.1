package org.runaway.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Utils;
import org.runaway.scrolls.ScrollShop;

import java.util.Collections;

public class ScrollsCommand extends CommandManager {

    public ScrollsCommand() {
        super("scrollshop", "prison.console", Collections.singletonList("scrolls"), true);
    }


    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
        if (args.length == 1) {
            if (!Utils.getPlayers().contains(args[0])) {
                cs.sendMessage(ChatColor.RED + "Use: /" + cmdName + " <player>");
                return;
            }
            Player player = Bukkit.getPlayer(args[0]);
            if (player != null) {
                ScrollShop.getMenu(player).open(GamerManager.getGamer(player));

            } else {
                cs.sendMessage(ChatColor.RED + "Player is not online");
            }
        } else {
            cs.sendMessage(ChatColor.RED + "Use: /" + cmdName + " <player>");
        }
    }
}
