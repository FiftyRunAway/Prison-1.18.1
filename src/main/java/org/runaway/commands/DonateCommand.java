package org.runaway.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.inventories.DonateMenu;

import java.util.Collections;

public class DonateCommand extends CommandManager {

    public DonateCommand() {
        super("donate", "prison.commands", Collections.singletonList("донат"), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        new DonateMenu(p);
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
    }
}
