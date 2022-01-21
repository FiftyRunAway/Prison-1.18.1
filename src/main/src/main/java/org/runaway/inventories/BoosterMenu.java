package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.boosters.Booster;
import org.runaway.boosters.LBlocks;
import org.runaway.boosters.LMoney;
import org.runaway.enums.EButtons;
import org.runaway.items.Item;
import org.runaway.enums.BoosterType;
import org.runaway.enums.EStat;
import org.runaway.items.ItemManager;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.IMenuButton;
import org.runaway.boosters.Serializer;
import org.runaway.enums.EMessage;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.MenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.utils.ItemBuilder;

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

        MenuButton button = DefaultButtons.RETURN.getButtonOfItemStack(new ItemBuilder(EButtons.CANCEL.getItemStack()).build());
        button.setSlot(26); blocks.addButton(button); money.addButton(button);

        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new ItemBuilder(EButtons.CANCEL.getItemStack()).build()).setSlot(8);
        back.setClickEvent(event -> new MainMenu(event.getWhoClicked()));
        menu.addButton(back);

        menu.open(gamer);
    }

    private static void loadMenu(Gamer gamer, StandardMenu menu, BoosterType type) {
        if (gamer.getStringStatistics(EStat.BOOSTERS) != null) {
            AtomicInteger i = new AtomicInteger(0);
            AtomicInteger ser = new AtomicInteger(0);
            gamer.getBoosters().forEach(s -> {
                if (type.name().equals(s.split("-")[0].toUpperCase())) {
                    IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(Serializer.unserializeBooster(s, i.get(), type)).setSlot(ser.getAndIncrement());
                    btn.setClickEvent(event -> {
                        Gamer g = GamerManager.getGamer(event.getWhoClicked());
                        if (event.getClickedButton().getItem().getType().isBlock()) {
                            Booster booster = type == BoosterType.MONEY ? Prison.gMoney : Prison.gBlocks;
                            if (!booster.isActive()) {
                                String[] var = Serializer.unserial(event.getClickedButton().getItem(), g, type).split("-");
                                booster.start(g.getGamer(), Long.parseLong(var[1]), Double.parseDouble(var[0]));
                                event.getWhoClicked().closeInventory();
                            } else {
                                g.sendMessage(EMessage.BOOSTERALREADYACTIVE);
                            }
                        } else {
                            if ((type == BoosterType.BLOCKS && g.isActiveLocalBlocks()) ||
                                    (type == BoosterType.MONEY && g.isActiveLocalMoney())) {
                                g.sendMessage(EMessage.BOOSTERALREADYACTIVE);
                            } else {
                                Booster booster = type == BoosterType.MONEY ? new LMoney() : new LBlocks();
                                String[] var = Serializer.unserial(event.getClickedButton().getItem(), g, type).split("-");
                                booster.start(g.getGamer(), Long.parseLong(var[1]), Double.parseDouble(var[0]));
                                event.getWhoClicked().closeInventory();
                            }
                        }
                    });
                    menu.addButton(btn);
                }
                i.getAndIncrement();
            });
            menu.open(gamer);
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
