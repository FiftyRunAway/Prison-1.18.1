package org.runaway.inventories;

import org.runaway.Gamer;
import org.runaway.menu.type.StandardMenu;

public abstract class IMenu {

     public abstract StandardMenu build();

     void open(Gamer gamer) {
         gamer.getPlayer().openInventory(build().getInventory());
     }
}
