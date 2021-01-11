package org.runaway.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.enums.EConfig;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.inventories.LevelMenu;
import org.runaway.managers.GamerManager;

import java.util.Collections;

/*
 * Created by _RunAway_ on 26.1.2019
 */

public class LevelCommand extends CommandManager {

    public LevelCommand() {
        super("level", "prison.commands", Collections.singletonList("lvl"), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        Gamer gamer = GamerManager.getGamer(p);
        if (EConfig.CONFIG.getConfig().contains("levels." + (gamer.getIntStatistics(EStat.LEVEL) + 1)) || Gamer.toRebirth == (gamer.getIntStatistics(EStat.LEVEL) + 1)) {
            if (gamer.needRebirth()) {
                gamer.rebirth();
                return;
            }
            new LevelMenu(p);
        } else {
            gamer.sendMessage(EMessage.MAXLEVEL);
        }
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
    }
}
