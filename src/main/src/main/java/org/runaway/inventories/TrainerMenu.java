package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.runaway.enums.EButtons;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.utils.ItemBuilder;
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
            menu.addButton(trainer.getMenuIcon(player).setSlot(i.getAndIncrement()));
            i.incrementAndGet();
        });
        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new ItemBuilder(EButtons.CANCEL.getItemStack()).build()).setSlot(8);
        back.setClickEvent(event -> new MainMenu(event.getWhoClicked()));
        menu.addButton(back);
        menu.open(GamerManager.getGamer(player));
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
