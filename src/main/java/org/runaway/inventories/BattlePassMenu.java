package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.Item;
import org.runaway.Main;
import org.runaway.battlepass.BattlePass;
import org.runaway.battlepass.WeeklyMission;
import org.runaway.enums.EConfig;
import org.runaway.enums.EStat;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.utils.ExampleItems;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;

import java.util.concurrent.atomic.AtomicInteger;

public class BattlePassMenu implements IMenus {

    private static StandardMenu missionsMenu;
    private static StandardMenu main;
    private static int max_level;
    private static double pages;

    BattlePassMenu(Player player) {
        StandardMenu menu = main;
        Gamer gamer = Main.gamers.get(player.getUniqueId());

        IMenuButton exp = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.EXP_BOTTLE)
                .name(ChatColor.AQUA + "" + gamer.getStatistics(EStat.BATTLEPASS_LEVEL) + " &eуровень боевого пропуска")
                .lore(new Lore.BuilderLore()
                        .addString("            &7Опыт:")
                        .addString(progresBarLevel(gamer))
                        .addString("&70                     " + BattlePass.level)
                        .build())
                .build().item()).setSlot(49);
        menu.addButton(exp);

        openPage(0, gamer);

        if (pages > 1) {
            IMenuButton next = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.BARRIER).name("&aВперёд").build().item()).setSlot(53);
            next.setClickEvent(event -> openPage(1, Main.gamers.get(event.getWhoClicked().getUniqueId())));
            menu.addButton(next);
        }
        player.openInventory(menu.build());
    }

    private static void openPage(int page, Gamer gamer) {
        StandardMenu menu = main;
        for (int i = 0; i < 35; i++) {
            menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new ItemStack(Material.AIR)).setSlot(i));
        }

        boolean pass = true;
        int player_level = (int)gamer.getStatistics(EStat.BATTLEPASS_LEVEL);
        boolean hasMaxLevel = false;
        int maxLevel = page * 9 + 9;
        if (max_level < maxLevel && max_level >= page * 9) hasMaxLevel = true;

        if (page > 0) {
            for (int i = 9; i < 18; i++) {
                int level = page * 9 + (i - 9);
                if (hasMaxLevel && level > max_level) break;
                if (pass && player_level < level) pass = false;
                menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(glass(pass, level)).setSlot(i));
            }

            BattlePass.slots.forEach((reward, slot) -> {
                if (slot >= page * 54 && slot < page * 54 + 54) {
                    IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(reward.getIcon().getIcon()).setSlot(slot - (page * 54));
                    menu.addButton(btn);
                }
            });
        } else {
            main.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.COMPASS)
                    .name("&bБесплатные награды ->")
                    .lore(new Lore.BuilderLore()
                            .addSpace()
                            .addString("&7Эти награды вы получите,")
                            .addString("&7даже &eне покупая &7боевой пропуск!")
                            .build())
                    .build().item()).setSlot(0));
            main.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.COMPASS)
                    .name("&bПлатные награды ->")
                    .lore(new Lore.BuilderLore()
                            .addSpace()
                            .addString("&7Эти награды вы получите,")
                            .addString("&eтолько имея &7боевой пропуск!")
                            .build())
                    .build().item()).setSlot(18));
            main.addButton(DefaultButtons.FILLER.getButtonOfItemStack(ExampleItems.glass(11)).setSlot(9));
            main.addButton(DefaultButtons.FILLER.getButtonOfItemStack(ExampleItems.glass(11)).setSlot(27));

            for (int i = 10; i < 18; i++) {
                if (hasMaxLevel && i - 9 > max_level) break;
                if (pass && player_level < i - 9) pass = false;
                menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(glass(pass, i - 9)).setSlot(i));
            }

            BattlePass.slots.forEach((reward, slot) -> {
                if (slot > 0 && slot < 54) {
                    IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(reward.getIcon().getIcon()).setSlot(slot);
                    menu.addButton(btn);
                }
            });
        }

        if (Math.floor(pages) > page) {
            IMenuButton next = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.BARRIER).name("&aВперёд").build().item()).setSlot(53);
            next.setClickEvent(event -> openPage(page + 1, Main.gamers.get(event.getWhoClicked().getUniqueId())));
            menu.addButton(next);
        } else {
            menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new ItemStack(Material.AIR)).setSlot(53));
        }
        if (page != 0) {
            IMenuButton back = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.BARRIER).name("&aНазад").build().item()).setSlot(45);
            back.setClickEvent(event -> openPage(page - 1, Main.gamers.get(event.getWhoClicked().getUniqueId())));
            menu.addButton(back);
        } else {
            menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new ItemStack(Material.AIR)).setSlot(45));
        }

        gamer.getPlayer().openInventory(menu.build());
    }

    private static ItemStack glass(boolean opened, int level) {
        return ExampleItems.glass(opened ? 5 : 14, ChatColor.GRAY + "" + level + " Уровень");
    }

    public static void load() {
        max_level = EConfig.BATTLEPASS.getConfig().getConfigurationSection("levels").getKeys(false).size();
        pages = (Double.parseDouble(String.valueOf(BattlePass.glass_slots.size())) + 1) / 9;

        main = StandardMenu.create(6, ChatColor.YELLOW +  "Боевой пропуск");
        missionsMenu = StandardMenu.create(6, ChatColor.YELLOW +  "Боевой пропуск &7• &eИспытания");
        main.addChild(ChatColor.YELLOW +  "Боевой пропуск &7• &eИспытания", missionsMenu);

        for (int i = 36; i < 45; i++)
            main.addButton(DefaultButtons.FILLER.getButtonOfItemStack(ExampleItems.glass(11)).setSlot(i));

        IMenuButton wm = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.SIGN)
                .name("&eПосмотреть испытания")
                .lore(new Lore.BuilderLore()
                        .addSpace()
                        .addString("&7>> Открыть").build())
                .build().item()).setSlot(51);
        wm.setClickEvent(e -> openMissionsMenu(e.getWhoClicked(), missionsMenu));
        main.addButton(wm);

        if (pages > 1) {
            IMenuButton next = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.BARRIER).name("&aВперёд").build().item()).setSlot(53);
            next.setClickEvent(event -> openPage(1, Main.gamers.get(event.getWhoClicked().getUniqueId())));
            main.addButton(next);
        }
    }

    private static void openMissionsMenu(Player player, StandardMenu menu) {
        AtomicInteger i = new AtomicInteger(19);
        BattlePass.missions.forEach(wm -> {
            IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.PAPER)
                    .name("&e" + wm.getName())
                    .lore(new Lore.BuilderLore()
                            .addSpace()
                            .addString("&7>> Посмотреть задания")
                            .build())
                    .build().item()).setSlot(i.getAndIncrement());
            btn.setClickEvent(e -> openTasksMenu(Main.gamers.get(e.getWhoClicked().getUniqueId()), wm));
            menu.addButton(btn);
        });

        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new Item.Builder(Material.BARRIER).name("&cВернуться").build().item()).setSlot(53);
        back.setClickEvent(event -> new BattlePassMenu(event.getWhoClicked()));
        menu.addButton(back);

        player.openInventory(menu.build());
    }

    private String progresBarLevel(Gamer gamer) {
        StringBuilder sb = new StringBuilder();
        double pr = Double.parseDouble(gamer.getStatistics(EStat.BATTLEPASS_SCORE).toString());
        double progress = Math.ceil((pr / BattlePass.level) * 100);
        double pos = Math.ceil(progress / 10);
        for (int i = 0; i < 10; i++) {
            if (i < pos) {
                sb.append("&a&m--");
            } else {
                sb.append("&8&m--");
            }
        }
        sb.append("&8[&c").append(Math.round(progress)).append("%&8]");
        return Utils.colored(sb.toString());
    }

    private static void openTasksMenu(Gamer gamer, WeeklyMission missions) {
        StandardMenu menu = StandardMenu.create(4, ChatColor.YELLOW +  "Боевой пропуск &7• &eИспытания &7• &e" + missions.getName());
        AtomicInteger i = new AtomicInteger(10);
        missions.getMissions().forEach(mission -> {
            IMenuButton btn = DefaultButtons.FILLER.getButtonOfItemStack(mission.getIcon().getIcon(gamer)).setSlot(i.getAndIncrement());
            menu.addButton(btn);
            if (i.get() == 16) i.set(19);
        });

        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new Item.Builder(Material.BARRIER).name("&cВернуться").build().item()).setSlot(35);
        back.setClickEvent(event -> openMissionsMenu(event.getWhoClicked(), missionsMenu));
        menu.addButton(back);

        gamer.getPlayer().openInventory(menu.build());
    }

    @Override
    public int getRows() {
        return 6;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW +  "Боевой пропуск";
    }
}
