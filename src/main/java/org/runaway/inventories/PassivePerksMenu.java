package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.runaway.Gamer;
import org.runaway.Item;
import org.runaway.Main;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.passiveperks.EPassivePerk;
import org.runaway.passiveperks.PassivePerks;

import java.util.ArrayList;
import java.util.List;

public class PassivePerksMenu implements IMenus {

    private static StandardMenu menu;

    public static StandardMenu getMenu(Player p) {
        StandardMenu sm = menu;
        Gamer gamer = GamerManager.getGamer(p);

        for (EPassivePerk perk : EPassivePerk.values()) {
            sm.addButton(perk.getPerk().getIcon(gamer).setSlot(perk.getPerk().getSlot()));
        }
        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new Item.Builder(Material.BARRIER).name("&cВернуться").build().item()).setSlot(53);
        back.setClickEvent(event -> new MainMenu(event.getWhoClicked()));
        sm.addButton(back);
        return sm;
    }

    public static void load() {
        menu = StandardMenu.create(6, ChatColor.YELLOW + "Пассивные умения");

        int[] left_arrow = { 48, 47, 39, 30, 29, 23, 21, 12, 11, 3 };
        int[] right_arrow = { 15, 14, 23, 33, 32, 41, 51, 50, 5 };
        for (int f : left_arrow) {
            menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.STICK)
                .name("&a&l←-").build().item()).setSlot(f));
        }
        for (int f : right_arrow) {
            menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.STICK)
                    .name("&a&l-→").build().item()).setSlot(f));
        }
        int level = 2;
        for (int i = 49; i > 0; i = i-9) {
            menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.LOG)
                    .name("&7[&e" + level + " уровень&7]").build().item()).setSlot(i));
            level += 5;
        }
    }

    @Override
    public int getRows() {
        return 6;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Пассивные умения";
    }
}
