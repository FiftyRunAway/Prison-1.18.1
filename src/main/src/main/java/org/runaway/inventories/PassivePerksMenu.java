package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.enums.EButtons;
import org.runaway.items.Item;
import org.runaway.managers.GamerManager;
import org.runaway.menu.SimpleItemStack;
import org.runaway.menu.UpdateMenu;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.passiveperks.EPassivePerk;
import org.runaway.utils.ItemBuilder;

import java.util.ArrayList;
import java.util.List;

public class PassivePerksMenu implements IMenus {

    private static List<IMenuButton> preloaded;

    public static StandardMenu getMenu(Player p) {
        StandardMenu menu = StandardMenu.create(6, ChatColor.YELLOW + "Пассивные умения");
        Gamer gamer = GamerManager.getGamer(p);
        preloaded.forEach(menu::addButton);

        for (EPassivePerk perk : EPassivePerk.values()) {
            IMenuButton imb = perk.getPerk().getIcon(gamer).setSlot(perk.getPerk().getSlot());
            menu.addButton(imb);

            if (imb.getItem().getType().equals(Material.YELLOW_STAINED_GLASS_PANE)) UpdateMenu.builder()
                    .updateType(new SimpleItemStack[]{SimpleItemStack.builder()
                            .material(Material.ORANGE_STAINED_GLASS_PANE).build()})
                    .gamerLive(gamer).build().update(menu, imb);
        }
        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new ItemBuilder(EButtons.CANCEL.getItemStack()).build()).setSlot(53);
        back.setClickEvent(event -> new MainMenu(event.getWhoClicked()));
        menu.addButton(back);
        return menu;
    }

    public static void load() {
        preloaded = new ArrayList<>();

        int[] left_arrow = { 48, 47, 39, 30, 29, 23, 21, 12, 11, 3 };
        int[] right_arrow = { 15, 14, 23, 33, 32, 41, 51, 50, 5 };
        for (int f : left_arrow) {
            preloaded.add(DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.STICK)
                .name("&a&l←-").build().item()).setSlot(f));
        }
        for (int f : right_arrow) {
            preloaded.add(DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.STICK)
                    .name("&a&l-→").build().item()).setSlot(f));
        }
        int level = 2;
        for (int i = 49; i > 0; i = i-9) {
            preloaded.add(DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.JUNGLE_LOG)
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
