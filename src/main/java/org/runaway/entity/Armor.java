package org.runaway.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

@Builder @Getter
public class Armor {

    public ItemStack boots, leggings, chestplate, helmet, itemInHand;
}
