package org.runaway.menu.button;

import org.bukkit.inventory.ItemStack;

public class MenuButton extends IMenuButton {

    public MenuButton(ItemStack item, int slot) {
        super(item, slot, "button");
    }

    public MenuButton(ItemStack item, int slot, String identifier) {
        super(item, slot, identifier);
    }

    public MenuButton(ItemStack item, int slot, DefaultButtons defaultButton) {
        super(item, slot, defaultButton);
    }

    public MenuButton(ItemStack item, int slot, DefaultButtons defaultButton, Object value) {
        super(item, slot, defaultButton, value);
    }
}
