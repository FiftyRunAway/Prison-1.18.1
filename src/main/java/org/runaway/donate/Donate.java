package org.runaway.donate;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.runaway.Item;
import org.runaway.utils.Lore;
import org.runaway.enums.MoneyType;

import java.util.HashMap;

public class Donate {

    private String name;
    private Material icon;
    private int amount;
    private int price;
    private boolean temporary;
    private Lore lore;
    private int slot;

    public static HashMap<Donate, DonateIcon> icons = new HashMap<>();

    public Donate(String name, Material icon, int amount, int price, boolean temporary, Lore lore, int slot) {
        this.name = name;
        this.icon = icon;
        this.amount = amount;
        this.price = price;
        this.temporary = temporary;
        this.lore = lore;
        this.slot = slot;
    }

    public ItemStack getIcon() {
        return new Item.Builder(icon).amount(amount).name(name).lore(new Lore.BuilderLore()
                .addString("&fОписание:")
                .addLore(lore)
                .addSpace()
                .addString("&fЦена: &c" + price + " " + MoneyType.RUBLES.getShortName())
                .addString(temporary ? "&cВременный предмет!" : "&cПостоянный предмет")
        .build()).build().item();
    }

    public int getSlot() { return slot; }

    public int getPrice() {
        return price;
    }
}
