package org.runaway.battlepass.missions;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.runaway.Gamer;
import org.runaway.battlepass.BattlePass;
import org.runaway.battlepass.IMission;
import org.runaway.enums.TypeMessage;
import org.runaway.events.custom.KillRatsEvent;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Vars;

public class RatsFarm extends IMission implements Listener {

    private boolean isRare;
    private String description;

    @EventHandler
    private void onRatKill(KillRatsEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = GamerManager.getGamer(player);

        BattlePass.missions.forEach(weeklyMission -> {
            if (!weeklyMission.isStarted()) return;
            weeklyMission.getMissions().forEach(mission -> {
                if (mission.getClass().isAssignableFrom(this.getClass())) {
                    if (!mission.isCompleted(gamer)) {
                        RatsFarm rf = (RatsFarm) mission;
                        if (!rf.isRare || event.isRare())
                            rf.addValue(gamer, 1);
                    }
                }
            });
        });
    }

    @Override
    public void init() {
        try {
            this.isRare = Boolean.parseBoolean(this.getDescriptionDetails()[1].toString().toLowerCase());
            this.description = ChatColor.GRAY + "Убивайте " + (this.isRare ? ChatColor.YELLOW +  "редких " : "") + ChatColor.GRAY + "крыс";
        } catch (Exception ex) { Vars.sendSystemMessage(TypeMessage.ERROR, "Error with kill rats event"); }
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getArgumentsString() {
        return "rats_value isRare_boolean";
    }

    @Override
    public int getExperience() {
        return 40000;
    }
}
