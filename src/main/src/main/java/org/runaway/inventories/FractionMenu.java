package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.runaway.Gamer;
import org.runaway.enums.*;
import org.runaway.items.Item;
import org.runaway.Prison;
import org.runaway.donate.Privs;
import org.runaway.donate.features.FractionDiscount;
import org.runaway.managers.GamerManager;
import org.runaway.menu.SimpleItemStack;
import org.runaway.menu.UpdateMenu;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.utils.ItemBuilder;
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
        menu.setType(InventoryType.WORKBENCH);
        IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.IRON_SWORD).name("&fФракция • &bСлучайная").lore(new Lore.BuilderLore()
                .addSpace()
                .addString("&fЦена &7• &aБесплатно")
                .build()).build().item()).setSlot(0);
        UpdateMenu.builder().updateType(new SimpleItemStack[]{SimpleItemStack.builder()
                        .material(Material.STONE_SWORD)
                        .durability(0).build(),
                        SimpleItemStack.builder()
                                .material(Material.GOLDEN_SWORD)
                                .durability(0).build()})
                .build().update(menu, btn);
        btn.setClickEvent(event ->
                new Confirmation(event.getWhoClicked(), () ->
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

        AtomicInteger i = new AtomicInteger(1);

        double finalSale = sale;
        Arrays.stream(FactionType.values()).forEach(factionType -> {
            if (factionType.equals(FactionType.DEFAULT)) return;
            IMenuButton button = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(factionType.getIcon()).name("&fФракция &7• " + factionType.getColor() + factionType.getName())
                    .lore(new Lore.BuilderLore()
                            .addSpace()
                            .addString("&fЦена &7• &a" + cost + " " + MoneyType.RUBLES.getShortName() + (finalSale > 0 ? (" &7(&bСкидка " + (1 - finalSale) * 100 + "%&7)") : ("")))
                            .build()).build().item()).setSlot(i.getAndIncrement());
            button.setClickEvent(event -> {
                if(!gamer.hasMoney(cost)) {
                    gamer.sendMessage(EMessage.MONEYNEEDS);
                    return;
                }
                new Confirmation(event.getWhoClicked(), () ->
                        gamer.inFraction(factionType, false, cost));
            });
            menu.addButton(button);
        });
        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new ItemBuilder(EButtons.CANCEL.getItemStack()).build()).setSlot(9);
        back.setClickEvent(event -> event.getWhoClicked().closeInventory());
        menu.addButton(back);

        menu.open(GamerManager.getGamer(player));
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
