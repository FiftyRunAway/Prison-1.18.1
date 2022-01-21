package org.runaway.inventories;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.enums.EButtons;
import org.runaway.enums.EMessage;
import org.runaway.items.ItemManager;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;
import org.runaway.utils.ExampleItems;
import org.runaway.utils.ItemBuilder;

import java.util.List;
import java.util.Objects;

public class RuneMenu implements IMenus {

    public static StandardMenu getMenu(Player player) {
        StandardMenu menu = StandardMenu.create(3, new RuneMenu().getName());

        ItemStack is = EButtons.SEARCH.getItemStack();
        ItemStack glass = ExampleItems.glass(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < 26; i++) {
            if (i == 17) continue;
            menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(glass).setSlot(i));
        }
        menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new ItemBuilder(is)
                        .name("&eИнформация:")
                        .addLoreLine("&r")
                        .addLoreLine("&7Выберите предмет из вашего инвентаря,")
                        .addLoreLine("&7чтобы работать с рунами!")
                .build()).setSlot(8));
        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new ItemBuilder(EButtons.CANCEL.getItemStack()).name("&cСохранить и выйти").build()).setSlot(26);
        back.setClickEvent(event -> event.getWhoClicked().closeInventory());
        menu.addButton(back);

        GamerManager.getGamer(player).setOpenedRunesMenu(menu);
        return menu;
    }

    public static void onRuneClick(InventoryClickEvent event) {
        Gamer g = GamerManager.getGamer((Player) event.getWhoClicked());
        StandardMenu menu = g.getOpenedRunesMenu();
        ItemStack clicked = event.getCurrentItem();

        if (menu.getButton(17) == null) return;
        ItemStack working = menu.getButton(17).getItem();
        List<Rune> runes = RuneManager.getRunes(working);
        Rune rune = RuneManager.getRune(ItemManager.getPrisonItem(clicked).getTechName().replace("Rune", ""));

        RuneManager.RuneType runeType = getType(working.getType());
        if (rune.getType().equals(RuneManager.RuneType.ARMOR)) {
            if (!runeType.equals(RuneManager.RuneType.HELMET) &&
                    !runeType.equals(RuneManager.RuneType.CHESTPLATE) &&
                    !runeType.equals(RuneManager.RuneType.LEGGINGS) &&
                    !runeType.equals(RuneManager.RuneType.BOOTS)) {
                g.sendMessage(EMessage.ISNOTSUITABLERUNE);
                return;
            }
        } else {
            if (!rune.getType().equals(runeType)) {
                g.sendMessage(EMessage.ISNOTSUITABLERUNE);
                return;
            }
        }
        if (rune.getRarity().getSlot() > runes.size()) {
            g.sendMessage(EMessage.ISNOTSUITABLERUNE);
            return;
        }
        for (Rune r : runes) {
            if (r == null) continue;
            if (r.getRarity().equals(rune.getRarity())) {
                g.sendMessage("&cСначала уберите поставленную руну этой редкости!");
                return;
            }
        }
        g.getOpenedRunesMenu().addButton(DefaultButtons.FILLER.getButtonOfItemStack(
                RuneManager.addRune(working, rune)).setSlot(17));
        g.sendMessage("&aВы поставили руну " + RuneManager.getRuneName(rune) + " &aна ваш предмет!");
        g.getOpenedRunesMenu().addButton(getRuneBtn(rune, working, true));
        for (ItemStack s : g.getPlayer().getInventory().getContents()) {
            if (s == null) continue;
            if (s.equals(clicked)) {
                if (s.getAmount() == 1) {
                    g.getPlayer().getInventory().remove(s);
                } else {
                    s.setAmount(s.getAmount() - 1);
                }
                g.setOpenedRunesMenu(menu);
                return;
            }
        }
    }

    public static void onClick(InventoryClickEvent event) {
        Gamer g = GamerManager.getGamer((Player) event.getWhoClicked());
        StandardMenu menu = g.getOpenedRunesMenu();
        if (menu.getButton(17) != null) {
            g.getPlayer().getInventory().addItem(menu.getButton(17).getItem());
        }
        ItemStack clicked = event.getCurrentItem();
        ItemStack f = ExampleItems.glass(Material.GRAY_STAINED_GLASS_PANE);
        for (int i = 0; i < 27; i++) {
            if (i == 8 || i == 17 || i == 26) continue;
            menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(f.clone()).setSlot(i));
        }
        menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(event.getCurrentItem()).setSlot(17));

        List<Rune> runes = RuneManager.getRunes(clicked);
        //Take item from player's inventory
        if(clicked.getAmount() == 1) {
            event.getClickedInventory().remove(clicked);
        } else {
            clicked.setAmount(clicked.getAmount() - 1);
        }
        Objects.requireNonNull(event.getClickedInventory())
                .remove(Objects.requireNonNull(clicked));
        ItemStack filler = new ItemBuilder(EButtons.PLUS.getItemStack()).name("&aСвободная ячейка")
                .addLoreLine("&r")
                .addLoreLine("&7Выберите руну этой редкости")
                .addLoreLine("&7в вашем инвентаре!")
                .build();
        int s = 1;
        for (Rune rune : runes) {
            ItemStack pane = ExampleItems.glass(getPane(s), getRarity(s).getName() + " руна");
            menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(pane.clone()).setSlot(s));
            menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(pane.clone()).setSlot(s + 18));

            ++s;
            if (rune == null) {
                menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(filler.clone()).setSlot(s + 8));
                continue;
            }
            menu.addButton(getRuneBtn(rune, clicked, true));
        }
        g.setOpenedRunesMenu(menu);
    }

    private static IMenuButton getRuneBtn(Rune rune, ItemStack clicked, boolean remove) {
        ItemStack is = ItemManager.getPrisonItem(rune.getTechName() + "Rune").getItemStack();
        IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(new ItemBuilder(is)
                .addLoreLine(remove ? "&r" : "")
                .addLoreLine(remove ? "&cНажмите, чтобы достать руну!" : "")
                .build()).setSlot(rune.getRarity().getSlot() + 9);
        ItemStack filler = new ItemBuilder(EButtons.PLUS.getItemStack()).name("&aСвободная ячейка")
                .addLoreLine("&r")
                .addLoreLine("&7Выберите руну этой редкости")
                .addLoreLine("&7в вашем инвентаре!")
                .build();
        if (remove) {
            btn.setClickEvent(e -> {
                Player player = e.getWhoClicked();
                Gamer gamer = GamerManager.getGamer(player);
                if (!gamer.isInventory()) {
                    gamer.sendMessage(EMessage.NOINVENTORY);
                    return;
                }
                gamer.getOpenedRunesMenu().addButton(DefaultButtons.FILLER.getButtonOfItemStack(
                        RuneManager.removeRune(gamer.getOpenedRunesMenu().getButton(17).getItem(), rune)).setSlot(17));
                player.getInventory().addItem(ItemManager.getPrisonItem(rune.getTechName() + "Rune").getItemStack());
                gamer.getOpenedRunesMenu().addButton(DefaultButtons.FILLER.getButtonOfItemStack(filler.clone()).setSlot(e.getSlot()));
                gamer.sendMessage("&cВы достали руну " + RuneManager.getRuneName(rune) + " &cиз вашего предмета!");
            });
        } else {
            btn.setClickEvent(e -> {
                Player player = e.getWhoClicked();
                Gamer gamer = GamerManager.getGamer(player);
                gamer.getOpenedRunesMenu().addButton(DefaultButtons.FILLER.getButtonOfItemStack(
                        RuneManager.addRune(gamer.getOpenedRunesMenu().getButton(17).getItem(), rune)).setSlot(17));
                gamer.sendMessage("&aВы поставили руну " + RuneManager.getRuneName(rune) + " &aна ваш предмет!");
                gamer.getOpenedRunesMenu().addButton(getRuneBtn(rune, gamer.getOpenedRunesMenu().getButton(17).getItem(), true));
                ItemStack ru = ItemManager.getPrisonItem(rune.getTechName() + "Rune").getItemStack();
                if(ru.getAmount() == 1) {
                    gamer.getPlayer().getInventory().remove(ru);
                } else {
                    ru.setAmount(ru.getAmount() - 1);
                }
            });
        }
        return btn;
    }

    private static Material getPane(int runeNumber) {
        switch (runeNumber) {
            case 1 -> {
                return Material.WHITE_STAINED_GLASS_PANE;
            }
            case 2 -> {
                return Material.GREEN_STAINED_GLASS_PANE;
            }
            case 3 -> {
                return Material.MAGENTA_STAINED_GLASS_PANE;
            }
            case 4 -> {
                return Material.ORANGE_STAINED_GLASS_PANE;
            }
            default -> {
                return Material.BARRIER;
            }
        }
    }

    private static RuneManager.RuneRarity getRarity(int runeNumber) {
        switch (runeNumber) {
            case 1 -> {
                return RuneManager.RuneRarity.COMMON;
            }
            case 2 -> {
                return RuneManager.RuneRarity.RARE;
            }
            case 3 -> {
                return RuneManager.RuneRarity.EPIC;
            }
            case 4 -> {
                return RuneManager.RuneRarity.LEGENDARY;
            }
            default -> {
                return null;
            }
        }
    }

    private static RuneManager.RuneType getType(Material material) {
        String type = material.name();
        if (type.endsWith("BOOTS")) return RuneManager.RuneType.BOOTS;
        if (type.endsWith("LEGGINGS")) return RuneManager.RuneType.LEGGINGS;
        if (type.endsWith("CHESTPLATE")) return RuneManager.RuneType.CHESTPLATE;
        if (type.endsWith("HELMET")) return RuneManager.RuneType.HELMET;
        if (type.endsWith("SWORD")) return RuneManager.RuneType.SWORD;
        if (type.endsWith("PICKAXE")) return RuneManager.RuneType.PICKAXE;
        if (type.endsWith("AXE")) return RuneManager.RuneType.AXE;
        if (type.endsWith("BOW")) return RuneManager.RuneType.BOW;
        return RuneManager.RuneType.ARMOR;
    }

    @Override
    public int getRows() {
        return 3;
    }

    @Override
    public String getName() {
        return "&eМастер рун";
    }
}
