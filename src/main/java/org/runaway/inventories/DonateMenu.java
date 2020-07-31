package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.runaway.Item;
import org.runaway.Main;
import org.runaway.donate.Donate;
import org.runaway.enums.MoneyType;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.button.MenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;

public class DonateMenu implements IMenus {

    private void buy(Donate donate, Player player) {
        player.sendMessage(Utils.colored("&fЦена: &c" + donate.getPrice() + " " + MoneyType.RUBLES.getShortName()));
        player.sendMessage(Utils.colored("&7>> &b" + Vars.getSite()));
        player.closeInventory();
    }

    public DonateMenu(Player player) {
        StandardMenu menu = StandardMenu.create(getRows(), getName());
        for (int i = 0; i < Main.value_donate; i++) {
            Donate donate = (Donate) Utils.donate.get(i);
            MenuButton mn = DefaultButtons.OPEN.getButtonOfItemStack(Donate.icons.get(donate).icon());
            mn.setSlot(donate.getSlot()); mn.setClickEvent(event -> buy(donate, player));
            menu.addButton(mn);
        }
        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new Item.Builder(Material.BARRIER).name("&cВернуться").build().item()).setSlot(44);
        back.setClickEvent(event -> new MainMenu(event.getWhoClicked()));
        menu.addButton(back);

        IMenuButton privs = DefaultButtons.RETURN.getButtonOfItemStack(new Item.Builder(Material.DIAMOND).name("&aДонат-группы").build().item()).setSlot(40);
        privs.setClickEvent(event -> new PrivilageMenu(event.getWhoClicked()));
        menu.addButton(privs);

        player.openInventory(menu.build());
    }

    @Override
    public int getRows() {
        return 5;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Меню доната";
    }
}
