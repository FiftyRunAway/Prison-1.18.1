package org.runaway.items.formatters;

import org.bukkit.inventory.ItemStack;

public interface Formatter {
    Object getFinalValue();

    Object[] getReplaceObjects();

    ItemStack apply(ItemStack itemStack, String string);
}
