package org.runaway.menu.type;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.runaway.menu.IMenu;

public class StandardMenu extends IMenu {

    boolean created;
    private StandardMenu(int rows, String title){
        setTitle(title);
        setInventorySize(rows);
    }

    public static StandardMenu create(int rows, String title){
        return new StandardMenu(rows,title);
    }

    @Override
    public Inventory build() {
        if(created) {
            return getInventory();
        }
        created = true;
        setCancelClickEvent(true);
        Inventory inv = Bukkit.createInventory(this, getSize(), getTitle());
        setDummies(inv);
        setInventory(inv);
        return inv;

    }
}
