package org.runaway.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.inventories.ShopMenu;
import org.runaway.utils.Utils;

import java.util.Collections;

public class ShopCommand extends CommandManager {

    public ShopCommand() {
        super("shop", "prison.commands", Collections.singletonList("магазин"), true);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        new ShopMenu(p);
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
                new ShopMenu(player);
            } else {
                cs.sendMessage(ChatColor.RED + "Player is not online");
            }
        } else {
            cs.sendMessage(ChatColor.RED + "Use: /" + cmdName + " <player>");
        }
    }
}
