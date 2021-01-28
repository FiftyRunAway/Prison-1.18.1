package org.runaway.inventories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.achievements.Achievement;
import org.runaway.enums.EMessage;
import org.runaway.items.Item;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.utils.ExampleItems;
import org.runaway.Requires;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;
import org.runaway.board.Board;
import org.runaway.enums.EConfig;
import org.runaway.enums.EStat;

import java.util.ArrayList;

/*
 * Created by _RunAway_ on 26.1.2019
 */

public class LevelMenu implements IMenus {

    public LevelMenu(Player player) {
        StandardMenu menu = StandardMenu.create(getRows(), getName());
        menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(ExampleItems.glass(7)).setSlot(0));
        menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(ExampleItems.glass(7)).setSlot(1));
        menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(ExampleItems.glass(7)).setSlot(7));
        menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(ExampleItems.glass(7)).setSlot(8));
        Gamer gamer = GamerManager.getGamer(player);
        IMenuButton lvl = DefaultButtons.FILLER.getButtonOfItemStack(
                new Item.Builder(Material.NETHER_STAR).name("&eПовысить уровень:").lore(new Lore.BuilderLore().addList(lore(gamer)).build()).build().item()).setSlot(4);
        lvl.setClickEvent(event -> {
            Player p = event.getWhoClicked();
            Gamer g = GamerManager.getGamer(p);
            double price = new Requires(g).costNextLevel();
            if (g.getMoney() >= price) {
                double blocks = new Requires(g).blocksNextLevel();
                if (g.getDoubleStatistics(EStat.BLOCKS) >= blocks) {
                    g.sendTitle(ChatColor.YELLOW + "Поздравляем", ChatColor.YELLOW + "с повышением уровня!");
                    g.withdrawMoney(price);
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
                } else {
                    g.sendMessage(EMessage.LEVELNEEDBLOCKS);
                    p.closeInventory();
                }
            } else {
                g.sendMessage(EMessage.MONEYNEEDS);
                p.closeInventory();
            }
        });
        menu.addButton(lvl);
        IMenuButton btn;
        if (hasAccessToNextLevel(gamer)) {
            btn = DefaultButtons.FILLER.getButtonOfItemStack(
                    ExampleItems.glass(5, ChatColor.GREEN + "" + ChatColor.BOLD + "МОЖНО"));
        } else {
            btn = DefaultButtons.FILLER.getButtonOfItemStack(
                    ExampleItems.glass(14, ChatColor.RED + "" + ChatColor.BOLD + "НЕЛЬЗЯ"));
        }
        menu.addButton(btn.setSlot(2));
        menu.addButton(btn.setSlot(3));
        menu.addButton(btn.setSlot(5));
        menu.addButton(btn.setSlot(6));

        player.openInventory(menu.build());
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
        return 0;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Повышение уровня";
    }
}
