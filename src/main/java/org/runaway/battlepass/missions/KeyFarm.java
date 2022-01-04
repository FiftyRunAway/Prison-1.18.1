package org.runaway.battlepass.missions;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.runaway.Gamer;
import org.runaway.battlepass.BattlePass;
import org.runaway.battlepass.IMission;
import org.runaway.events.custom.DropKeyEvent;
import org.runaway.managers.GamerManager;
import org.runaway.mines.Mine;
import org.runaway.utils.Utils;

public class KeyFarm extends IMission implements Listener {

    private String mineName;
    private Mine mine;
    private String description;

    @EventHandler
    private void onDropKey(DropKeyEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = GamerManager.getGamer(player);

        BattlePass.missions.forEach(weeklyMission -> {
            if (!weeklyMission.isStarted()) return;
            weeklyMission.getMissions().forEach(mission -> {
                if (mission.getClass().isAssignableFrom(this.getClass()) && !mission.isCompleted(gamer)) {
                    KeyFarm kf = (KeyFarm) mission;
                    if (kf.mineName != null) {
                        if (event.fromMine(kf.mine)) kf.addValue(gamer, 1);
                    } else {
                        kf.addValue(gamer, 1);
                    }
                }
            });
        });
    }

    // Get mine_name from main material of mine that set in Config.yml
    @Override
    public void init() {
        this.mine = getMineString(this.getDescriptionDetails()[1].toString());
        if (this.mine != null) this.mineName = Utils.colored(this.mine.getName());
        this.description = ChatColor.GRAY + "Добывайте ключи на " + (this.mine == null ? "любой шахте" : ("шахте '" + this.mineName + ChatColor.GRAY + "'"));
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getArgumentsString() {
        return "keys_value mine_name";
    }

    @Override
    public int getExperience() {
        return 40000;
    }
}
