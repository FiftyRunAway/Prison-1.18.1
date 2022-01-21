package org.runaway.items.formatters;

import org.bukkit.inventory.ItemStack;

public interface Formatter {
    Object getFinalValue();

    Object[] getReplaceObjects();

    String getString();

    ItemStack apply(ItemStack itemStack);

    ItemStack apply(ItemStack itemStack, Object customValue);
}
