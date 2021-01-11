package org.runaway.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.AutoRestart;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Utils;
import org.runaway.enums.EMessage;

import java.util.Collections;

/*
 * Created by _RunAway_ on 21.4.2019
 */

public class RestartCommand extends CommandManager {

    public RestartCommand() {
        super("torestart", "prison.commands", Collections.singletonList("рестарт"), true);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        Gamer gamer = GamerManager.getGamer(p);
        if (Main.isAutoRestart) {
            gamer.sendMessage(Utils.colored("&aДо перезагрузки сервера &e" + (int)(AutoRestart.getTime() / 60) + " ч, " + (int)(AutoRestart.getTime() % 60) + " мин."));
        } else {
            gamer.sendMessage(EMessage.DISFUNCTION);
        }
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
        if (Main.isAutoRestart) {
            Bukkit.getConsoleSender().sendMessage(Utils.colored("&aTo restart - &e" + (int)(AutoRestart.getTime() / 60) + " h, " + (int)(AutoRestart.getTime() % 60) + " min."));
        } else {
            Bukkit.getConsoleSender().sendMessage("This function disabled!");
        }
    }
}
