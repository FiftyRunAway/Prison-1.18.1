package org.runaway.utils;

/*
 * Created by _RunAway_ on 23.1.2019
 */

import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class Items {

    private final short data;
    private final int amount;
    private final String name;
    private final Lore lore;
    private final boolean unbreakable;
    private final HashMap<Enchantment, Integer> enchantments;

    public abstract static class Builder<T extends Builder<T>> {
        private short data = 0;
        private int amount = 1;
        private String name = "";
        private Lore lore = null;
        private boolean unbreakable = false;
        private HashMap<Enchantment, Integer> enchantments = new HashMap<>();

        public T data(short data) {
            this.data = data;
            return self();
        }

        public T amount(int amount) {
            this.amount = amount;
            return self();
        }

        public T name(String name) {
            this.name = Utils.colored(name);
            return self();
        }

        public T lore(Lore lore) {
            this.lore = lore;
            return self();
        }

        public T unbreakable() {
            this.unbreakable = unbreakable;
            return self();
        }

        public T enchantment(Enchantment enchantment, int level) {
            enchantments.put(enchantment, level);
            return self();
        }

        public T enchantmentList(HashMap<Enchantment, Integer> enchants) {
            if (enchants != null) {
                enchants.forEach((enchantment, integer) -> {
                    enchantments.put(enchantment, integer);
                });
            }
            return self();
        }

        protected abstract T self();

        public abstract Items build();
    }

    public Items(Builder<?> builder) {
        data = builder.data;
        amount = builder.amount;
        name = builder.name;
        lore = builder.lore;
        unbreakable = builder.unbreakable;
        enchantments = builder.enchantments;
    }

    public short getData() {
        return data;
    }

    public int getAmount() {
        return amount;
    }

    public Lore getLore() {
        return lore;
    }

    public String getName() {
        return name;
    }

    public HashMap<Enchantment, Integer> getEnchantments() {
        return enchantments;
    }

    public boolean isUnbreakable() {
        return unbreakable;
    }
}
