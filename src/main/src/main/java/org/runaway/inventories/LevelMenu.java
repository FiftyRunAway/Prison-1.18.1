package org.runaway.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.achievements.Achievement;
import org.runaway.enums.EButtons;
import org.runaway.enums.EMessage;
import org.runaway.items.Item;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.events.ButtonClickEvent;
import org.runaway.menu.type.StandardMenu;
import org.runaway.utils.ExampleItems;
import org.runaway.Requires;
import org.runaway.utils.ItemBuilder;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;
import org.runaway.board.Board;
import org.runaway.enums.EConfig;
import org.runaway.enums.EStat;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by _RunAway_ on 26.1.2019
 */

public class LevelMenu implements IMenus {

    public static List<Integer> passiveLevels;

    public LevelMenu(Player player) {
        StandardMenu menu = StandardMenu.create(getRows(), getName());
        Gamer gamer = GamerManager.getGamer(player);
        IMenuButton lvl = DefaultButtons.FILLER.getButtonOfItemStack(
                new Item.Builder(Material.EXPERIENCE_BOTTLE).name("&eПовысить уровень:").lore(new Lore.BuilderLore().addList(lore(gamer)).build()).build().item());
        menu.addButton(lvl.setSlot(24));
        ItemStack btn;
        if (hasAccessToNextLevel(gamer)) {
            btn = ExampleItems.glass(Material.LIME_STAINED_GLASS_PANE, "&a&lНажмите, чтобы повысить");
        } else {
            btn = ExampleItems.glass(Material.RED_STAINED_GLASS_PANE, "&cВыйти");
        }
        IMenuButton bt = DefaultButtons.FILLER.getButtonOfItemStack(btn);
        bt.setClickEvent(event -> {
            if (hasAccessToNextLevel(GamerManager.getGamer(event.getWhoClicked()))) {
                onClickLevel(event);
                return;
            }
            event.getWhoClicked().closeInventory();
        });
        for (int i = 10; i < 13; i++) {
            for (int b = 0; b < 3; b++) {
                menu.addButton(bt.clone().setSlot(i + (b * 9)));
            }
        }
        bt = DefaultButtons.FILLER.getButtonOfItemStack(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name("").build());
        for (int i = 14; i < 17; i++) {
            for (int b = 0; b < 3; b++) {
                menu.addButton(bt.clone().setSlot(i + (b * 9)));
            }
        }

        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new ItemBuilder(EButtons.CANCEL.getItemStack()).build()).setSlot(44);
        back.setClickEvent(event -> event.getWhoClicked().closeInventory());
        menu.addButton(back);

