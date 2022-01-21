package org.runaway.inventories;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.runaway.items.ItemManager;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

public class RunesListMenu implements IMenus {

    public static StandardMenu getMenu(Player player) {
        StandardMenu menu = StandardMenu.create(6, "&eСписок рун");

        int i = 0;
        for (Rune rune : RuneManager.runes) {
            ItemStack r = ItemManager.getPrisonItem(rune.getTechName() + "Rune").getItemStack();
            IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(r).setSlot(i++);
            btn.setClickEvent(event -> event.getWhoClicked().getInventory().addItem(r));
            menu.addButton(btn);
        }
        return menu;
    }

    @Override
    public int getRows() {
        return 6;
    }

    @Override
    public String getName() {
        return "&eСписок рун";
    }
}
