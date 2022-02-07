package org.runaway.nametag;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.runaway.utils.Utils;
import org.runaway.utils.color.ColorAPI;

import java.util.List;

public class Teams {

    public static Scoreboard sb;

    public static void load(Player player) {
        player.setPlayerListHeader(Utils.colored("Добро пожаловать! \n") + ColorAPI.process("<SOLID:0275fd>Aquantix"));
        player.setPlayerListFooter(ColorAPI.process("<SOLID:0275fd>IP: <SOLID:85C1E9>mc.aquantix.su"));
    }
}
