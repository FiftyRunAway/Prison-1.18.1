package org.runaway.items;

import lombok.Getter;
import org.runaway.Prison;

@Getter
public class NumberedPrisonItem {
    private PrisonItem prisonItem;
    private int amount;

    public NumberedPrisonItem(PrisonItem prisonItem, int amount) {
        this.prisonItem = prisonItem;
        this.amount = amount;
    }

    public NumberedPrisonItem(PrisonItem prisonItem) {
        this(prisonItem, 1);
    }

    public NumberedPrisonItem(String prisonItem, int amount) {
        this(ItemManager.getPrisonItem(prisonItem), amount);
    }

    public NumberedPrisonItem(String prisonItem) {
        this(ItemManager.getPrisonItem(prisonItem), 1);
    }

    String getName() {
        return prisonItem.getName();
    }
}
