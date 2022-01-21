package org.runaway.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.inventories.AchievementsMenu;
import org.runaway.runes.armor.FreezeRune;
import org.runaway.runes.utils.RuneManager;

import java.util.Arrays;

public class AchievementsCommand extends CommandManager {

    public AchievementsCommand() {
        super("achievements", "prison.commands", Arrays.asList("достижения", "achievs"), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        new AchievementsMenu(p);

        p.getInventory().setItemInMainHand(RuneManager.addRune(p.getInventory().getItemInMainHand(), new FreezeRune()));
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {

    }
}
