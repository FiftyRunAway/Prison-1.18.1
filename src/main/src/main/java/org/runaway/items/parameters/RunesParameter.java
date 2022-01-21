package org.runaway.items.parameters;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.runaway.items.PrisonItem;
import org.runaway.items.formatters.LoreFormatter;
import org.runaway.items.formatters.NbtFormatter;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;
import org.runaway.utils.ItemUtils;

import java.util.List;
import java.util.function.Function;

@Getter
public class RunesParameter extends DefaultParameter {
    int amount;
    List<String> defaultRunes;

    RunesParameter(String nbtString, int priority, int amount, List<String> defaultRunes) {
        super(null, nbtString, null, null, null, priority, false, false, null);
        this.amount = amount;
        this.defaultRunes = defaultRunes;
        this.priority = priority;
    }

    RunesParameter(String nbtString) {
        this(nbtString, 0, 0, null);
    }

    @Override
    public Function<PrisonItem, ItemStack> getInitialParameterApplier() {
        return (prisonItem -> {
            ItemStack itemStack = prisonItem.getItemStack();
            Parameter amountParameter = DefaultParameter.builder()
                    .defaultNbtFormatter(NbtFormatter.builder().nbtString("runesAmount").finalValue(getAmount()).build())
                    .defaultLoreFormatter(LoreFormatter.builder().loreString(ParameterManager.getRunesAmountString()).finalValue(getAmount()).build())
                    .preSpace(true)
                    .priority(20).build();
            itemStack = amountParameter.getInitialParameterApplier().apply(prisonItem);
            prisonItem.getParameters().add(amountParameter);
            for (int i = 1; i < (getAmount() + 1); i++) {
                Rune rune = null;
                if (getDefaultRunes() != null && getDefaultRunes().size() > (i - 1)) {
                    String runeString = getDefaultRunes().get(i - 1);
                    if (runeString != null) {
                        rune = RuneManager.getRune(runeString);
                    }
                }
                Parameter parameter = DefaultParameter.builder()
                        .defaultNbtFormatter(NbtFormatter.builder().nbtString("rune" + i).finalValue(rune == null ? "" : rune.getTechName()).build())
                        .defaultLoreFormatter(LoreFormatter.builder().loreString(ParameterManager.getRuneInfoString()).replaceObjects(new Object[]{i, rune == null ? "-" : rune.getName()}).build())
                        .mutable(true)
                        .build();
                itemStack = parameter.getInitialParameterApplier().apply(prisonItem);
                prisonItem.getMutableParameters().add(parameter);
            }
            ItemUtils.addLore(itemStack, "&r");
            prisonItem.setItemStack(itemStack);
            return itemStack;
        });
    }
}
