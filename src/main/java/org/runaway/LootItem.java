package org.runaway;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.runaway.items.PrisonItem;

@Getter
@Setter
@Builder
public class LootItem {
    private PrisonItem prisonItem;
    private float chance;
    private int minAmount, maxAmount;
}