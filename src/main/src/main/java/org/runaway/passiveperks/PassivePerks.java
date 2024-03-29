package org.runaway.passiveperks;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.items.Item;
import org.runaway.enums.EStat;
import org.runaway.inventories.Confirmation;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.utils.Lore;

import java.util.Arrays;

public abstract class PassivePerks {

    protected abstract String getName();
    protected abstract String getDescription();
    public abstract int getLevel();

    public int getSlot() {
        return 0;
    }
    public void getPerkAction(Gamer gamer) { }

    public boolean isEffectAction() {
        return false;
    }

    public static void onJoin(Gamer gamer) {
        Arrays.stream(EPassivePerk.values()).forEach(perk -> {
            perk.getPerk().getAction(gamer);
        });
    }

    public IMenuButton getIcon(Gamer gamer) {
        IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(
                new Item.Builder(getMaterial(gamer))
                        .lore(lore(gamer))
                        .name(ChatColor.AQUA + getName())
                        .build().item());
        btn.setClickEvent(event -> {
            Player p = event.getWhoClicked();
            Gamer g = GamerManager.getGamer(p);
            if (g.getIntStatistics(EStat.LEVEL) < getLevel()
                    || g.hasPassivePerk(this)
                    || g.hasPassivePerk(getAnotherPerk(getLevel()))) {
                return;
            }
            new Confirmation(p, () -> {
                g.addPassivePerk(this);
                g.sendTitle("&bВы получили", "&bпассивный навык!");
                this.getPerkAction(g);
                p.closeInventory();
            });
        });
        return btn;
    }

    private Lore lore(Gamer gamer) {
        StringBuilder sb = new StringBuilder();
        if (gamer.getIntStatistics(EStat.LEVEL) < getLevel()) {
            sb.append("&cВы не достигли данного уровня");
        } else {
            if (gamer.hasPassivePerk(this)) {
                sb.append("&eВы уже получили это умение");
            } else {
                if (gamer.hasPassivePerk(getAnotherPerk(getLevel()))) {
                    sb.append("&eВы уже сделали свой выбор");
                } else {
                    sb.append("&a&nНажмите, чтобы получить");
                }
            }
        }
        return new Lore.BuilderLore()
                .addString("&7Описание:")
                .addString("&e" + getDescription())
                .addSpace()
                .addString(sb.toString())
                .build();
    }

    private Material getMaterial(Gamer gamer) {
        if (gamer.getIntStatistics(EStat.LEVEL) < getLevel()) {
            return Material.RED_STAINED_GLASS_PANE;
        } else {
            if (gamer.hasPassivePerk(this)) {
                return Material.LIME_STAINED_GLASS_PANE;
            } else {
                if (gamer.hasPassivePerk(getAnotherPerk(getLevel()))) {
                    return Material.BLACK_STAINED_GLASS_PANE;
                } else {
                    return Material.YELLOW_STAINED_GLASS_PANE;
                }
            }
        }
    }

    private PassivePerks getAnotherPerk(int level) {
        for (EPassivePerk perk : EPassivePerk.values()) {
            if (!perk.getPerk().getName().equals(getName())
                    && perk.getPerk().getLevel() == level) return perk.getPerk();
        }
        return null;
    }
    public void getAction(Gamer gamer) {
        if (gamer.hasPassivePerk(this)) {
            getPerkAction(gamer);
        }
    }
}
