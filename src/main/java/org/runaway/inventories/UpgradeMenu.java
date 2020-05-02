package org.runaway.inventories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.utils.ExampleItems;
import org.runaway.enums.EMessage;
import org.runaway.upgrades.UpgradeMisc;

/*
 * Created by _RunAway_ on 4.5.2019
 */

public class UpgradeMenu implements IMenus {

    public UpgradeMenu(Player p2) {
        Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, getName());
        inventory.setItem(0, ExampleItems.glass(7));
        inventory.setItem(1, ExampleItems.glass(5, ChatColor.GREEN + "" + ChatColor.BOLD + "УЛУЧШИТЬ"));
        inventory.setItem(3, ExampleItems.glass(14, ChatColor.RED + "" + ChatColor.BOLD + "ОТМЕНИТЬ"));
        inventory.setItem(4, ExampleItems.glass(7));
        Gamer gamer = Main.gamers.get(p2.getUniqueId());
        if (p2.getInventory().getItemInMainHand().getItemMeta() == null || p2.getInventory().getItemInMainHand().getItemMeta().getLore() == null || p2.getInventory().getItemInMainHand().getItemMeta().getLore().size() < 1) {
            gamer.sendMessage(EMessage.NOLOREUPGRADE);
            return;
        }
        if (p2.getInventory().getItemInMainHand().getItemMeta().getLore().get(1).contains("Аукционный предмет")) {
            gamer.sendMessage(EMessage.NOLOREUPGRADE);
            return;
        }
        String next = UpgradeMisc.getNext(UpgradeMisc.getSection(p2));
        if (next == null) {
            gamer.sendMessage(EMessage.MAXLEVELUPGRADE);
            return;
        }
        inventory.setItem(2, UpgradeMisc.buildItem(next, true, p2, false));
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
