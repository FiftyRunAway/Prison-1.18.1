package org.runaway.items.formatters;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.runaway.utils.ItemUtils;

@Getter @Builder
public class LoreFormatter implements Formatter {
    Object finalValue;
    Object[] replaceObjects;

    public String format(String string) {
        return String.format(string, getReplaceObjects());
    }

    public ItemStack apply(ItemStack itemStack, String string) {
        return ItemUtils.addLore(itemStack, format(string) + getFinalValue().toString());
    }
}
