package org.runaway.menu;

import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.runaway.enums.EMessage;
import org.runaway.inventories.RuneMenu;
import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.ButtonOptions;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.events.ButtonClickEvent;
import org.runaway.menu.events.ClickType;
import org.runaway.runes.utils.RuneManager;
import org.runaway.utils.Utils;

import java.util.function.Consumer;

public class MenuListener implements Listener {

    private static boolean initialized;

    public MenuListener(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        initialized = true;
    }

    public MenuListener() {};

    public static void register(JavaPlugin plugin) {

        if (!initialized)
            new MenuListener(plugin);

    }

    @EventHandler
    public void listenClick(InventoryClickEvent event) {
        try {
            Inventory inventory = event.getClickedInventory();
            if (inventory == null) return;

            InventoryHolder holder = inventory.getHolder();

            if (!(holder instanceof IMenu)) {
                //For RunesMenu
                if (event.getView().getTopInventory().isEmpty()) return;
                if (!event.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;
                if (!event.getView().getTitle().equalsIgnoreCase(Utils.colored(new RuneMenu().getName()))) {
                    if (!event.getView().getTitle().equals("Chest") && (!event.getView().getTitle().equals("Ender Chest"))) {
                        event.setCancelled(true);
                    }
                    return;
                }

                ItemStack is = event.getCurrentItem();
                if (is == null) return;
                if (is.getItemMeta() == null) return;
                PrisonItem pi = ItemManager.getPrisonItem(is);
                if (pi == null) return;
                if (pi.getTechName().endsWith("Rune")) {
                    RuneMenu.onRuneClick(event);
                } else {
                    if (pi.getMutableParameters() == null) return;
                    if (!RuneManager.getRunes(is).isEmpty()) {
                        RuneMenu.onClick(event);
                    }
                }
                event.setCancelled(true);
                return;
            }

            final ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

            IMenu menu = (IMenu) holder;
            IMenuButton clickedButton = menu.findButtonByItem(clickedItem);

            if (clickedButton != null) {
                if (clickedButton.containsData(ButtonOptions.CANCEL_EVENT.getIdentifier())) {
                    if (((boolean) clickedButton.getItemData().get(ButtonOptions.CANCEL_EVENT.getIdentifier()))) {
                        event.setCancelled(true);
                    }
                } else event.setCancelled(true);

                ClickType clickType;
                if (event.isShiftClick()) {
                    if (event.isLeftClick())
                        clickType = ClickType.SHIFT_LEFT;
                    else
                        clickType = ClickType.SHIFT_RIGHT;
                } else {
                    if (event.isLeftClick())
                        clickType = ClickType.LEFT;
                    else
                        clickType = ClickType.RIGHT;
                }
                ButtonClickEvent buttonClickEvent = new ButtonClickEvent(clickedButton, ((Player) event.getWhoClicked()), event.getClickedInventory(),
                        event.getSlot(), clickType);

                if (clickedButton.getClickEvent() != null) {
                    clickedButton.getClickEvent().accept(buttonClickEvent);
                } else if (menu.getClickListener() != null)
                    menu.getClickListener().accept(buttonClickEvent);
            } else {
                if(menu.getItemClickEvent() != null) menu.getItemClickEvent().accept(event);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            GamerManager.getGamer(event.getWhoClicked().getUniqueId()).sendMessage(EMessage.ERRORELEMENT);
            event.getWhoClicked().closeInventory();
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {

        InventoryHolder holder = event.getInventory().getHolder();

        if (!(holder instanceof IMenu))
            return;

        IMenu menu = (IMenu) holder;

        Consumer<InventoryCloseEvent> closeListener = menu.getCloseListener();

        if (closeListener != null)
            closeListener.accept(event);

    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {

        InventoryHolder holder = event.getInventory().getHolder();

        if (!(holder instanceof IMenu))
            return;

        IMenu menu = (IMenu) holder;

        Consumer<InventoryOpenEvent> openListener = menu.getOpenListener();

        if (openListener != null)
            openListener.accept(event);

    }

    @EventHandler
    public void onPickup(InventoryPickupItemEvent event){

        InventoryHolder holder = event.getInventory().getHolder();

        if (!(holder instanceof IMenu))
            return;

        IMenu menu = (IMenu) holder;

        Consumer<InventoryPickupItemEvent> pickupListener = menu.getPickupListener();
        if(pickupListener != null){
            pickupListener.accept(event);
        }
    }
}
