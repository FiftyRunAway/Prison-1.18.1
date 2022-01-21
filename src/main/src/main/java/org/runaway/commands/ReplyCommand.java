package org.runaway.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Utils;

import java.util.Arrays;
import java.util.Collections;

public class ReplyCommand extends CommandManager {
    public ReplyCommand() {
        super("r", "prison.commands", Collections.singletonList("reply"), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        Gamer gamer = GamerManager.getGamer(p);
        if(gamer.getReplyPlayer() == null) {
            gamer.sendMessage("&cНекому отвечать.");
            return;
        }
        String targetName = gamer.getReplyPlayer();
        Player target = Bukkit.getPlayerExact(targetName);
        if(target == null) {
            gamer.sendMessage("&cИгрок оффлайн!");
            return;
        }
        if(args.length == 0) {
            gamer.sendMessage("&cВведите сообщение!");
            return;
        }
        Gamer targetGamer = GamerManager.getGamer(target);
        targetGamer.setReplyPlayer(p.getName());
        gamer.setReplyPlayer(targetName);
        String message = Utils.combine(args, 0);
        targetGamer.sendMessage("&6" + p.getName() + " -> ВЫ: &7" + message);
        gamer.sendMessage("&6ВЫ -> " + target.getName() + ": &7" + message);
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
    }
}