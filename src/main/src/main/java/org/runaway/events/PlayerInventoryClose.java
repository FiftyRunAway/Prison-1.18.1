package org.runaway.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.runaway.Gamer;
import org.runaway.inventories.Confirmation;
import org.runaway.managers.GamerManager;
import org.runaway.tasks.Cancellable;

/*
 * Created by _RunAway_ on 10.2.2019
 */

public class PlayerInventoryClose implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            Gamer gamer = GamerManager.getGamer(player);
            if (gamer == null) return;
            if (gamer.getOpenedRunesMenu() != null &&
                    gamer.getOpenedRunesMenu().getButton(17) != null) {
                gamer.getPlayer().getInventory().addItem(gamer.getOpenedRunesMenu().getButton(17).getItem());
                gamer.sendMessage("&aВы завершили работы у мастера рун!");
                gamer.setOpenedRunesMenu(null);
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
            }
        }
    }
}
