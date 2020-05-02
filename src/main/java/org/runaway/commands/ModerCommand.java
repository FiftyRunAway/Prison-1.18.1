package org.runaway.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class ModerCommand extends CommandManager {

    public ModerCommand() {
        super("check", "prison.moderation", Arrays.asList("проверка"), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {

    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {

    }
}
