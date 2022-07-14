package org.runaway.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.items.ItemManager;
import org.runaway.jobs.job.Mover;
import org.runaway.managers.GamerManager;

public class BlockPlace implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Gamer gamer = GamerManager.getGamer(event.getPlayer());

        ItemStack main = event.getItemInHand();
        if (main.equals(ItemManager.getPrisonItem("boxItem").getItemStack())) {
            if(!gamer.isEndedCooldown("boxCd")) {
                return;
            }
            gamer.addCooldown("boxCd", 300);
            Mover.placeBoxListener(event);
        }
    }
}
