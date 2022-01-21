package org.runaway.items.formatters;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.runaway.utils.Utils;

@Getter @Builder
public class NameFormatter implements Formatter {
    String nameString;
    Object finalValue;
    Object[] replaceObjects;

    @Override
    public String getString() {
        return "";
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
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Utils.colored(format(itemMeta.getDisplayName()) + customValue.toString()));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
