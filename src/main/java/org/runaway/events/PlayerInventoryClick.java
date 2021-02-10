package org.runaway.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.runaway.Gamer;
import org.runaway.managers.GamerManager;

/*
 * Created by _RunAway_ on 23.1.2019
 */

public class PlayerInventoryClick implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Gamer gamer = GamerManager.getGamer(player);
        if(gamer.getCurrentIMenu() != null) {
            if(gamer.getCurrentIMenu().isCancelClickEvent()) {
                event.setCancelled(true);
            }
        }
    }
}
