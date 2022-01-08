package org.runaway.inventories;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.runaway.Gamer;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.tasks.Cancellable;
import org.runaway.tasks.SyncRepeatTask;
import org.runaway.utils.Items;
import org.runaway.utils.Lore;

public abstract class IMenu {

     public abstract StandardMenu build();

     void open(Gamer gamer) {
         gamer.getPlayer().openInventory(build().getInventory());
     }

     public static Cancellable updateType(StandardMenu menu, IMenuButton oneButton, ItemStack itemStack, Lore lore) {
         final boolean[] stage = {true};
         return new SyncRepeatTask(() -> {
             if (menu.getInventory() != null) {
                 if (stage[0]) {
                     stage[0] = false;
                 } else stage[0] = true;
                 ItemStack is = oneButton.getItem().clone();
                 is.setType(stage[0] ? is.getType() : itemStack.getType());
                 is.setDurability(itemStack.getDurability());
                 IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(is);
                 btn.setClickEvent(oneButton.getClickEvent());
                 btn.setSlot(oneButton.getSlot());
                 btn.setFiller(oneButton.isFiller());
                 menu.addButton(btn);
             }
         }, 20, 20);
     }

     public static Cancellable updateType(StandardMenu menu, IMenuButton oneButton, Material material) {
         return updateType(menu, oneButton, new ItemStack(material, 1, (short)0), null);
     }
}
