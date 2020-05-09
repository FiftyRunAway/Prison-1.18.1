package org.runaway.battlepass;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.runaway.Item;
import org.runaway.utils.Items;
import org.runaway.utils.Lore;

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
        return new Item.Builder(this.reward.getType())
                .name(ChatColor.GREEN + this.reward.getName())
                .amount(this.reward.getValue() > 0 ? this.reward.getValue() : 1)
                .lore(new Lore.BuilderLore()
                        .addSpace()
                        .addString(ChatColor.GRAY + this.reward.getDescription())
                        .build())
                .build().item();
    }
}
