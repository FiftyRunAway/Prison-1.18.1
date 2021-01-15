package org.runaway.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.enums.EMessage;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Utils;

import java.util.Arrays;
import java.util.Collections;

public class InvseeCommand extends CommandManager {

    public InvseeCommand() {
        super("invsee", "prison.commands", Collections.singletonList("checkinv"), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        Gamer gamer = GamerManager.getGamer(p);
        if(!p.hasPermission("prison.admin")) {
            gamer.sendMessage(EMessage.NOPERM);
            return;
        }
        if(args.length == 0) {
            gamer.sendMessage("&cВведите ник!");
            return;
        }
        String targetName = args[0];
        Player target = Bukkit.getPlayerExact(targetName);
        if(target == null) {
            gamer.sendMessage("&cИгрок оффлайн!");
            return;
        }
        if(args.length == 1) {
            p.openInventory(target.getInventory());
        } else if(args.length == 2 && args[1].equalsIgnoreCase("ec")) {
            p.openInventory(target.getEnderChest());
        }
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
    }
}