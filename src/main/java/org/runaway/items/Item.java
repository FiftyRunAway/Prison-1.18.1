package org.runaway.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.runaway.utils.Items;

public class Item extends Items {

    private final Material material;
    private final Builder builders;

    public static class Builder extends Items.Builder<Builder> {
        private final Material material;

        public Builder(Material material) {
            this.material = material;
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
        stack.setDurability(i.getData());
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
