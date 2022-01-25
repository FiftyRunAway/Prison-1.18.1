package org.runaway.nametag;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.runaway.donate.Privs;

import java.util.List;

public class Teams {

    public static Scoreboard sb;

    private static List<Team> privs;

    public static void load(Player player) {
        Privs privs = Privs.DEFAULT.getPrivilege(player);
        privs.getTeam().addPlayer(player);
    }
}
