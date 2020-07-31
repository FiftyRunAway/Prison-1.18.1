package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.runaway.Item;
import org.runaway.donate.Privs;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;

public class PrivilageMenu implements IMenus {

    public PrivilageMenu(Player player) {
        StandardMenu menu = StandardMenu.create(getRows(), getName());

        Privs.icons.forEach((privs, itemStack) ->
                menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(itemStack).setSlot(privs.getSlot())));

        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new Item.Builder(Material.BARRIER).name("&cВернуться").build().item()).setSlot(26);
        back.setClickEvent(event -> new DonateMenu(event.getWhoClicked()));
        menu.addButton(back);

        player.openInventory(menu.build());
    }

    @Override
    public int getRows() {
        return 3;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Привилегии";
    }
}
