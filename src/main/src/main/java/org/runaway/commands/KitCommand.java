package org.runaway.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.inventories.KitsMenu;
import org.runaway.managers.GamerManager;

import java.util.Collections;

public class KitCommand extends CommandManager {

    public KitCommand() {
        super("pkit", "prison.commands", Collections.singletonList("pkits"), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        Gamer gamer = GamerManager.getGamer(p);
        KitsMenu.getMenu(gamer).open(gamer);
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) { }
}
