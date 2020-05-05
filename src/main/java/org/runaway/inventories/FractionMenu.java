package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Item;
import org.runaway.Main;
import org.runaway.enums.EConfig;
import org.runaway.enums.EStat;
import org.runaway.enums.FactionType;
import org.runaway.enums.MoneyType;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.utils.Lore;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Created by _RunAway_ on 5.2.2019
 */

public class FractionMenu implements IMenus {

    public FractionMenu(Player player) {
        Gamer gamer = Main.gamers.get(player.getUniqueId());
        StandardMenu menu = StandardMenu.create(getRows(), getName());
        IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.DOUBLE_PLANT).name("&fФракция • Рандомная").lore(new Lore.BuilderLore()
                .addSpace()
                .addString("&fЦена &7• &aБесплатно")
                .build()).build().item()).setSlot(8);
        btn.setClickEvent(event -> Main.gamers.get(event.getWhoClicked().getUniqueId()).inFraction(FactionType.DEFAULT, true));
        menu.addButton(btn);
        int cost = EConfig.CONFIG.getConfig().getInt("costs.SelectFraction") * (int) gamer.getStatistics(EStat.LEVEL);
        AtomicInteger i = new AtomicInteger();

        Arrays.stream(FactionType.values()).forEach(factionType -> {
            if (factionType.equals(FactionType.DEFAULT)) return;
            IMenuButton button = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(factionType.getIcon()).name("&fФракция &7• " + factionType.getColor() + factionType.getName())
                    .lore(new Lore.BuilderLore()
                            .addSpace()
                            .addString("&fЦена &7• &a" + cost + " " + MoneyType.RUBLES.getShortName())
                            .build()).build().item()).setSlot(i.getAndIncrement());
            button.setClickEvent(event -> Main.gamers.get(event.getWhoClicked().getUniqueId()).inFraction(factionType, false));
            menu.addButton(button);
        });
        player.openInventory(menu.build());
    }

    @Override
    public int getRows() {
        return 1;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Выбор фракции";
    }
}
