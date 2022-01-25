package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.menu.IMenu;
import org.runaway.utils.ExampleItems;
import org.runaway.utils.ItemBuilder;

import java.util.Arrays;
import java.util.HashMap;

public class Confirmation {

    public static HashMap<String, Boolean> inMenu;

    private static int[] close_buttons, accept_buttons;

    private static StandardMenu confirmed, closed;

    public Confirmation(Player player, Runnable runnable) {
        StandardMenu menu = StandardMenu.create(5, ChatColor.YELLOW + "" + ChatColor.ITALIC + "Вы уверены?");
        Gamer gamer = GamerManager.getGamer(player);
        // Closing confirmation
        IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(new ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                .name("&c&lНЕТ")
                .build());
        btn.setClickEvent(event -> {
            Gamer g = GamerManager.getGamer(event.getWhoClicked());
            clickRun(event.getWhoClicked(), false, runnable, g.getCurrentIMenu());
        });
        Arrays.stream(close_buttons).forEach(i -> menu.addButton(btn.clone().setSlot(i)));

        // Accepting confirmation
        IMenuButton btnConfirm = DefaultButtons.FILLER.getButtonOfItemStack(new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE)
                .name("&a&lДА")
                .build());
        btnConfirm.setClickEvent(event ->
                clickRun(event.getWhoClicked(), true, runnable, gamer.getCurrentIMenu()));
        Arrays.stream(accept_buttons).forEach(i -> menu.addButton(btnConfirm.clone().setSlot(i)));

        menu.open(GamerManager.getGamer(player));
    }

    private void clickRun(Player player, boolean confirm, Runnable runnable, IMenu start) {
        if (confirm) {
            runnable.run();
            confirmed.open(GamerManager.getGamer(player));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        } else {
            closed.open(GamerManager.getGamer(player));
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_FALL, 1, 1);
        }
        inMenu.put(player.getName(), confirm);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (inMenu.containsKey(player.getName())) {
                    inMenu.remove(player.getName());

                    if (confirm) {
                        player.closeInventory();
                    } else {
                        if (start != null) start.open(GamerManager.getGamer(player));
                        else player.closeInventory();
                    }
                }
            }
        }.runTaskLater(Prison.getInstance(), 40L);
    }

    public static void load() {
        inMenu = new HashMap<>();

        close_buttons = new int[] { 14, 15, 16, 23, 24, 25, 32, 33, 34 };
        accept_buttons = new int[] { 10, 11, 12, 19, 20, 21, 28, 29, 30 };

        // Load confirmed part of animation
        confirmed = StandardMenu.create(5, "&o&eВы уверены? &7• &aПодтверждено");
        int[] confirmed_buttons = new int[] { 7, 15, 23, 29, 31, 39 };
        ItemStack confirmed_btn = ExampleItems.glass(Material.LIME_STAINED_GLASS_PANE);

        Arrays.stream(confirmed_buttons).forEach(i ->
                confirmed.addButton(DefaultButtons.FILLER.getButtonOfItemStack(confirmed_btn).setSlot(i)));

        // Load closed part of animation
        closed = StandardMenu.create(5, "&o&eВы уверены? &7• &cОтменено");
        int[] closed_buttons = new int[] { 2, 6, 10, 13, 16, 19, 22, 23, 25, 28, 31, 34, 38, 42 };
        ItemStack closed_btn = ExampleItems.glass(Material.RED_STAINED_GLASS_PANE);

        Arrays.stream(closed_buttons).forEach(i ->
                closed.addButton(DefaultButtons.FILLER.getButtonOfItemStack(closed_btn).setSlot(i)));
    }
}
