package org.runaway.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.inventories.AchievementsMenu;

import java.util.Arrays;

public class AchievementsCommand extends CommandManager {

    public AchievementsCommand() {
        super("achievements", "prison.commands", Arrays.asList("достижения", "achievs"), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        new AchievementsMenu(p);
        //new UpItemsMenu(p);
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {

    }
}
