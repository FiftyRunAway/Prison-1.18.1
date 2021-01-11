package org.runaway.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.managers.GamerManager;

import java.util.Collections;

/*
 * Created by _RunAway_ on 20.1.2019
 */

public class AutosellCommand extends CommandManager {

    public AutosellCommand() {
        super("autosell", "prison.commands", Collections.singletonList("автопродажа"), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        Gamer gamer = GamerManager.getGamer(p);
        if (gamer.getStatistics(EStat.AUTOSELLDONATE).equals(true) || p.hasPermission("prison.autosell")) {
            if (gamer.getStatistics(EStat.AUTOSELL).equals(true)) {
                gamer.setStatistics(EStat.AUTOSELL, false);
                gamer.sendMessage(EMessage.AUTOSELLDISABLE);
            } else {
                gamer.setStatistics(EStat.AUTOSELL, true);
                gamer.sendMessage(EMessage.AUTOSELLENABLE);
            }
        } else {
            gamer.sendMessage(EMessage.BUYDONATE);
        }
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
    }
}
