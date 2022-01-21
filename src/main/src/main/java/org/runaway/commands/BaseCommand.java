package org.runaway.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.managers.GamerManager;

import java.util.Collections;

public class BaseCommand extends CommandManager {

    public BaseCommand() {
        super("base", "prison.commands", Collections.singletonList("база"), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        Gamer gamer = GamerManager.getGamer(p);
        gamer.teleportBase();
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {

    }
}
