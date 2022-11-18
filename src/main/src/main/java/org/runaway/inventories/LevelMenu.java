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
import org.runaway.levels.GamerLevel;
import org.runaway.managers.GamerManager;
import org.runaway.menu.SimpleItemStack;
import org.runaway.menu.UpdateMenu;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.events.ButtonClickEvent;
import org.runaway.menu.type.StandardMenu;
import org.runaway.mines.Mines;
import org.runaway.requirements.Require;
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
                new Item.Builder(Material.EXPERIENCE_BOTTLE).name("&bНовый уровень").lore(new Lore.BuilderLore()
                        .addList(lore(gamer)).build()).build().item());
        menu.addButton(lvl.setSlot(24));
        ItemStack btn;
        if (new GamerLevel(gamer).getNextRequirements().canPass(gamer, false)) {
            btn = ExampleItems.glass(Material.LIME_STAINED_GLASS_PANE, "&a&lНажмите, чтобы повысить");
        } else {
            btn = ExampleItems.glass(Material.RED_STAINED_GLASS_PANE, "&cЕщё рановато");
        }
        IMenuButton bt = DefaultButtons.FILLER.getButtonOfItemStack(btn);
        bt.setClickEvent(event -> {
            if (new GamerLevel(GamerManager.getGamer(event.getWhoClicked())).getNextRequirements().canPass(gamer, false)) {
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

        IMenuButton back = DefaultButtons.RETURN.getButtonOfItemStack(new ItemBuilder(EButtons.CANCEL.getItemStack()).build()).setSlot(53);
        back.setClickEvent(event -> event.getWhoClicked().closeInventory());
        menu.addButton(back);

        int nextLevel = new GamerLevel(gamer).getNextLevel();
        List<IMenuButton> bonuses = new ArrayList<>();
        bonuses.add(DefaultButtons.FILLER.getButtonOfItemStack(new ItemBuilder(Material.TNT)
                .name("&cДополнительное здоровье").addLoreLine("&r").addLoreLine("&fУ вас прибавится полсердца здоровья")
                .build()));
        if (passiveLevels.contains(nextLevel)) {
            bonuses.add(DefaultButtons.FILLER.getButtonOfItemStack(new ItemBuilder(Material.REDSTONE)
                            .name("&cПассивное умение").addLoreLine("&r").addLoreLine("&fБудет доступен новый пассивный навык")
                            .addLoreLine("&fна выбор в главном меню!")
                    .build()));
        }
        for (Mines mine : Mines.getMines()) {
            if (mine.getMinLevel() == nextLevel) {
                bonuses.add(DefaultButtons.FILLER.getButtonOfItemStack(new ItemBuilder(mine.getPrisonIcon(gamer, false).getType())
                        .name("&7Новая " + (mine.hasBoss() ? "локация" : "шахта") + " • &e" + mine.getName())
                        .addLoreLine("&r").addLoreLine(mine.hasBoss() ? "&fВы сможете телепортироваться на локацию (&6с пропуском&f)" : "&fВы получите доступ к новой шахте")
                        .build()));
                if (mine.hasBoss()) {
                    bonuses.add(DefaultButtons.FILLER.getButtonOfItemStack(new ItemBuilder(mine.getBoss().getAttributable().getMobType().getIcon())
                            .name("&7Новый босс • &e" + mine.getBoss().getAttributable().getName())
                            .addLoreLine("&r").addLoreLine("&fУ вас появится доступ к новому боссу")
                            .build()));
                }
            }
        }
        if (nextLevel == 5) {
            IMenuButton fraction = DefaultButtons.FILLER.getButtonOfItemStack(new ItemBuilder(Material.IRON_SWORD)
                    .name("&bВыбор фракции").addLoreLine("&r").addLoreLine("&fВы сможете выбрать себе фракцию")
                    .build()).setSlot(46 + bonuses.size());

            UpdateMenu.builder().updateType(new SimpleItemStack[]{SimpleItemStack.builder()
                            .material(Material.STONE_SWORD)
                            .durability(0).build(),
                    SimpleItemStack.builder()
                            .material(Material.GOLDEN_SWORD)
                            .durability(0).build()})
                    .build().update(menu, fraction);

            bonuses.add(fraction);
        }
        int i = 0;
        for (IMenuButton btn2 : bonuses) {
            menu.addButton(btn2.setSlot(46 + i));
            i++;
        }

        menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new ItemBuilder(EButtons.ARROW_RIGHT.getItemStack())
                        .name("&eБонусы после прокачки уровня")
                .build()).setSlot(45));

        menu.open(GamerManager.getGamer(player));
    }

    private static void onClickLevel(ButtonClickEvent event) {
        Player p = event.getWhoClicked();
        if (p.isOnline()) {
            Gamer g = GamerManager.getGamer(p);
            GamerLevel gamerLevel = new GamerLevel(g);
            for (Require require : gamerLevel.getNextRequirements().getRequireList()) {
                if (!require.canAccess(g, true).isAccess()) {
                    p.closeInventory();
                    return;
                }
            }
            for (Require require : gamerLevel.getNextRequirements().getRequireList()) {
                require.doAfter(g);
            }
            g.sendTitle(ChatColor.AQUA + "" + gamerLevel.getNextLevel() + " уровень");
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
                Utils.sendClickableMessage(g, "&bВам доступны на выбор новые пассивные навыки!", "profile", "&fОткройте меню пассивных навыков в главном меню");
            }
        }
    }

    private static ArrayList<String> lore(Gamer gamer) {
        GamerLevel gamerLevel = new GamerLevel(gamer);

        ArrayList<String> lore = new ArrayList<>();
        lore.add(Utils.colored("       &7[&c" + gamerLevel.getCurrentLevel() + " &7-> &a" + gamerLevel.getNextLevel() + "&7]"));
        lore.add(Utils.colored("&eТребования:"));
        lore.addAll(gamerLevel.getNextRequirements().getLore(gamer));
//        lore.add(" ");
//        lore.add("&eВы получите:");
//        int next = gamer.getIntStatistics(EStat.LEVEL);
//        if ()
        return lore;
    }

    private static boolean hasAccessToNextLevel(Gamer gamer) {
        int nextlevel = (gamer.getIntStatistics(EStat.LEVEL) + 1);
        if (gamer.getMoney() >= EConfig.CONFIG.getConfig().getDouble("levels." + nextlevel + ".price")) {
            return gamer.getDoubleStatistics(EStat.BLOCKS) >= EConfig.CONFIG.getConfig().getDouble("levels." + nextlevel + ".blocks");
        }
        return false;
    }

    @Override
    public int getRows() {
        return 6;
    }

    @Override
    public String getName() {
        return ChatColor.YELLOW + "Повышение уровня";
    }
}
