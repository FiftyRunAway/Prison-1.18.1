package org.runaway.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.inventories.TrainerMenu;
import org.runaway.utils.Utils;

import java.util.Collections;

/*
 * Created by _RunAway_ on 13.5.2019
 */

public class TrainerCommand extends CommandManager {

    public TrainerCommand() {
        super("trainer", "prison.commands", Collections.singletonList("тренер"), true);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        new TrainerMenu(p);
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
                new TrainerMenu(player);
            } else {
                cs.sendMessage(ChatColor.RED + "Player is not online");
            }
        } else {
            cs.sendMessage(ChatColor.RED + "Use: /" + cmdName + " <player>");
        }
    }
}
