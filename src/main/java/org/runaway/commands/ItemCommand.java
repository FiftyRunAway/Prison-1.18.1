package org.runaway.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.enums.EMessage;
import org.runaway.managers.GamerManager;

import java.util.Collections;

public class ItemCommand extends CommandManager {

    public ItemCommand() {
        super("item", "prison.admin", Collections.singletonList("getitem"), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        Gamer gamer = GamerManager.getGamer(p);
        if(!p.hasPermission("prison.admin")) {
            gamer.sendMessage(EMessage.NOPERM);
            return;
        }
        p.getInventory().addItem(Prison.getInstance().getItemManager().getPrisonItem(args[0]).getItemStack());
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
    }
}