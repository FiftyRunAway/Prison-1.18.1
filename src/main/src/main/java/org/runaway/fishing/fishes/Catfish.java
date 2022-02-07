package org.runaway.fishing.fishes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.runaway.fishing.EFishType;
import org.runaway.fishing.Fish;

public class Catfish extends Fish {

    @Override
    public String getName() {
        return "Сом";
    }

    @Override
    public EFishType getType() {
        return EFishType.LEGENDARY;
    }

    @Override
    public ItemStack getMaterial() {
        return new ItemStack(Material.TROPICAL_FISH);
    }

    @Override
    protected double getMaxWeight() {
        return 40000;
    }

    @Override
    protected double getChance() {
        return 0.1;
    }

    @Override
    protected short getIconData() {
        return 0;
    }

    @Override
    public double getPrice() {
        return 0.000049;
    }
}
