package org.runaway.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.inventories.FishSellMenu;

import java.util.Collections;

public class FisherCommand extends CommandManager {

    public FisherCommand() {
        super("fisher", "prison.admin", Collections.singletonList("prisonfisher"), true);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {

    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
        if (args.length != 1) {
            cs.sendMessage(ChatColor.RED + "Use: /" + cmdName + " <player>");
            return;
        }
        Player player = Bukkit.getPlayer(args[0]);
        FishSellMenu.openMenu(player);
    }
}
