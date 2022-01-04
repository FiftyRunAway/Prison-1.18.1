package org.runaway.battlepass.missions;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.runaway.Gamer;
import org.runaway.battlepass.BattlePass;
import org.runaway.battlepass.IMission;
import org.runaway.events.custom.BossDamageEvent;
import org.runaway.managers.GamerManager;

public class BossDamageFarm extends IMission implements Listener {

    @EventHandler
    public void onBossDamage(BossDamageEvent event) {
        Player player = event.getSource();
        Gamer gamer = GamerManager.getGamer(player);
        int value = (int) Math.round(event.getDamage());

        BattlePass.missions.forEach(weeklyMission -> {
            if (!weeklyMission.isStarted()) return;
            weeklyMission.getMissions().forEach(mission -> {
                if (mission.getClass().isAssignableFrom(this.getClass()) && !mission.isCompleted(gamer)) {
                    BossDamageFarm bdf = (BossDamageFarm) mission;
                    bdf.addValue(gamer, value);
                }
            });
        });
    }


    @Override
    public String getDescription() {
        return ChatColor.GRAY + "Наносите урон боссам";
    }

    @Override
    public String getArgumentsString() {
        return "damage_value";
    }

    @Override
    public int getExperience() {
        return 40000;
    }
}
