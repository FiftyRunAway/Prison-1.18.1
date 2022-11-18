package org.runaway.mines;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.donate.features.BossNotify;
import org.runaway.items.Item;
import org.runaway.utils.Items;
import org.runaway.utils.Lore;
import org.runaway.utils.TimeUtils;

import java.util.ArrayList;

public class MineIcon extends Items {

    private final Mines mine;

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
    }

    private ItemStack getButton(boolean access, Gamer gamer, boolean showBoss) {
        return new Item.Builder(access ? (showBoss ? mine.getBoss().getAttributable().getMobType().getIcon().getType() : mine.icon) : Material.RED_STAINED_GLASS_PANE)
                .name(access ? (showBoss ? mine.getBoss().getAttributable().getName() : mine.name) : "&cНедоступно").lore(lore(gamer, showBoss)).build().item();
    }

    public ItemStack acessButton(Gamer gamer, boolean showBoss) {
        return getButton(mine.canTeleport(gamer), gamer, showBoss);
    }

    public Lore lore(Gamer gamer, boolean showBoss) {
        ArrayList<String> reqs = new ArrayList<>();
        boolean access = mine.canTeleport(gamer);
        reqs.add("&7• " + (access ? ChatColor.GREEN : ChatColor.RED) + "Минимальный уровень • " + mine.getMinLevel());
        if (mine.needPerm) reqs.add("&7• &4&nСпециальный доступ");
        Lore.BuilderLore lore;
        if (!mine.canTeleport(gamer)) {
            lore = new Lore.BuilderLore().addSpace().addString("&7Требования к доступу:")
                    .addList(reqs);
        } else {
            lore = new Lore.BuilderLore().addSpace().addString(" &fУровень &7• &e" + mine.getMinLevel());
        }
        if (mine.hasBoss() && access) {
            if (mine.getBoss() != null) {
                Object b = gamer.getPrivilege().getValue(new BossNotify());
                if (gamer.hasPermission("admin") || b != null && Boolean.parseBoolean(b.toString())) {
                    lore.addString("&r");
                    lore.addString(" &7Респавн &7• " + (mine.boss.isAlive() ? "&6Уже появился" : " &e" + TimeUtils.getDuration(mine.getBoss().getRespawnTimeLeft())));
                }
            }
        }
        return lore.build();
    }
}
