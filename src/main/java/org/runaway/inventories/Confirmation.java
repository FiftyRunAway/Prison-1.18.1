package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.runaway.Main;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.utils.ExampleItems;

import java.util.Arrays;
import java.util.HashMap;

public class Confirmation {

    public static HashMap<String, Boolean> inMenu;

    private static int[] close_buttons, accept_buttons;
    private static ItemStack close_btn, accept_btn;

    private static StandardMenu confirmed, closed;

    Confirmation(Player player, Inventory startMenu, Inventory redirectMenu, Runnable runnable) {
        StandardMenu menu = StandardMenu.create(5, ChatColor.YELLOW + "" + ChatColor.ITALIC + "Вы уверены?");

        // Closing confirmation
        IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(close_btn);
        btn.setClickEvent(event -> clickRun(event.getWhoClicked(), false, runnable, startMenu, redirectMenu));
        Arrays.stream(close_buttons).forEach(i -> menu.addButton(btn.clone().setSlot(i)));

        // Accepting confirmation
        IMenuButton btn_confirm = DefaultButtons.FILLER.getButtonOfItemStack(accept_btn);
        btn_confirm.setClickEvent(event -> clickRun(event.getWhoClicked(), true, runnable, startMenu, redirectMenu));
        Arrays.stream(accept_buttons).forEach(i -> menu.addButton(btn_confirm.clone().setSlot(i)));

        player.openInventory(menu.build());
    }

    private void clickRun(Player player, boolean confirm, Runnable runnable, Inventory start, Inventory redirect) {
        if (confirm) {
            runnable.run();
            player.openInventory(confirmed.build());
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        } else {
            player.openInventory(closed.build());
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
        }
        inMenu.put(player.getName(), confirm);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (inMenu.containsKey(player.getName())) {
                    inMenu.remove(player.getName());

                    if (confirm) {
                        if (redirect != null) player.openInventory(redirect);
                        else player.closeInventory();
                    } else {
                        if (start != null) player.openInventory(start);
                        else player.closeInventory();
                    }
                }
            }
        }.runTaskLater(Main.getInstance(), 40L);
    }

    public static void load() {
        inMenu = new HashMap<>();

        close_buttons = new int[] { 14, 15, 16, 23, 24, 25, 32, 33, 34 };
        accept_buttons = new int[] { 10, 11, 12, 19, 20, 21, 28, 29, 30 };
        close_btn = ExampleItems.glass(14, ChatColor.RED + "" + ChatColor.BOLD + "НЕТ");
        accept_btn = ExampleItems.glass(5, ChatColor.GREEN + "" + ChatColor.BOLD + "ДА");

        // Load confirmed part of animation
        confirmed = StandardMenu.create(5, "&o&eВы уверены? &7• &aПодтверждено");
        int[] confirmed_buttons = new int[] { 7, 15, 23, 29, 31, 39 };
        ItemStack confirmed_btn = ExampleItems.glass(5);

        Arrays.stream(confirmed_buttons).forEach(i ->
                confirmed.addButton(DefaultButtons.FILLER.getButtonOfItemStack(confirmed_btn).setSlot(i)));

        // Load closed part of animation
        closed = StandardMenu.create(5, "&o&eВы уверены? &7• &cОтменено");
        int[] closed_buttons = new int[] { 2, 6, 10, 13, 16, 19, 22, 23, 25, 28, 31, 34, 38, 42 };
        ItemStack closed_btn = ExampleItems.glass(14);

        Arrays.stream(closed_buttons).forEach(i ->
                closed.addButton(DefaultButtons.FILLER.getButtonOfItemStack(closed_btn).setSlot(i)));
    }
}
