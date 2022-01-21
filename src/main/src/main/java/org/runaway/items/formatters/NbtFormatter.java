package org.runaway.items.formatters;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.runaway.utils.ItemUtils;

@Getter @Builder
public class NbtFormatter implements Formatter {
    String nbtString;
    Object finalValue;
    Object[] replaceObjects;

    @Override
    public String getString() {
        return getNbtString();
    }

    public String format(String string) {
        return String.format(string, getReplaceObjects());
    }

    @Override
    public ItemStack apply(ItemStack itemStack) {
        return apply(itemStack, (getFinalValue() == null ? "" : getFinalValue().toString()));
    }

    @Override
    public ItemStack apply(ItemStack itemStack, Object customValue) {
        itemStack = ItemUtils.addItemTag(itemStack, format(getNbtString()), customValue);
        return itemStack;
    }
}
