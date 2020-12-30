package org.runaway.utils;

/*
 * Created by _RunAway_ on 23.1.2019
 */

public abstract class Items {

    private final short data;
    private final int amount;
    private final String name;
    private final Lore lore;
    private final boolean unbreakable;

    public abstract static class Builder<T extends Builder<T>> {
        private short data = 0;
        private int amount = 1;
        private String name = "";
        private Lore lore = null;
        private boolean unbreakable = false;

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

        protected abstract T self();

        public abstract Items build();
    }

    public Items(Builder<?> builder) {
        data = builder.data;
        amount = builder.amount;
        name = builder.name;
        lore = builder.lore;
        unbreakable = builder.unbreakable;
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

    public boolean isUnbreakable() {
        return unbreakable;
    }
}
