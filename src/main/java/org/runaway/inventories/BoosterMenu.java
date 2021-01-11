package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Item;
import org.runaway.Main;
import org.runaway.enums.BoosterType;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.IMenuButton;
import org.runaway.boosters.Serializer;
import org.runaway.enums.EConfig;
import org.runaway.enums.EMessage;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.MenuButton;
import org.runaway.menu.type.StandardMenu;

import java.util.concurrent.atomic.AtomicInteger;

/*
 * Created by _RunAway_ on 23.1.2019
 */

public class BoosterMenu implements IMenus {

    public BoosterMenu(Player player) {
        StandardMenu blocks = StandardMenu.create(3, "&eАктивация ускорителей &7• &eУскорители блоков");
        StandardMenu money = StandardMenu.create(3, "&eАктивация ускорителей &7• &eУскорители денег");
        StandardMenu menu = StandardMenu.create(1, "&eАктивация ускорителей");
        menu.addChild("&eАктивация ускорителя &7• &eУскоритель блоков", blocks);
        menu.addChild("&eАктивация ускорителя &7• &eУскоритель денег", money);

        Gamer gamer = GamerManager.getGamer(player);

        MenuButton bl = DefaultButtons.OPEN.getButtonOfItemStack(new Item.Builder(Material.DIAMOND_BLOCK).name("&eУскорители блоков").build().item(), "&eАктивация ускорителей &7• &eУскорители блоков");
        bl.setSlot(3); bl.setClickEvent(event -> loadMenu(gamer, blocks, BoosterType.BLOCKS));
        menu.addButton(bl);

        MenuButton mn = DefaultButtons.OPEN.getButtonOfItemStack(new Item.Builder(Material.GOLD_BLOCK).name("&eУскорители денег").build().item(), "&eАктивация ускорителей &7• &eУскорители денег");
        mn.setSlot(5); mn.setClickEvent(event -> loadMenu(gamer, money, BoosterType.MONEY));
        menu.addButton(mn);

        MenuButton button = DefaultButtons.RETURN.getButtonOfItemStack(new Item.Builder(Material.BARRIER).name("&cВернуться").build().item());
        button.setSlot(26); blocks.addButton(button); money.addButton(button);

        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new Item.Builder(Material.BARRIER).name("&cВернуться").build().item()).setSlot(8);
        back.setClickEvent(event -> new MainMenu(event.getWhoClicked()));
        menu.addButton(back);

        player.openInventory(menu.build());
    }

    private static void loadMenu(Gamer gamer, StandardMenu menu, BoosterType type) {
        if (EConfig.BOOSTERS.getConfig().contains(gamer.getGamer()) && !EConfig.BOOSTERS.getConfig().getStringList(gamer.getGamer()).isEmpty()) {
            AtomicInteger i = new AtomicInteger(0);
            AtomicInteger ser = new AtomicInteger(0);
            EConfig.BOOSTERS.getConfig().getStringList(gamer.getGamer()).forEach(s -> {
                if (type.name().equals(s.split(" ")[0])) {
                    IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(new Serializer().unserializeBooster(s, i.get(), type)).setSlot(ser.getAndIncrement());
                    menu.addButton(btn);
                }
                i.getAndIncrement();
            });
            gamer.getPlayer().openInventory(menu.build());
        } else gamer.sendMessage(EMessage.NOBOOSTERS);
    }

    @Override
    public int getRows() {
        return 3;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Ускорители";
    }
}
