package org.runaway.battlepass.missions;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.battlepass.IMission;
import org.runaway.events.custom.PlayerKillEvent;

public class KillsFarm extends IMission implements Listener {

    @EventHandler
    public void onKillPlayer(PlayerKillEvent event) {
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
        return "Совершите несколько убийств";
    }

    @Override
    public String getArgumentsString() {
        return "kills_value";
    }

    @Override
    public int getExperience() {
        return 40000;
    }
}
