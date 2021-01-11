package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.runaway.Item;
import org.runaway.donate.Donate;
import org.runaway.donate.Privs;
import org.runaway.enums.MoneyType;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;

public class PrivilageMenu implements IMenus {

    private void buy(Privs priv, Player player) {
        player.closeInventory();
        player.sendMessage(Utils.colored("&fЦена: &b&l" + priv.getPrice() + " " + MoneyType.RUBLES.getShortName()));
        player.sendMessage(Utils.colored("&7• &e&n" + Vars.getSite()));
    }

    public PrivilageMenu(Player player) {
        StandardMenu menu = StandardMenu.create(getRows(), getName());

        Privs.icons.forEach((privs, itemStack) -> {
            IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(itemStack).setSlot(privs.getSlot());
            btn.setClickEvent(e -> buy(privs, e.getWhoClicked()));
            menu.addButton(btn);
        });

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