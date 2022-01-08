package org.runaway.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.runaway.Gamer;
import org.runaway.enums.EStat;
import org.runaway.inventories.Confirmation;
import org.runaway.inventories.ModeMenu;
import org.runaway.managers.GamerManager;
import org.runaway.tasks.Cancellable;

/*
 * Created by _RunAway_ on 10.2.2019
 */

public class PlayerInventoryClose implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            Gamer gamer = GamerManager.getGamer(player);
            if (event.getInventory().getName().equalsIgnoreCase(ChatColor.YELLOW + "Выбор сложности") && gamer.getStatistics(EStat.MODE).equals("default")) {
                new ModeMenu();
            }
            if(gamer.getCurrentIMenu() != null) {
                gamer.setCurrentIMenu(null);
            }
            if(Confirmation.inMenu != null && Confirmation.inMenu.containsKey(player.getName())) {
                Confirmation.inMenu.remove(player.getName());
            }
            if (!gamer.getUpdatingButtons().isEmpty()) {
                gamer.getUpdatingButtons().forEach(Cancellable::stop);
                gamer.getUpdatingButtons().clear();
                Bukkit.getConsoleSender().sendMessage("cleared");
            }
        }
    }
}
