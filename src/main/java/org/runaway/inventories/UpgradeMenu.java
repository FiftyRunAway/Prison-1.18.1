package org.runaway.inventories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.items.ItemManager;
import org.runaway.items.parameters.ParameterMeta;
import org.runaway.managers.GamerManager;
import org.runaway.requirements.RequireList;
import org.runaway.utils.ExampleItems;
import org.runaway.enums.EMessage;
import org.runaway.upgrades.UpgradeMisc;
import org.runaway.utils.ItemUtils;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by _RunAway_ on 4.5.2019
 */

public class UpgradeMenu implements IMenus {

    public UpgradeMenu(Player p2, String nextPrisonItem, RequireList requireList) {
        Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, getName());
        inventory.setItem(0, ExampleItems.glass(7));
        inventory.setItem(1, ExampleItems.glass(5, ChatColor.GREEN + "" + ChatColor.BOLD + "УЛУЧШИТЬ"));
        inventory.setItem(3, ExampleItems.glass(14, ChatColor.RED + "" + ChatColor.BOLD + "ОТМЕНИТЬ"));
        inventory.setItem(4, ExampleItems.glass(7));
        Gamer gamer = GamerManager.getGamer(p2);
        if (p2.getInventory().getItemInMainHand().getItemMeta() == null || p2.getInventory().getItemInMainHand().getItemMeta().getLore() == null || p2.getInventory().getItemInMainHand().getItemMeta().getLore().size() < 1) {
            gamer.sendMessage(EMessage.NOLOREUPGRADE);
            return;
        }
        if (p2.getInventory().getItemInMainHand().getItemMeta().getLore().get(1).contains("Аукционный предмет")) {
            gamer.sendMessage(EMessage.NOLOREUPGRADE);
            return;
        }
        List<String> upgradeLore = requireList.getLore(gamer);
        ItemStack nextItem = ItemManager.getPrisonItem(nextPrisonItem).getItemStack();
        nextItem = new ParameterMeta(p2.getInventory().getItemInMainHand()).applyTo(nextItem);
        ItemUtils.addLore(nextItem, "&r", "&dТребования:");
        inventory.setItem(2, ItemUtils.addLore(nextItem, upgradeLore));
        p2.openInventory(inventory);
    }

    @Override
    public int getRows() {
        return 0;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Прокачка предмета";
    }
}
