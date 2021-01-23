package org.runaway.mines;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.items.Item;
import org.runaway.enums.EStat;
import org.runaway.utils.Items;
import org.runaway.utils.Lore;

import java.util.ArrayList;

public class MineIcon extends Items {

    private final Mines mine;
    private ItemStack btn;
    private ItemStack secbtn;

    public static class Builder extends Items.Builder<Builder> {
        private final Mines mine;

        public Builder(Mines mine) {
            this.mine = mine;
        }

        @Override protected Builder self() {
            return this;
        }

        @Override
        public MineIcon build() {
            return new MineIcon(this);
        }
    }

    private MineIcon(Builder builder) {
        super(builder);
        this.mine = builder.mine;
        this.btn = getButton(true);
        this.secbtn = getButton(false);
    }

    private ItemStack getButton(boolean access) {
        ArrayList<String> reqs = new ArrayList<>();
        reqs.add("&7• " + (access ? ChatColor.GREEN : ChatColor.RED) + "Минимальный уровень • " + mine.getMinLevel());
        if (mine.needPerm) reqs.add("&7• &4&nСпециальный доступ");
        return new Item.Builder(mine.icon).name(mine.name).lore(
                new Lore.BuilderLore()
                        .addSpace()
                        .addString("&7Требования к доступу:")
                        .addList(reqs)
                        .build()).build().item();
    }

    public ItemStack acessButton(Gamer gamer) {
        if (gamer.getIntStatistics(EStat.LEVEL) >= mine.getMinLevel()) {
            return getYesAccess();
        }
        return getNoAccess();
    }

    public ItemStack getYesAccess() {
        return btn;
    }

    public ItemStack getNoAccess() {
        return secbtn;
    }
}
