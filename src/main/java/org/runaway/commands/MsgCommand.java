package org.runaway.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.enums.EConfig;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.inventories.LevelMenu;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Utils;

import java.util.Arrays;

public class MsgCommand extends CommandManager {
    public MsgCommand() {
        super("msg", "prison.commands", Arrays.asList("m", "tell"), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        Gamer gamer = GamerManager.getGamer(p);
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
            gamer.sendMessage("&cВведите сообщение!");
            return;
        }
        Gamer targetGamer = GamerManager.getGamer(target);
        targetGamer.setReplyPlayer(p.getName());
        gamer.setReplyPlayer(targetName);
        String message = Utils.combine(args, 1);
        targetGamer.sendMessage("&6" + p.getName() + " -> ВЫ: &7" + message);
        gamer.sendMessage("&6ВЫ -> " + target.getName() + ": &7" + message);
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
    }
}
