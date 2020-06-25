package org.runaway.battlepass.missions;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.battlepass.IMission;
import org.runaway.events.custom.PlayerDamageEvent;

public class DamageFarm extends IMission implements Listener {

    @EventHandler
    private void onDamagePlayer(PlayerDamageEvent event) {
        Player player = event.getPlayerDamaged();
        Gamer gamer = Main.gamers.get(player.getUniqueId());

        addAllValues(gamer);
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
