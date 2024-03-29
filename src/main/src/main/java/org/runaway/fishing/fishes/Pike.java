package org.runaway.fishing.fishes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.runaway.fishing.EFishType;
import org.runaway.fishing.Fish;

public class Pike extends Fish {
    @Override
    public String getName() {
        return "Щука";
    }

    @Override
    public EFishType getType() {
        return EFishType.RARE;
    }

    @Override
    public ItemStack getMaterial() {
        return new ItemStack(Material.COOKED_SALMON);
    }

    @Override
    protected double getMaxWeight() {
        return 2500;
    }

    @Override
    protected double getChance() {
        return 0.4;
    }

    @Override
    protected short getIconData() {
        return 2;
    }

    @Override
    public double getPrice() {
        return 0.000044;
    }
}
