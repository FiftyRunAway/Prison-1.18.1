package org.runaway.quests;

import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.enums.EStat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DailyStreak {

    private ArrayList<String> quests;

    public DailyStreak(Player player) {
        Gamer gamer = Main.gamers.get(player.getUniqueId());
        quests = new ArrayList<>();
        List<String> str = Arrays.asList(gamer.getStatistics(EStat.DAILYSTREAK).toString().split(" "));
        quests.addAll(str);
        int streak = Math.abs(str.size() - 7);
    }

    private boolean canGet() {
        return true;
    }
}
