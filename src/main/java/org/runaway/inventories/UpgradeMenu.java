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
import org.runaway.items.PrisonItem;
import org.runaway.items.parameters.ParameterMeta;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.requirements.RequireList;
import org.runaway.upgrades.Upgrade;
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
        StandardMenu menu = StandardMenu.create(getRows(), getName());
        menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(ItemManager.getPrisonItem("glass7").getItemStack()).setSlot(0));
        menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(ItemManager.getPrisonItem("glass7").getItemStack()).setSlot(1));
        menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(ItemManager.getPrisonItem("glass7").getItemStack()).setSlot(2));
        menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(ItemManager.getPrisonItem("glass7").getItemStack()).setSlot(6));
        menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(ItemManager.getPrisonItem("glass7").getItemStack()).setSlot(7));
        menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(ItemManager.getPrisonItem("glass7").getItemStack()).setSlot(8));

        PrisonItem pi = ItemManager.getPrisonItem("glass_upgrade");
        pi.setName("&a&lУЛУЧШИТЬ");
        IMenuButton button = DefaultButtons.FILLER.getButtonOfItemStack(pi.getItemStack()).setSlot(3);
        button.setClickEvent(event ->
                Upgrade.upgrade(event.getWhoClicked(), false));
        menu.addButton(button);

        button = DefaultButtons.FILLER.getButtonOfItemStack(ItemManager.getPrisonItem("glass_exit").getItemStack()).setSlot(5);
        button.setClickEvent(event ->
                event.getWhoClicked().closeInventory());
        menu.addButton(button);

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

        menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(ItemUtils.addLore(nextItem, upgradeLore)).setSlot(4));
        p2.openInventory(menu.build());
    }

    @Override
    public int getRows() {
        return 1;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Прокачка предмета";
    }
}
