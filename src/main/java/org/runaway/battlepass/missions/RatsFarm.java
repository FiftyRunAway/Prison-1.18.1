package org.runaway.battlepass.missions;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.battlepass.BattlePass;
import org.runaway.battlepass.IMission;
import org.runaway.enums.TypeMessage;
import org.runaway.events.custom.KillRatsEvent;
import org.runaway.utils.Vars;

public class RatsFarm extends IMission implements Listener {

    private boolean isRare;

    @EventHandler
    private void onRatKill(KillRatsEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = Main.gamers.get(player.getUniqueId());

        BattlePass.missions.forEach(weeklyMission -> {
            if (!weeklyMission.isStarted()) return;
            weeklyMission.getMissions().forEach(mission -> {
                if (mission.getClass().getSimpleName().equals(this.getClass().getSimpleName())) {
                    if (!mission.isCompleted(gamer)) {
                        RatsFarm rf = (RatsFarm) mission;
                        if (!rf.isRare || event.isRare())
                            rf.addValue(gamer);
                    }
                }
            });
        });

        addAllValues(gamer);
    }

    @Override
    public void init() {
        try {
            this.isRare = Boolean.getBoolean(this.getDescriptionDetails()[1].toString().toLowerCase());
        } catch (Exception ex) { Vars.sendSystemMessage(TypeMessage.ERROR, "Error with kill rats event"); }
    }

    @Override
    public String getDescription() {
        return "Убивайте " + (this.isRare ? "редких" : "") + "крыс";
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
