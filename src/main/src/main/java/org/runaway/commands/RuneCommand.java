package org.runaway.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.inventories.RuneMenu;
import org.runaway.inventories.RunesListMenu;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Utils;

import java.util.Collections;

public class RuneCommand extends CommandManager {

    public RuneCommand() {
        super("rune", "prison.console", Collections.singletonList("runes"), true);
    }


    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        if (p.hasPermission("prison.admin")) {
            RunesListMenu.getMenu(p).open(GamerManager.getGamer(p));
        }
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
                RuneMenu.getMenu(player).open(GamerManager.getGamer(player));
            } else {
                cs.sendMessage(ChatColor.RED + "Player is not online");
            }
        } else {
            cs.sendMessage(ChatColor.RED + "Use: /" + cmdName + " <player>");
        }
    }
}
