package org.runaway.battlepass.missions;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.battlepass.BattlePass;
import org.runaway.battlepass.IMission;
import org.runaway.events.custom.DropKeyEvent;
import org.runaway.mines.Mine;
import org.runaway.utils.Utils;

public class KeyFarm extends IMission implements Listener {

    private String mine_name;
    private Mine mine;

    @EventHandler
    private void onDropKey(DropKeyEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = Main.gamers.get(player.getUniqueId());

        BattlePass.missions.forEach(weeklyMission -> {
            if (!weeklyMission.isStarted()) return;
            weeklyMission.getMissions().forEach(mission -> {
                if (mission.getClass().getSimpleName().equals(this.getClass().getSimpleName())) {
                    if (!mission.isCompleted(gamer)) {
                        KeyFarm kf = (KeyFarm) mission;
                        if (kf.mine_name != null) {
                            if (event.fromMine(kf.mine)) kf.addValue(gamer);
                        } else {
                            kf.addValue(gamer);
                        }
                    }
                }
            });
        });
    }

    // Get mine_name from main material of mine which set in config.yml
    @Override
    public void init() {
        this.mine = getMineString(this.getDescriptionDetails()[1].toString());
        if (this.mine != null) this.mine_name = ChatColor.GRAY + ChatColor.stripColor(Utils.colored(Utils.upCurLetter(this.mine.getName(), 1)));
    }

    @Override
    public String getDescription() {
        return "Добудьте ключей на " + (this.mine == null ? "любой шахте" : this.mine_name);
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
