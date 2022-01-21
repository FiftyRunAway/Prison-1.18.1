package org.runaway.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.inventories.MainMenu;

import java.util.Arrays;

public class ProfileCommand extends CommandManager {

    public ProfileCommand() {
        super("profile", "prison.commands", Arrays.asList("профиль"), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        new MainMenu(p);
        //new UpItemsMenu(p);
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
    }
}
