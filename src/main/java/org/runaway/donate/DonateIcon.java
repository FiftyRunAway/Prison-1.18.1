package org.runaway.donate;

import org.bukkit.inventory.ItemStack;
import org.runaway.utils.Items;

public class DonateIcon extends Items {

    private final Donate donate;
    private ItemStack item;

    public static class Builder extends Items.Builder<DonateIcon.Builder> {
        private final Donate donate;

        public Builder(Donate donate) {
            this.donate = donate;
        }

        @Override public Items build() { return new DonateIcon(this); }

        @Override protected Builder self() { return this; }
    }

    private DonateIcon(Builder builder) {
        super(builder);
        donate = builder.donate;
        item = donate.getIcon();
    }

    public ItemStack icon() {
        return item;
    }
}
