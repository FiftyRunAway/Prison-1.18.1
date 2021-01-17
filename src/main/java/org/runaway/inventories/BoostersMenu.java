package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.runaway.Gamer;
import org.runaway.Item;
import org.runaway.Prison;
import org.runaway.donate.features.BoosterBlocks;
import org.runaway.donate.features.BoosterMoney;
import org.runaway.menu.button.IMenuButton;
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
        StandardMenu menu = StandardMenu.create(1, "&eВаши активные ускорители");
        int i = 0;
        Gamer gamer = Prison.gamers.get(g.getUniqueId());

        if (gamer.getPrivilege().getValue(new BoosterBlocks()) != null) menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new org.runaway.Item.Builder(Material.GOLD_NUGGET).name("&eУскоритель блоков донатера").lore(loreConstPrivilege(gamer, true)).build().item()).setSlot(i++));
        if (gamer.getPrivilege().getValue(new BoosterMoney()) != null) menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new org.runaway.Item.Builder(Material.IRON_NUGGET).name("&eУскоритель денег донатера").lore(loreConstPrivilege(gamer, false)).build().item()).setSlot(i++));
        if (gamer.isActiveLocalBlocks()) menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new org.runaway.Item.Builder(Material.DIAMOND).name("&dⓁ &eускоритель блоков").lore(loreLocal(gamer, true)).build().item()).setSlot(i++));
        if (gamer.isActiveLocalMoney()) menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new org.runaway.Item.Builder(Material.GOLD_INGOT).name("&dⓁ &eускоритель денег").lore(loreLocal(gamer,false)).build().item()).setSlot(i++));
        if (Prison.gMoney.isActive()) menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new org.runaway.Item.Builder(Material.GOLD_BLOCK).name("&dⒼ &eускоритель денег").lore(loreGlobal(false)).build().item()).setSlot(i++));
        if (Prison.gBlocks.isActive()) menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new org.runaway.Item.Builder(Material.DIAMOND_BLOCK).name("&dⒼ &eускоритель блоков").lore(loreGlobal(true)).build().item()).setSlot(i++));
        menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new org.runaway.Item.Builder(Material.DIAMOND_ORE).name("&cПостоянный &aускоритель денег").lore(loreConst(gamer, false)).build().item()).setSlot(7));
        menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new org.runaway.Item.Builder(Material.GOLD_ORE).name("&cПостоянный &aускоритель блоков").lore(loreConst(gamer, true)).build().item()).setSlot(6));

        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new Item.Builder(Material.BARRIER).name("&cВернуться").build().item()).setSlot(8);
        back.setClickEvent(event -> new MainMenu(event.getWhoClicked()));
        menu.addButton(back);

        g.openInventory(menu.build());
    }

    private static Lore loreGlobal(boolean blocks) {
        return new Lore.BuilderLore()
                .addString("&fВладелец: &e" + (blocks ? Prison.gBlocks.getOwner() : Prison.gMoney.getOwner()))
                .addString("&fОсталось времени: &e" + (blocks ? Utils.formatTime(Prison.gBlocks.getTime()) : Utils.formatTime(Prison.gMoney.getTime())))
                .addString("&fМножитель: &e" + (blocks ? Prison.gBlocks.getMultiplier() : Prison.gMoney.getMultiplier()) + "x")
                .build();
    }

    private static Lore loreLocal(Gamer gamer, boolean blocks) {
        return new Lore.BuilderLore()
                .addString("&fВладелец: &e" + gamer.getGamer())
                .addString("&fЗакончится: &e" + (blocks ? Utils.getlBlocksTime().get(gamer.getGamer()) : Utils.getlMoneyTime().get(gamer.getGamer())))
                .addString("&fМножитель: &e" + (blocks ? Utils.getlBlocksMultiplier().get(gamer.getGamer()) : Utils.getlMoneyMultiplier().get(gamer.getGamer())) + "x")
                .build();
    }

    private static Lore loreConst(Gamer gamer, boolean blocks) {
        return new Lore.BuilderLore()
                .addString("&fМножитель: &e" + (blocks ? gamer.getStatistics(EStat.BOOSTERBLOCKS) : gamer.getStatistics(EStat.BOOSTERMONEY)) + "x")
                .build();
    }

    private static Lore loreConstPrivilege(Gamer gamer, boolean blocks) {
        return new Lore.BuilderLore()
                .addString("&fМножитель: &e" + (blocks ? gamer.getPrivilege().getValue(new BoosterBlocks()) : gamer.getPrivilege().getValue(new BoosterMoney())) + "x")
                .build();
    }

    @Override
    public int getRows() {
        return 1;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Ваши активные ускорители";
    }
}
