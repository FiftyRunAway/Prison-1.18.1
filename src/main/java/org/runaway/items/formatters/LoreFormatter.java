package org.runaway.items.formatters;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.runaway.Prison;
import org.runaway.utils.ItemUtils;

@Builder @Getter
public class LoreFormatter implements Formatter {
    String loreString;
    Object finalValue;
    Object[] replaceObjects;

    @Override
    public String getString() {
        return getLoreString();
    }

    public String format(String string) {
        return String.format(string, getReplaceObjects());
    }

    @Override
    public ItemStack apply(ItemStack itemStack) {
        return ItemUtils.addLore(itemStack, format(getLoreString()) + (getFinalValue() == null ? "" : getFinalValue().toString()));
    }

    @Override
    public ItemStack apply(ItemStack itemStack, Object customValue) {
        return ItemUtils.setLoreValue(itemStack, format(getLoreString()), customValue.toString());
    }
}
