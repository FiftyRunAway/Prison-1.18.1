package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.enums.EButtons;
import org.runaway.items.Item;
import org.runaway.Prison;
import org.runaway.donate.features.BoosterBlocks;
import org.runaway.donate.features.BoosterMoney;
import org.runaway.items.ItemManager;
import org.runaway.managers.GamerManager;
import org.runaway.menu.UpdateMenu;
import org.runaway.menu.button.IMenuButton;
import org.runaway.utils.ItemBuilder;
import org.runaway.utils.ItemUtils;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;
import org.runaway.enums.EStat;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.type.StandardMenu;

/*
 * Created by _RunAway_ on 1.2.2019
 */

public class BoostersMenu implements IMenus {

    public BoostersMenu(Player g) {
        StandardMenu menu = StandardMenu.create(1, "&eАктивные ускорители");
        Gamer gamer = GamerManager.getGamer(g);

        int i = 0;
        if (gamer.getPrivilege().getValue(new BoosterBlocks()) != null) menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.GOLD_NUGGET).name("&eУскоритель блоков донатера").lore(loreConstPrivilege(gamer, true)).build().item()).setSlot(i++));
        if (gamer.getPrivilege().getValue(new BoosterMoney()) != null) menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.IRON_NUGGET).name("&eУскоритель денег донатера").lore(loreConstPrivilege(gamer, false)).build().item()).setSlot(i++));
        if (gamer.isActiveLocalBlocks()) menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.DIAMOND).name("&6Локальный &eускоритель блоков").lore(loreLocal(gamer, true)).build().item()).setSlot(i++));
        if (gamer.isActiveLocalMoney()) menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.GOLD_INGOT).name("&6Локальный &eускоритель денег").lore(loreLocal(gamer,false)).build().item()).setSlot(i++));
        if (Prison.gMoney.isActive()) {
            IMenuButton gMoney = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.GOLD_BLOCK).name("&6Глобальный &eускоритель денег").lore(loreGlobal(false)).build().item()).setSlot(i++);
            menu.addButton(gMoney);

            UpdateMenu.builder().boostersUpdate(true).isBoosterBlocks(false).gamerLive(gamer).build().update(menu, gMoney);
        }
        if (Prison.gBlocks.isActive()) {
            IMenuButton gBlocks = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.DIAMOND_BLOCK).name("&6Глобальный &eускоритель блоков").lore(loreGlobal(true)).build().item()).setSlot(i++);
            menu.addButton(gBlocks);

            UpdateMenu.builder().boostersUpdate(true).isBoosterBlocks(true).gamerLive(gamer).build().update(menu, gBlocks);
        }
        menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.DIAMOND_ORE).name("&aПостоянный ускоритель денег").lore(loreConst(gamer, false)).build().item()).setSlot(7));
        menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.GOLD_ORE).name("&aПостоянный ускоритель блоков").lore(loreConst(gamer, true)).build().item()).setSlot(6));
        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(
                new ItemBuilder(EButtons.CANCEL.getItemStack()).build()).setSlot(8);
        back.setClickEvent(event -> new MainMenu(event.getWhoClicked()));
        menu.addButton(back);

        menu.open(gamer);
    }

    public static Lore loreGlobal(boolean blocks) {
        return new Lore.BuilderLore()
                .addString("&fВладелец &7• &e" + (blocks ? Prison.gBlocks.getOwner() : Prison.gMoney.getOwner()))
                .addString("&fОсталось времени &7• &e" + (blocks ? Utils.formatTime(Prison.gBlocks.getTime()) : Utils.formatTime(Prison.gMoney.getTime())))
                .addString("&fМножитель &7• &e" + (blocks ? Prison.gBlocks.getMultiplier() : Prison.gMoney.getMultiplier()) + "x")
                .build();
    }

    private static Lore loreLocal(Gamer gamer, boolean blocks) {
        return new Lore.BuilderLore()
                .addString("&fВладелец &7• &e" + gamer.getGamer())
                .addString("&fЗакончится &7• &e" + (blocks ? Utils.getlBlocksTime().get(gamer.getGamer()) : Utils.getlMoneyTime().get(gamer.getGamer())))
                .addString("&fМножитель &7• &e" + (blocks ? Utils.getlBlocksMultiplier().get(gamer.getGamer()) : Utils.getlMoneyMultiplier().get(gamer.getGamer())) + "x")
                .build();
    }

    private static Lore loreConst(Gamer gamer, boolean blocks) {
        return new Lore.BuilderLore()
                .addString("&fМножитель &7• &e" + (blocks ? gamer.getStatistics(EStat.BOOSTERBLOCKS) : gamer.getStatistics(EStat.BOOSTERMONEY)) + "x")
                .build();
    }

    private static Lore loreConstPrivilege(Gamer gamer, boolean blocks) {
        return new Lore.BuilderLore()
                .addString("&fМножитель &7• &e" + (blocks ? gamer.getPrivilege().getValue(new BoosterBlocks()) : gamer.getPrivilege().getValue(new BoosterMoney())) + "x")
                .build();
    }

    @Override
    public int getRows() {
        return 1;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Активные ускорители";
    }
}
