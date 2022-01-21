package org.runaway.items;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.runaway.Prison;
import org.runaway.utils.Items;

public class Item extends Items {

    private final Material material;
    private final Builder builders;

    public static class Builder extends Items.Builder<Builder> {
        private final Material material;
        private final ItemStack itemStack;

        public Builder(Material material) {
            this.material = material;
            this.itemStack = null;
        }

        public Builder(ItemStack itemStack) {
            this.itemStack = itemStack;
            this.material = null;
        }

        @Override public Item build() {
            return new Item(this);
        }

        @Override protected Builder self() {
            return this;
        }
    }

    private Item(Builder builder) {
        super(builder);
        material = builder.material;
        builders = builder;
    }

    public ItemStack item() {
        Item i = builders.build();
        ItemStack stack = new ItemStack(this.material, i.getAmount());
        //stack.setDurability(i.getData());
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(i.getName());
        if (!i.getEnchantments().isEmpty()) {
            i.getEnchantments().forEach((enchant, level) ->
                    meta.addEnchant(enchant, level, true));
        }
        if (i.getLore() != null) meta.setLore(i.getLore().getList());
        stack.setItemMeta(meta);
        return stack;
    }
}
