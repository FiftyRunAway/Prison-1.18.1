package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.items.ItemManager;
import org.runaway.items.parameters.ParameterMeta;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.requirements.RequireList;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;
import org.runaway.upgrades.Upgrade;
import org.runaway.utils.ExampleItems;
import org.runaway.enums.EMessage;
import org.runaway.utils.ItemUtils;

import java.util.List;

/*
 * Created by _RunAway_ on 4.5.2019
 */

public class UpgradeMenu implements IMenus {

    public UpgradeMenu(Player p2, String nextPrisonItem, RequireList requireList) {
        StandardMenu menu = StandardMenu.create(getRows(), getName());
        menu.setType(InventoryType.WORKBENCH);

        IMenuButton button = DefaultButtons.FILLER.getButtonOfItemStack(ExampleItems.glass(Material.LIME_STAINED_GLASS_PANE, "&a&lУлучшить")).setSlot(5);
        button.setClickEvent(event ->
                Upgrade.upgrade(event.getWhoClicked(), false));
        menu.addButton(button);

        button = DefaultButtons.FILLER.getButtonOfItemStack(ExampleItems.glass(Material.RED_STAINED_GLASS_PANE, "&c&lОтменить улучшение"));
        button.setClickEvent(event ->
                event.getWhoClicked().closeInventory());
        for (int i = 1; i < 10; i++) {
            if (i == 5) continue;
            menu.addButton(button.clone().setSlot(i));
        }

        Gamer gamer = GamerManager.getGamer(p2);
        if (p2.getInventory().getItemInMainHand().getItemMeta() == null || p2.getInventory().getItemInMainHand().getItemMeta().getLore() == null || p2.getInventory().getItemInMainHand().getItemMeta().getLore().isEmpty()) {
            gamer.sendMessage(EMessage.NOLOREUPGRADE);
            return;
        }
        List<String> upgradeLore = requireList.getLore(gamer);
        ItemStack nextItem = ItemManager.getPrisonItem(nextPrisonItem).getItemStack();
        nextItem = new ParameterMeta(p2.getInventory().getItemInMainHand()).applyTo(nextItem);
        for (Rune rune : RuneManager.getRunes(p2.getInventory().getItemInMainHand())) {
            nextItem = RuneManager.addRune(nextItem, rune);
        }
        ItemUtils.addLore(nextItem, "&r", "&dТребования:");

        menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(ItemUtils.addLore(nextItem, upgradeLore)).setSlot(0));
        menu.open(GamerManager.getGamer(p2));
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
