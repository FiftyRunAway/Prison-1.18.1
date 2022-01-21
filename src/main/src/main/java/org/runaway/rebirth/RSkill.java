package org.runaway.rebirth;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.items.Item;
import org.runaway.enums.EConfig;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.inventories.Confirmation;
import org.runaway.inventories.RebirthMenu;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.utils.Lore;

import java.util.ArrayList;

public abstract class RSkill {

    protected abstract String getName();
    protected abstract Material getMaterial();
    protected abstract String getLore();
    protected abstract int getMaximumLevel();
    protected abstract int getStep();

    public IMenuButton getIcon(Player player) {
        IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(
                new Item.Builder(getMaterial())
                        .lore(lore(player))
                        .amount(getAmount(player))
                        .name(ChatColor.YELLOW + getName() + " &7(Сейчас: &e" + getValue(player.getName()) + getValueDescription() + "&7)")
                        .build().item());
        btn.setClickEvent(event -> {
            Player p = event.getWhoClicked();
            Gamer gamer = GamerManager.getGamer(p);
            String name = p.getName();
            if (getLevel(name) + 1 < getMaximumLevel() && gamer.getIntStatistics(EStat.REBIRTH_SCORE) >= getCost(name)) {
                new Confirmation(p, () -> {
                    levelUp(name);
                    gamer.setStatistics(EStat.REBIRTH_SCORE, gamer.getIntStatistics(EStat.REBIRTH_SCORE) - getCost(name));
                    p.closeInventory();
                    gamer.sendMessage(EMessage.REBIRTHBOUGHT);
                });
            }
        });
        return btn;
    }

    private Lore lore(Player player) {
        ArrayList<String> l = new ArrayList<>();

        String name = player.getName();
        if (getLevel(name) + 1 < getMaximumLevel()) {
            l.add(
                    "&7Следующее значение: &e" + getNextValue(name) +
                            (getValueDescription() != null ? (getValueDescription()) : "") + " &7(&b" + getLevel(name) + "&7/&b" + getMaximumLevel() + "&7)");
            l.add("&7Стоимость следующего уровня: &d" + getCost(name) + " ОП");
            Gamer gamer = GamerManager.getGamer(player);
            if (gamer.getIntStatistics(EStat.REBIRTH_SCORE) >= getCost(name)) {
                l.add("&aНажмите, чтобы приобрести данный навык!");
            } else {
                l.add("&cУ вас недостаточно очков перерождения...");
            }
        } else {
            l.add("&dЭтот навык прокачан до конца!");
        }

        return new Lore.BuilderLore()
                .addSpace()
                .addString("&7Описание:")
                .addString(ChatColor.WHITE + "&o" + getLore())
                .addSpace()
                .addList(l)
                .build();
    }

    private int getAmount(Player player) {
        return getLevel(player.getName()) + 1;
    }

    private int getLevel(String player) {
        try {
            return 1; // TO-DO
        } catch (Exception e) {
            return 0;
        }
    }

    public int getValue(String player) {
        return getLevel(player) * getStep();
    }

    private int getNextValue(String player) {
        return getLevel(player) * getStep() + getStep();
    }

    private int getCost(String player) {
        return getLevel(player) + 1;
    }

    private String configName() {
        return this.getClass().getSimpleName();
    }

    public String getValueDescription() {
        return null;
    }

    private void levelUp(String player) {
        /*EConfig.REBIRTH_DATA.getConfig().set(player + "." + configName(), getLevel(player) + 1);
        EConfig.REBIRTH_DATA.saveConfig();*/
    }
}
