package org.runaway.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.enums.EMessage;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Utils;

import java.util.Collections;

public class HideCommand extends CommandManager {

    private static Prison plugin = Prison.getInstance();

    public HideCommand() {
        super("hide", "prison.commands", Collections.singletonList("скрыть"), false);
    }

    @Override
    public void runCommand(Player p, String[] args, String cmdName) {
        Gamer gamer = GamerManager.getGamer(p);
        if (gamer.isHideEnabled()) {
            Utils.getPlayers().forEach(player ->
                    showPlayer(p, Bukkit.getPlayerExact(player)));
            gamer.getHiddenPlayers().clear();
            gamer.sendTitle(" ", Utils.colored(EMessage.SHOWGAMERS.getMessage()));
        } else {
            Utils.getPlayers().forEach(player -> {
                hidePlayer(p, Bukkit.getPlayerExact(player));
                gamer.getHiddenPlayers().add(player);
            });
            gamer.sendTitle(" ", Utils.colored(EMessage.HIDEGAMERS.getMessage()));
        }
    }

    @Override
    public void runConsoleCommand(CommandSender cs, String[] args, String cmdName) {
    }

    public static void hideOnJoin(Gamer gamer) {
        Utils.getPlayers().forEach(player -> {
            Gamer g = GamerManager.getGamer(player);
            if (g.isHideEnabled()) {
                hidePlayer(g.getPlayer(), gamer.getPlayer());
                g.getHiddenPlayers().add(gamer.getName());
            }
        });
    }

    public static void removeOnQuit(Gamer gamer) {
        Utils.getPlayers().forEach(player -> {
            Gamer g = GamerManager.getGamer(player);
            if (g.isHideEnabled()) {
                showPlayer(g.getPlayer(), gamer.getPlayer());
                g.getHiddenPlayers().remove(gamer.getName());
            }
        });
    }

    public static void hidePlayer(Player arg0, Player arg1) {
        try {
            arg0.hidePlayer(plugin, arg1);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static void showPlayer(Player arg0, Player arg1) {
        try {
            arg0.showPlayer(plugin, arg1);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
