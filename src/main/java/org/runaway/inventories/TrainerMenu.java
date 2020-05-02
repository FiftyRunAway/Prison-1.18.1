package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.runaway.utils.Utils;
import org.runaway.menu.type.StandardMenu;
import org.runaway.trainer.Trainer;

import java.util.concurrent.atomic.AtomicInteger;

/*
 * Created by _RunAway_ on 13.5.2019
 */

public class TrainerMenu implements IMenus {

    public TrainerMenu(Player player) {
        StandardMenu menu = StandardMenu.create(getRows(), getName());
        AtomicInteger i = new AtomicInteger(1);
        Utils.trainer.forEach(trainer -> {
            menu.addButton(((Trainer) trainer).getMenuIcon(player).setSlot(i.getAndIncrement()));
            i.incrementAndGet();
        });
        player.openInventory(menu.build());
    }

    @Override
    public int getRows() {
        return 1;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Тренер";
    }
}
