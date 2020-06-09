package org.runaway.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.runaway.events.custom.PlayerFishingEvent;

public class PlayerFishing implements Listener {

    @EventHandler
    public void onPlayerFishing(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
            Bukkit.getServer().getPluginManager().callEvent(new PlayerFishingEvent(player, event.getCaught()));
        }
    }
}
