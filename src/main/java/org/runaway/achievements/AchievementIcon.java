package org.runaway.achievements;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.runaway.utils.Items;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;

public class AchievementIcon extends Items {

    private final Achievement achievement;
    private ItemStack opened;
    private ItemStack closed;

    public static class Builder extends Items.Builder<Builder> {
        private final Achievement achievement;

        Builder(Achievement achievement) {
            this.achievement = achievement;
        }

        @Override public AchievementIcon build() { return new AchievementIcon(this); }

        @Override protected Builder self() {
            return this;
        }
    }

    private AchievementIcon(Builder builder) {
        super(builder);
        this.achievement = builder.achievement;
        this.opened = getOpened();
        this.closed = getClosed();
    }

    private ItemStack getOpened() {
        ItemStack item = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Utils.colored(ChatColor.YELLOW + achievement.getTitle()));
        ArrayList<String> rews = new ArrayList<>();
        Arrays.stream(achievement.getReward()).forEach(reward -> rews.add("&7- " + reward.rewardTitle()));
        meta.setLore(new Lore.BuilderLore()
                .addString("&aДостижение получено!")
                .addString("&7Задание: " + achievement.getName())
                .addString("&7Награды:")
                .addList(rews)
                .build().getList());
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack getClosed() {
        ItemStack item = new ItemStack(Material.COAL_BLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Utils.colored(achievement.isSecret ? "&4!!! &cСекретное достижение &4!!!" : ChatColor.YELLOW + achievement.getTitle()));
        ArrayList<String> rews = null;
        if (!achievement.isSecret) {
            rews = new ArrayList<>();
            for (Reward reward : achievement.getReward()) { rews.add("&7- " + reward.rewardTitle()); }
        }
        meta.setLore(new Lore.BuilderLore()
                .addString("&cДостижение заблокировано!")
                .addString("&7Задание: " + (achievement.isSecret ? " &kClosed" : achievement.getName()))
                .addString("&7Награды:" + (achievement.isSecret ? " &kClosed" : ""))
                .addList(rews)
                .build().getList());
        item.setItemMeta(meta);
        return item;
    }

    ItemStack getIconOpened() {
        return opened;
    }

    ItemStack getIconClosed() {
        return closed;
    }
}
