package org.runaway.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.utils.Utils;

import java.util.Collections;

/*
 * Created by _RunAway_ on 13.5.2019
 */

public class RebirthCommand extends CommandManager {

    public RebirthCommand() {
        super("rebirth", "prison.commands", Collections.singletonList("престиж"), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        Gamer gamer = Main.gamers.get(p.getUniqueId());
        if (gamer.needRebirth()) {
            gamer.rebirth();
            return;
        }
        gamer.getPlayer().sendMessage(Utils.colored("&cРановато..."));
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
    }
}
