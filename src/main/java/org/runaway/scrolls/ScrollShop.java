package org.runaway.scrolls;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.items.Item;
import org.runaway.inventories.Confirmation;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;

import java.util.Arrays;

public enum ScrollShop {
    FIRE_SET("Магия огня",  "scroll", 500, 10, new Lore.BuilderLore()
            .addString("&7- &cМагический посох огня")
            .addString("&7- &eСвиток магии огня")
            .build(), Material.FIREBALL, new Item[] {
            new Item.Builder(Material.BLAZE_ROD).name("&cМагический посох огня").build(),
            new Item.Builder(Material.BLAZE_POWDER).name("&eСвиток магии огня").build()}),
    WIND_SET("Магия ветра",  "scroll", 550, 12, new Lore.BuilderLore()
            .addString("&7- &cМагический посох ветра")
            .addString("&7- &eСвиток магии ветра")
            .build(), Material.NETHER_STAR, new Item[] {
            new Item.Builder(Material.BONE).name("&cМагический посох ветра").build(),
            new Item.Builder(Material.PAPER).name("&eСвиток магии ветра").build()}),
    LAND_SET("Магия земли",  "scroll", 575, 14, new Lore.BuilderLore()
            .addString("&7- &cМагический посох земли")
            .addString("&7- &eСвиток магии земли")
            .build(), Material.DIRT, new Item[] {
            new Item.Builder(Material.SAPLING).name("&cМагический посох земли").build(),
            new Item.Builder(Material.MAGMA_CREAM).name("&eСвиток магии земли").build()}),
    WATER_SET("Магия воды",  "scroll", 600, 16, new Lore.BuilderLore()
            .addString("&7- &cМагический посох воды")
            .addString("&7- &eСвиток магии воды")
            .build(), Material.WATER_BUCKET, new Item[] {
            new Item.Builder(Material.STICK).name("&cМагический посох воды").build(),
            new Item.Builder(Material.LINGERING_POTION).name("&eСвиток магии воды").build()});

    private String name;
    private String type;
    private int price;
    private Lore lore;
    private Material icon;
    private Item[] drop;
    private int slot;

    ScrollShop(String name, String type, int price, int slot, Lore items, Material icon, Item[] drop) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.lore = items;
        this.icon = icon;
        this.drop = drop;
        this.slot = slot;
    }

    public static StandardMenu getMenu(Player player) {
        StandardMenu menu = StandardMenu.create(2, Utils.colored("&eМагазин свитков"));
        Gamer gamer = GamerManager.getGamer(player);
        ItemStack main = new Item.Builder(Material.GOLD_INGOT).name("&eИнформация").lore(new Lore.BuilderLore()
                .addString("&cВнимание: Свитки и посохи можно")
                .addString("&cиспользовать бесконечно, но")
                .addString("&cодновременно можно использовать")
                .addString("&cтолько ОДИН свиток!")
                .addSpace()
                .addString("&fУ вас свитков &7• &e" + gamer.getIntStatistics(EStat.SCROLLS)).build()).build().item();
        menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(main).setSlot(4));
        Arrays.stream(values()).forEach(s -> {
            Lore lore = new Lore.BuilderLore()
                    .addString("&fСостав набора:")
                    .addLore(s.lore)
                    .addSpace()
                    .addString("&fЦена &7• &f" + s.price + " свитков").build();
            IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(s.icon).name(s.name).lore(lore).build().item()).setSlot(s.slot);
            btn.setClickEvent(event -> {
                if (gamer.getIntStatistics(EStat.SCROLLS) >= s.price) {
                    if (s.type.equalsIgnoreCase("scroll")) {
                        if (!gamer.isInventory()) {
                            gamer.sendMessage(EMessage.NOINVENTORY);
                            return;
                        }
                        new Confirmation(event.getWhoClicked(), menu.build(), null, () -> {
                            Arrays.stream(s.drop).forEach(item -> player.getInventory().addItem(item.item()));
                            gamer.setStatistics(EStat.SCROLLS, gamer.getIntStatistics(EStat.SCROLLS) - s.price);
                            gamer.sendMessage(EMessage.SUCCESSFULBUY);
                        });
                    }
                } else {
                    gamer.sendMessage(EMessage.NOSCROLLS);
                }
                player.closeInventory();
            });
            menu.addButton(btn);
        });
        return menu;
    }
}
