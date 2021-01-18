package org.runaway.inventories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.runaway.utils.ExampleItems;
import org.runaway.Prison;
import org.runaway.utils.Utils;
import org.runaway.enums.EConfig;
import org.runaway.enums.MoneyType;
import org.runaway.upgrades.UpgradeMisc;

import java.util.ArrayList;

public class ShopMenu implements IMenus {

    private static Inventory inventory;

    public ShopMenu(Player player) {
        if (player != null) player.openInventory(inventory);
    }

    public void load() {
        String event_shop = EConfig.SHOP.getConfig().getString("event");
        if (event_shop != null) {
            inventory = Bukkit.createInventory(null, getRows() * 9, Utils.colored(getName() + " &7| &eСобытие: " + event_shop));
        } else {
            inventory = Bukkit.createInventory(null, getRows() * 9, getName());
        }
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, ExampleItems.glass(7));
        }
        for (int i = 27; i < 36; i++) {
            inventory.setItem(i, ExampleItems.glass(7));
        }
        Prison.value_shopitems = EConfig.CONFIG.getConfig().getInt("values.shop_items");
        for (int i = 1; i < Prison.value_shopitems + 1; i++) {
            ItemStack item;
            if (EConfig.SHOP.getConfig().contains("items." + i + ".config")) {
                item = UpgradeMisc.buildItem(EConfig.SHOP.getConfig().getString("items." + i + ".config"), false, null, false);
                ItemMeta meta = item.getItemMeta();
                ArrayList<String> lore = new ArrayList<>(meta.getLore());
                lore.add(" ");
                lore.add(Utils.colored("&fЦена &7• &f" + EConfig.SHOP.getConfig().getInt("items." + i + ".cost") + " " + MoneyType.RUBLES.getShortName()));
                if (event_shop != null && EConfig.SHOP.getConfig().contains("items." + i + ".event")) {
                    lore.add(Utils.colored("&cВременный предмет"));
                }
                meta.setLore(lore);
                meta.spigot().setUnbreakable(true);
                item.setItemMeta(meta);
            } else {
                item = new ItemStack(Material.valueOf(EConfig.SHOP.getConfig().getString("items." + i + ".material")), EConfig.SHOP.getConfig().getInt("items." + i + ".amount"));
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(Utils.colored(EConfig.SHOP.getConfig().getString("items." + i + ".name")));
                ArrayList<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(Utils.colored("&fЦена &7• &f" + EConfig.SHOP.getConfig().getInt("items." + i + ".cost") + " " + MoneyType.RUBLES.getShortName()));
                if (event_shop != null && EConfig.SHOP.getConfig().contains("items." + i + ".event")) {
                    lore.add(Utils.colored("&cВременный предмет"));
                }
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inventory.setItem(i + 8, item);
        }
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
