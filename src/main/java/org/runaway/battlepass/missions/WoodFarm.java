package org.runaway.battlepass.missions;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.battlepass.IMission;
import org.runaway.events.custom.BreakWoodEvent;

public class WoodFarm extends IMission implements Listener {

    @EventHandler
    private void onBreakWood(BreakWoodEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = Main.gamers.get(player.getUniqueId());

        addAllValues(gamer);
    }

    @Override
    protected void init() {
        // None
    }

    @Override
    public String getDescription() {
        return "Добудьте дерева в лесу";
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
