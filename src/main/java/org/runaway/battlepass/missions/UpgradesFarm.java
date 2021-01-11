package org.runaway.battlepass.missions;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.battlepass.IMission;
import org.runaway.events.custom.UpgradeEvent;

public class UpgradesFarm extends IMission implements Listener {

    @EventHandler
    public void onUpgrade(UpgradeEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = Main.gamers.get(player.getUniqueId());

        addAllValues(gamer);
    }

    @Override
    public String getDescription() {
        return "Улучшайте инструменты";
    }

    @Override
    public String getArgumentsString() {
        return "upgrades_value";
    }

    @Override
    public int getExperience() {
        return 40000;
    }
}