        menu.open(GamerManager.getGamer(player));
    }

    private static void onClickLevel(ButtonClickEvent event) {
        Player p = event.getWhoClicked();
        Gamer g = GamerManager.getGamer(p);
        double price = new Requires(g).costNextLevel();
        if (g.getMoney() >= price) {
            double blocks = new Requires(g).blocksNextLevel();
            if (g.getDoubleStatistics(EStat.BLOCKS) >= blocks) {
                g.sendTitle(ChatColor.AQUA + "Поздравляем", ChatColor.YELLOW + "с повышением уровня!");
                g.withdrawMoney(price, true);
                g.increaseIntStatistics(EStat.LEVEL);
                p.closeInventory();
                g.setLevelBar();
                g.setExpProgress();
                g.setHearts();
                int newLevel = g.getIntStatistics(EStat.LEVEL);
                if (newLevel == 5) {
                    Achievement.FIVE_LEVEL.get(p);
                } else if (newLevel == 10) {
                    Achievement.TEN_LEVEL.get(p);
                } else if (newLevel == 15) {
                    Achievement.FIFTEEN_LEVEL.get(p);
                } else if (newLevel == 20) {
                    Achievement.TWENTY_LEVEL.get(p);
                } else if (newLevel == 25) {
                    Achievement.TWENTYFIFTH_LEVEL.get(p);
                }
                if (passiveLevels.contains(newLevel)) {
                    g.sendMessage("&4Вам доступны на выбор новые пассивные навыки!");
                }
            } else {
                g.sendMessage(EMessage.LEVELNEEDBLOCKS);
                p.closeInventory();
            }
        } else {
            g.sendMessage(EMessage.MONEYNEEDS);
            p.closeInventory();
        }
    }

    private static ArrayList<String> lore(Gamer gamer) {
        Requires requires = new Requires(gamer);
        double price = requires.costNextLevel();
        double blocks = requires.blocksNextLevel();

        ArrayList<String> lore = new ArrayList<>();
        lore.add(Utils.colored("       &7[&c" + gamer.getStatistics(EStat.LEVEL) + " &7-> &a" + (Integer.parseInt(gamer.getStatistics(EStat.LEVEL).toString()) + 1) + "&7]"));
        lore.add(Utils.colored("&eТребования:"));
        lore.add(Utils.colored("&7• &fБлоков: " + AccessBlocksColor(gamer) + Math.round(gamer.getDoubleStatistics(EStat.BLOCKS)) + "/" + blocks + " " + AccessBlocksSymbol(gamer)));
        lore.add(Utils.colored("&7• &fДенег: " + AccessMoneyColor(gamer) + Board.FormatMoney(gamer.getMoney()) + "/" + Board.FormatMoney(price) + " " + AccessMoneySymbol(gamer)));
//        lore.add(" ");
//        lore.add("&eВы получите:");
//        int next = gamer.getIntStatistics(EStat.LEVEL);
//        if ()
        return lore;
    }

    private static ChatColor AccessBlocksColor(Gamer gamer) {
        int nextlevel = (gamer.getIntStatistics(EStat.LEVEL) + 1);
        if (gamer.getDoubleStatistics(EStat.BLOCKS) - EConfig.CONFIG.getConfig().getDouble("levels." + nextlevel + ".blocks") >= 0) {
            return ChatColor.GREEN;
        } else {
            return ChatColor.RED;
        }
    }

    private static ChatColor AccessMoneyColor(Gamer gamer) {
        int nextlevel = (gamer.getIntStatistics(EStat.LEVEL) + 1);
        if (gamer.getMoney() - EConfig.CONFIG.getConfig().getDouble("levels." + nextlevel + ".price") >= 0) {
            return ChatColor.GREEN;
        } else {
            return ChatColor.RED;
        }
    }

    private static boolean hasAccessToNextLevel(Gamer gamer) {
        int nextlevel = (gamer.getIntStatistics(EStat.LEVEL) + 1);
        if (gamer.getMoney() >= EConfig.CONFIG.getConfig().getDouble("levels." + nextlevel + ".price")) {
            return gamer.getDoubleStatistics(EStat.BLOCKS) >= EConfig.CONFIG.getConfig().getDouble("levels." + nextlevel + ".blocks");
        }
        return false;
    }

    private static String AccessBlocksSymbol(Gamer gamer) {
        int nextlevel = (gamer.getIntStatistics(EStat.LEVEL) + 1);
        if (gamer.getDoubleStatistics(EStat.BLOCKS) >= EConfig.CONFIG.getConfig().getDouble("levels." + nextlevel + ".blocks")) {
            return ChatColor.GREEN + "✔";
        } else {
            return ChatColor.RED + "✘";
        }
    }

    private static String AccessMoneySymbol(Gamer gamer) {
        int nextlevel = (gamer.getIntStatistics(EStat.LEVEL) + 1);
        if (gamer.getMoney() >= EConfig.CONFIG.getConfig().getDouble("levels." + nextlevel + ".price")) {
            return ChatColor.GREEN + "✔";
        } else {
            return ChatColor.RED + "✘";
        }
    }

    @Override
    public int getRows() {
        return 5;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Повышение уровня";
    }
}
