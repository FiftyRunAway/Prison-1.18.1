package org.runaway.fishing;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.items.Item;
import org.runaway.enums.MoneyType;
import org.runaway.jobs.EJobs;
import org.runaway.jobs.Job;
import org.runaway.utils.ItemBuilder;
import org.runaway.utils.Lore;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Fish {

    private double weight;

    public abstract String getName();
    public abstract EFishType getType();
    public abstract ItemStack getMaterial();
    protected abstract double getMaxWeight();
    protected abstract double getChance();
    protected abstract short getIconData();
    public abstract double getPrice();

    private double getMinWeight() {
        return Math.ceil(getMaxWeight() / 100);
    }

    private void setWeight(double weight) {
        this.weight = weight;
    }

    private double getWeight() {
        return this.weight;
    }

    public ItemBuilder getIcon() {
        return new ItemBuilder(getMaterial().getType())
                .name(getType().getColor() + getName())
                .setLore(new Lore.BuilderLore()
                        .addString("&7Вес: &e" + getWeight() + " г.")
                        .addSpace()
                        .addString("&7Вы можете продать её у")
                        .addString("&7&nглавного рыбака").build().getList());
    }

    public double getPriceLevel(Gamer gamer) {
        return BigDecimal.valueOf(getPrice() * 1000 * (((double) Job.getStatistics(gamer, EJobs.FISHERMAN.name().toLowerCase()) / 2) + 1)).setScale(2, RoundingMode.UP).doubleValue();
    }

    public ItemBuilder getIcon(Gamer gamer) {
        String m = getPriceLevel(gamer) + " ";
        return new ItemBuilder(getMaterial())
                .name(getType().getColor() + getName())
                .setLore(new Lore.BuilderLore()
                        .addString("&7Цена за 1 кг: &e" + m + MoneyType.RUBLES.getShortName())
                        .addSpace()
                        .addString("&7Цена зависит от")
                        .addString("&7вашего уровня работы!").build().getList());
    }

    private static Fish getRandomFish(EFishType type) {
        Fish fish = null;
        for (EFish eFish : EFish.values()) {
            Fish f = eFish.getFish();
            if (!f.getType().equals(type)) continue;
            if (Math.random() < f.getChance()) {
                fish = f;
            }
        }
        if (fish != null) {
            fish.setWeight(Math.ceil(ThreadLocalRandom.current().nextDouble(
                            fish.getMaxWeight() - fish.getMinWeight()) + fish.getMinWeight()));
            return fish;
        }
        return null;
    }

    public static Fish randomFish(EFishType type) {
        if (type == EFishType.NONE_REWARD || type == EFishType.TRY_AGAIN) return null;
        Fish fish = null;
        while (fish == null) {
            fish = getRandomFish(type);
        }
        return fish;
    }
}
