package org.runaway.battlepass.missions;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.runaway.Gamer;
import org.runaway.battlepass.BattlePass;
import org.runaway.battlepass.IMission;
import org.runaway.events.custom.BreakWoodEvent;
import org.runaway.managers.GamerManager;

public class WoodFarm extends IMission implements Listener {

    @EventHandler
    private void onBreakWood(BreakWoodEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = GamerManager.getGamer(player);
        int value = Math.toIntExact(Math.round(gamer.getBoosterBlocks()));
        BattlePass.missions.forEach(weeklyMission -> {
            if (!weeklyMission.isStarted()) return;
            weeklyMission.getMissions().forEach(mission -> {
                if (mission.getClass().isAssignableFrom(this.getClass()) && !mission.isCompleted(gamer)) {
                    WoodFarm bf = (WoodFarm) mission;
                    bf.addValue(gamer, value);
                }
            });
        });
    }

    @Override
    public String getDescription() {
        return ChatColor.GRAY + "Нарубите дерева в лесу";
    }

    @Override
    public String getArgumentsString() {
        return "wood_value";
    }

    @Override
    public int getExperience() {
        return 40000;
    }
}
