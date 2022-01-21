package org.runaway.enums;

import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.NotActuallyItemsAdderException;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.runaway.Prison;
import org.runaway.utils.ItemBuilder;

@Getter
public enum EButtons {
    BACK("mcicons:icon_back_orange", "&cВернуться", Material.BARRIER),
    NEXT("mcicons:icon_next_orange", "&aВперёд", Material.BARRIER),
    WHITE_BACK("mcicons:icon_back_white", "&cПредыдущая страница", Material.BARRIER),
    WHITE_NEXT("mcicons:icon_next_white", "&aСледующая страница", Material.BARRIER),
    SACK_OF_MONEY("itemsadder:sack_of_money", null, Material.GOLD_BLOCK),
    PLUS("itemsadder:plastic", null, Material.IRON_BARS),
    CANCEL("mcicons:icon_cancel", "&cВыйти", Material.ARROW),
    ARROW_RIGHT("mcicons:icon_right_blue", null, Material.COMPASS),
    SEARCH("mcicons:icon_comment", null, Material.PAPER);

    private String path;
    private String defaultDisplayName;
    private ItemStack replacer;

    EButtons(String path, String defaultDisplayName, Material replacer) {
        this.path = path;
        this.defaultDisplayName = (defaultDisplayName == null ? " " : defaultDisplayName);
        this.replacer = new ItemBuilder(replacer).name(this.defaultDisplayName).build();
    }

    public ItemStack getItemStack() {
        if (Prison.useItemsAdder) {
            try {
                return new ItemBuilder(CustomStack.getInstance(getPath()).getItemStack()).name(this.defaultDisplayName).build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ItemStack(this.replacer);
    }
}
