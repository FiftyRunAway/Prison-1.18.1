package org.runaway.items.parameters;

import org.bukkit.inventory.ItemStack;
import org.runaway.enums.StatType;
import org.runaway.items.PrisonItem;
import org.runaway.items.formatters.Formatter;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public interface Parameter {
    int getPriority();

    Function<PrisonItem, ItemStack> getInitialParameterApplier();

    ItemStack changeValues(ItemStack itemStack, Object value);

    StatType getStatType();

    Formatter getDefaultLoreFormatter();

    Formatter getDefaultNbtFormatter();

    Formatter getDefaultNameFormatter();

    BiFunction<ItemStack, Object[], Object> getParameterGetter();

    boolean isMutable();

    String getNbtString();
}
