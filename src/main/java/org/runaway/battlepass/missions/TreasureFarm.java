package org.runaway.battlepass.missions;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.runaway.Gamer;
import org.runaway.battlepass.IMission;
import org.runaway.events.custom.TreasureFindEvent;
import org.runaway.managers.GamerManager;

public class TreasureFarm extends IMission implements Listener {

    @EventHandler
    public void onFindTreasure(TreasureFindEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = GamerManager.getGamer(player);
        addAllValues(gamer);
    }

    @Override
    public String getDescription() {
        return ChatColor.GRAY + "Найдите несколько кладов в шахтах";
    }

    @Override
    public String getArgumentsString() {
        return "treasure_value";
    }

    @Override
    public int getExperience() {
        return 40000;
    }
}
