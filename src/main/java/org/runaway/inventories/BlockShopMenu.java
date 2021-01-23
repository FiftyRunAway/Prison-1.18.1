package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.runaway.items.Item;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;
import org.runaway.enums.EConfig;
import org.runaway.enums.MoneyType;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.PagedButton;
import org.runaway.menu.type.PagedMenu;

import java.util.concurrent.atomic.AtomicInteger;

public class BlockShopMenu implements IMenus {

    private static PagedMenu menu;

    public BlockShopMenu(Player player) {
        if (player != null) {
            menu.build();
            menu.open(GamerManager.getGamer(player));
        }
    }

    public void load() {
        menu = PagedMenu.create(getRows(), Utils.colored(getName() + " &8(&a%current%&7/&a%available%&8)"));
        menu.addButton(DefaultButtons.NEXT_PAGE.getButtonOfItemStack(new Item.Builder(Material.BARRIER).name("&aСледующая страница").build().item()).setSlot(53));
        menu.addButton(DefaultButtons.LAST_PAGE.getButtonOfItemStack(new Item.Builder(Material.BARRIER).name("&aПредыдущая страница").build().item()).setSlot(45));
        AtomicInteger i = new AtomicInteger();
        EConfig.SHOP.getConfig().getStringList("shop").forEach(s -> {
            String[] var = s.split(" ");
            Material mat = Material.getMaterial(var[0]);
            double price = Double.parseDouble(var[1]);
            int data = Integer.parseInt(var[2]);
            String name = ChatColor.YELLOW + String.valueOf(var[3]);
            menu.addButton(new PagedButton(new Item.Builder(mat)
                    .data((short) data)
                    .name(name.replaceAll("_", " "))
                    .lore(new Lore.BuilderLore()
                            .addSpace()
                            .addString("&7Цена за 1 шт. &f• &e" + price + " " + MoneyType.RUBLES.getShortName()).build())
                    .build().item()).setSlot(i.getAndIncrement()));
        });
    }

    @Override
    public int getRows() {
        return 6;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Магазин блоков";
    }
}
