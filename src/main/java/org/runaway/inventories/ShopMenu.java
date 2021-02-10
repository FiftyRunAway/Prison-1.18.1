package org.runaway.inventories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.runaway.Gamer;
import org.runaway.enums.EMessage;
import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.utils.ExampleItems;
import org.runaway.Prison;
import org.runaway.utils.ItemUtils;
import org.runaway.utils.Utils;
import org.runaway.enums.EConfig;
import org.runaway.enums.MoneyType;
import org.runaway.upgrades.UpgradeMisc;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ShopMenu implements IMenus {

    private static StandardMenu inventory;

    public ShopMenu(Player player) {
        if (player != null) player.openInventory(inventory.build());
    }

    public void load() {
        final String event_shop = EConfig.SHOP.getConfig().getString("event");
        String name = event_shop != null ? Utils.colored(getName() + " &7| &eСобытие: " + event_shop) : getName();
        StandardMenu menu = StandardMenu.create(getRows(), name);

        for (int i = 0; i < 9; i++) {
            menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(ExampleItems.glass(7)).setSlot(i));
        }
        for (int i = 27; i < 36; i++) {
            menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(ExampleItems.glass(7)).setSlot(i));
        }
        AtomicInteger i = new AtomicInteger(9);
        EConfig.SHOP.getConfig().getConfigurationSection("items").getKeys(false).forEach(s -> {
            ConfigurationSection section = EConfig.SHOP.getConfig().getConfigurationSection("items." + s);

            ItemStack itemStack, menubtn;
            double cost = section.getInt("cost");
            itemStack = ItemManager.getPrisonItem(section.getString("config")).getItemStack(
                    section.contains("amount") ? section.getInt("amount") : 1);
            menubtn = itemStack.clone();
            ItemUtils.addLore(itemStack, " ", "&eСтоимость &7• &f&n" + cost + "&r " + MoneyType.RUBLES.getShortName());
            if (event_shop != null && section.contains("event")) {
                ItemUtils.addLore(itemStack, "&cТолько для события - &r" + event_shop);
            }

            IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(itemStack);
            btn.setClickEvent(event -> {
                Player player = event.getWhoClicked();
                Gamer gamer = GamerManager.getGamer(player);
                if (gamer.getMoney() < cost) {
                    gamer.sendMessage(EMessage.MONEYNEEDS);
                    player.closeInventory();
                    return;
                }
                gamer.addItem(menubtn);
                gamer.withdrawMoney(cost);
                gamer.sendMessage(EMessage.SUCCESSFULBUY);
            });
            menu.addButton(btn.setSlot(i.getAndIncrement()));
        });
        inventory = menu;
    }

    @Override
    public int getRows() {
        return 4;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Магазин";
    }
}
