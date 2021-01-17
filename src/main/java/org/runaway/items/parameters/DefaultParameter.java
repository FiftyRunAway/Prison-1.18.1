package org.runaway.items.parameters;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.runaway.Prison;
import org.runaway.items.formatters.Formatter;
import org.runaway.items.formatters.LoreFormatter;
import org.runaway.items.formatters.NbtFormatter;
import org.runaway.utils.ItemUtils;

import java.util.function.BiFunction;
import java.util.function.UnaryOperator;

@Getter @Builder
public class DefaultParameter implements Parameter {
    String loreString, nbtString;
    Formatter defaultLoreFormatter, defaultNbtFormatter;
    int priority;
    ParameterManager parameterManager = Prison.getInstance().getItemManager().getParameterManager();

    @Override
    public UnaryOperator<ItemStack> getInitialParameterApplier() {
        return (itemStack -> {
            if(getLoreString() != null) {
                if(getDefaultLoreFormatter() == null) {
                    itemStack = ItemUtils.addLore(itemStack, "&r",
                            loreString);
                } else {
                   itemStack = getDefaultLoreFormatter().apply(itemStack, loreString);
                }
            }
            if(getNbtString() != null) {
                if(getDefaultNbtFormatter() == null) {
                    itemStack = ItemUtils.addItemTag(itemStack, "");
                } else {
                    itemStack = getDefaultNbtFormatter().apply(itemStack, nbtString);
                }
            }
            return itemStack;
        });
    }

    @Override
    public BiFunction<ItemStack, Object[], Object> getParameterGetter() {
        return ((itemStack, objects) -> {
            return Prison.getInstance().getItemManager().getValueByNbt(itemStack, objects == null ?
                    getNbtString() :
                    String.format(getNbtString(), objects));
        });
    }

    @Override
    public ItemStack applyNewValues(ItemStack itemStack, Formatter... formatters) {
        for (Formatter formatter : formatters) {
            if(formatter instanceof NbtFormatter) {
                formatter.apply(itemStack, getNbtString());
            } else if(formatter instanceof LoreFormatter) {
                formatter.apply(itemStack, getLoreString());
            }
        }
        return itemStack;
    }
}
