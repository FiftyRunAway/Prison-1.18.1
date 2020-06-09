package org.runaway.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.enums.EStat;
import org.runaway.inventories.Confirmation;
import org.runaway.inventories.ModeMenu;

/*
 * Created by _RunAway_ on 10.2.2019
 */

public class PlayerInventoryClose implements Listener {

    @EventHandler
    public void onPlayerInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            Gamer gamer = Main.gamers.get(player.getUniqueId());
            if (event.getInventory().getName().equalsIgnoreCase(ChatColor.YELLOW + "Выбор сложности") && gamer.getStatistics(EStat.MODE).equals("default")) {
                new ModeMenu();
            }
            Confirmation.inMenu.remove(player.getName());
        }
    }
}
