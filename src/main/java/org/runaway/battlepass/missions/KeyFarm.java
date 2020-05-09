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
    private String techName;

    @EventHandler
    private void onDropKey(DropKeyEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = Main.gamers.get(player.getUniqueId());

        BattlePass.missions.forEach(weeklyMission -> weeklyMission.getMissions().forEach(mission -> {
            if (mission.getClass().getSimpleName().equals(this.getClass().getSimpleName())) {
                if (!mission.isCompleted(gamer)) {
                    Mine mine = null;
                    KeyFarm kf = (KeyFarm) mission;
                    for (Mine m : Main.mines) {
                        if (m.getMaterial().toString().toLowerCase().equals(kf.techName.toLowerCase())) {
                            mine = m;
                            break;
                        }
                    }
                    if (mine != null && event.fromMine(mine)) kf.addValue(gamer);
                }
            }
        }));
    }

    @Override
    protected void init() {
        this.techName = this.getDescriptionDetails()[1].toString().toLowerCase();

        Main.mines.forEach(mine -> {
            if (mine.getMaterial().toString().toLowerCase().equals(this.techName)) {
                this.mine_name = ChatColor.GRAY + ChatColor.stripColor(Utils.colored(mine.getName().toLowerCase()));
            }
        });
    }

    @Override
    public String getDescription() {
        return "Добудьте ключей на шахте " + this.mine_name;
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
