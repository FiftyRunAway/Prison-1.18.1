package org.runaway.battlepass.missions;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.battlepass.BattlePass;
import org.runaway.battlepass.IMission;
import org.runaway.events.custom.PlayerDamageEvent;
import org.runaway.managers.GamerManager;

public class DamageFarm extends IMission implements Listener {

    @EventHandler
    private void onDamagePlayer(PlayerDamageEvent event) {
        Player player = event.getPlayerDamaged();
        Gamer gamer = GamerManager.getGamer(player);
        double damage = event.getPlayerSource().getLastDamage();

        BattlePass.missions.forEach(weeklyMission -> {
            if (!weeklyMission.isStarted()) return;
            weeklyMission.getMissions().forEach(mission -> {
                if (mission.getClass().getSimpleName().equals(this.getClass().getSimpleName())) {
                    if (!mission.isCompleted(gamer)) {
                        DamageFarm df = (DamageFarm) mission;
                        df.addValue(gamer);
                    }
                }
            });
        });
    }

    @Override
    public void addValue(Gamer gamer) {
        int damage = (int)Math.round(gamer.getPlayer().getLastDamage());
        int result = (int)getValues().get(gamer.getGamer() + damage);
        getValues().put(gamer.getGamer(), result);

        checkLevel(gamer);
    }

    @Override
    public String getDescription() {
        return "Наносите урон игрокам";
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
