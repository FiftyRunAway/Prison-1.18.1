package org.runaway.menu;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.runaway.Gamer;
import org.runaway.inventories.BoostersMenu;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.mines.Mines;
import org.runaway.tasks.Cancellable;
import org.runaway.tasks.SyncRepeatTask;
import org.runaway.tasks.SyncTask;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Builder @Getter
public class UpdateMenu {
    private Lore updateLore;
    private SimpleItemStack[] updateType;
    private String updateName;
    private int cooldown;
    private int start;

    private Mines mineBossUpdate;
    private boolean boostersUpdate;
    private boolean isBoosterBlocks;

    private Gamer gamerLive;

    public Cancellable update(StandardMenu menu, IMenuButton button) {
        AtomicInteger stage = new AtomicInteger(0);
        List<SimpleItemStack> ftypes = null;
        if (updateType != null) {
            ftypes = new ArrayList<>(Arrays.asList(updateType));
            ftypes.add(new SimpleItemStack(button.getItem().getType(), button.getItem().getDurability()));
        }
        List<SimpleItemStack> types = ftypes;
        Cancellable cancellable = new SyncRepeatTask(() -> {
            if (menu.getInventory() != null) {
                ItemStack oldItemStack = button.getItem();
                SimpleItemStack newSimpleIs = new SimpleItemStack(oldItemStack.getType(), oldItemStack.getDurability());
                if (types != null) {
                    if (stage.get() < types.size() - 1) {
                        newSimpleIs = types.get(stage.getAndIncrement());
                    } else if (stage.get() == types.size() - 1) {
                        newSimpleIs = types.get(stage.get());
                        stage.set(0);
                    }
                }
                ItemStack newItemStack = oldItemStack.clone();
                newItemStack.setType(newSimpleIs.getMaterial());
                newItemStack.setDurability((short) newSimpleIs.getDurability());
                ItemMeta meta = newItemStack.getItemMeta();
                if (updateLore != null || mineBossUpdate != null || boostersUpdate) {
                    //Live updates
                    if (mineBossUpdate != null) {
                        meta.setLore(mineBossUpdate.getLoreIcon(gamerLive).getList());
                    } else if (boostersUpdate) {
                        meta.setLore(BoostersMenu.loreGlobal(isBoosterBlocks).getList());
                    } else {
                        meta.setLore(updateLore.getList());
                    }
                    newItemStack.setItemMeta(meta);
                }
                if (updateName != null) {
                    meta.setDisplayName(Utils.colored(updateName));
                }
                newItemStack.setItemMeta(meta);
                IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(newItemStack);
                btn.setClickEvent(button.getClickEvent());
                btn.setSlot(button.getSlot());
                btn.setFiller(button.isFiller());

                menu.addButton(btn);
            }
        }, cooldown == 0 ? 20 : cooldown, start);
        if (gamerLive != null) {
            new SyncTask(() -> gamerLive.getUpdatingButtons().add(cancellable), 20);
        }
        return cancellable;
    }
}
