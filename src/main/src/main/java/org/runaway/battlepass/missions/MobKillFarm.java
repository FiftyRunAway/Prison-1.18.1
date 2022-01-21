package org.runaway.battlepass.missions;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.runaway.Gamer;
import org.runaway.battlepass.BattlePass;
import org.runaway.battlepass.IMission;
import org.runaway.entity.Attributable;
import org.runaway.entity.MobManager;
import org.runaway.events.custom.MobDeathEvent;
import org.runaway.managers.GamerManager;

import java.util.Locale;

public class MobKillFarm extends IMission implements Listener {
    private String description;
    private String mobTechName;

    @EventHandler
    public void onMobDeath(MobDeathEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = GamerManager.getGamer(player);

        BattlePass.missions.forEach(weeklyMission -> {
            if (!weeklyMission.isStarted()) return;
            weeklyMission.getMissions().forEach(mission -> {
                if (mission.getClass().isAssignableFrom(this.getClass()) && !mission.isCompleted(gamer)) {
                    MobKillFarm mkf = (MobKillFarm) mission;
                    if (mkf.mobTechName.equalsIgnoreCase(event.getController().getAttributable().getTechName())) {
                        mkf.addValue(gamer, 1);
                    }
                }
            });
        });
    }

    @Override
    public void init() {
        this.mobTechName = this.getDescriptionDetails()[1].toString().toLowerCase(Locale.ROOT);
        Attributable attributable = MobManager.getAttributable(this.mobTechName);
        String name = ChatColor.RESET + attributable.getName().toLowerCase(Locale.ROOT);
        this.description = ChatColor.GRAY + "Убейте " + ChatColor.YELLOW + name + ChatColor.GRAY;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getArgumentsString() {
        return "mobKills_value mobTechName";
    }

    @Override
    public int getExperience() {
        return 40000;
    }
}
