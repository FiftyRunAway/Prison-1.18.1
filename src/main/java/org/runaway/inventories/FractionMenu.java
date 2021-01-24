package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.items.Item;
import org.runaway.Prison;
import org.runaway.donate.Privs;
import org.runaway.donate.features.FractionDiscount;
import org.runaway.enums.EConfig;
import org.runaway.enums.EStat;
import org.runaway.enums.FactionType;
import org.runaway.enums.MoneyType;
import org.runaway.managers.GamerManager;
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
        Gamer gamer = GamerManager.getGamer(player);
        StandardMenu menu = StandardMenu.create(getRows(), getName());
        IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.DOUBLE_PLANT).name("&fФракция • Рандомная").lore(new Lore.BuilderLore()
                .addSpace()
                .addString("&fЦена &7• &aБесплатно")
                .build()).build().item()).setSlot(8);
        btn.setClickEvent(event ->
                new Confirmation(event.getWhoClicked(), menu.build(), null, () ->
                    GamerManager.getGamer(event.getWhoClicked()).inFraction(FactionType.DEFAULT, true, 0)));
        menu.addButton(btn);
        //Скидка
        Object obj = Privs.DEFAULT.getPrivilege(player).getValue(new FractionDiscount());
        double sale = 0;
        if (obj != null) sale = 1 - ((double) Integer.parseInt(obj.toString()) / 100);
        int cost;
        if (sale > 0) {
            cost = (int)Math.round(EConfig.CONFIG.getConfig().getInt("costs.SelectFraction") * gamer.getIntStatistics(EStat.LEVEL) * sale);
        } else {
            cost = EConfig.CONFIG.getConfig().getInt("costs.SelectFraction") * gamer.getIntStatistics(EStat.LEVEL);
        }

        AtomicInteger i = new AtomicInteger();

        double finalSale = sale;
        Arrays.stream(FactionType.values()).forEach(factionType -> {
            if (factionType.equals(FactionType.DEFAULT)) return;
            IMenuButton button = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(factionType.getIcon()).name("&fФракция &7• " + factionType.getColor() + factionType.getName())
                    .lore(new Lore.BuilderLore()
                            .addSpace()
                            .addString("&fЦена &7• &a" + cost + " " + MoneyType.RUBLES.getShortName() + (finalSale > 0 ? (" &7(&bСкидка " + (1 - finalSale) * 100 + "%&7)") : ("")))
                            .build()).build().item()).setSlot(i.getAndIncrement());
            button.setClickEvent(event ->
                    new Confirmation(event.getWhoClicked(), menu.build(), null, () ->
                        GamerManager.getGamer(event.getWhoClicked()).inFraction(factionType, false, cost)));
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
