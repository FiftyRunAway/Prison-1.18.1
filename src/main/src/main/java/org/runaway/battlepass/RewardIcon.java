package org.runaway.battlepass;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.runaway.items.Item;
import org.runaway.utils.Items;
import org.runaway.utils.Lore;

import java.util.ArrayList;

public class RewardIcon extends Items {

    private final IReward reward;

    public static class Builder extends Items.Builder<RewardIcon.Builder> {
        private final IReward reward;

        Builder(IReward reward) {
            this.reward = reward;
        }

        @Override public RewardIcon build() { return new RewardIcon(this); }

        @Override protected RewardIcon.Builder self() {
            return this;
        }
    }

    private RewardIcon(RewardIcon.Builder builder) {
        super(builder);
        this.reward = builder.reward;
    }

    public ItemStack getIcon() {
        boolean is = false;
        if (!reward.isStringValue())
            is = this.reward.getValue() > 0;
        ArrayList<String> adding = new ArrayList<>();
        int amount = 1;
        if (is) {
            adding.add(" ");
            adding.add("&7Количество • &e" + this.reward.getValue());
            amount = Math.min(this.reward.getValue(), 64);
        }
        return new Item.Builder(this.reward.getType())
                .name("&bНаграда: " + ChatColor.GREEN + this.reward.getName())
                .amount(amount)
                .lore(new Lore.BuilderLore()
                        .addSpace()
                        .addString("&7Описание награды:")
                        .addString(ChatColor.GRAY + this.reward.getDescription())
                        .addList(adding)
                        .build())
                .build().item();
    }
}
