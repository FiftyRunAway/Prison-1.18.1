package org.runaway.upgrades;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.achievements.Achievement;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.enums.UpgradeProperty;
import org.runaway.events.custom.UpgradeEvent;
import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;
import org.runaway.items.parameters.ParameterMeta;
import org.runaway.managers.GamerManager;
import org.runaway.requirements.Require;
import org.runaway.requirements.StarsRequire;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;
import org.runaway.tasks.SyncTask;
import org.runaway.trainer.TypeTrainings;
import org.runaway.utils.Utils;

import java.util.EnumMap;

/*
 * Created by _RunAway_ on 4.5.2019
 */

public class Upgrade {

    public static void upgrade(Player player, boolean fastGet) {
        try {
            if (player.isOnline()) {
                Gamer gamer = GamerManager.getGamer(player);
                String nextPrisonItem = ItemManager.getPrisonItem(player.getInventory().getItemInMainHand()).getNextPrisonItem();
                PrisonItem item = ItemManager.getPrisonItem(ItemManager.getPrisonItem(player.getInventory().getItemInMainHand()).getNextPrisonItem());

                for (Require require : item.getUpgradeRequireList().getRequireList()) {
                    if (!require.canAccess(gamer, true).isAccess()) {
                        player.closeInventory();
                        return;
                    }
                }
                if (Boolean.FALSE.equals(gamer.getUpgradeConfirmation().getOrDefault(item.getTechName(), false)) &&
                        gamer.getLevelItem(item.getItemStack()) > gamer.getLevel()) {
                    gamer.getUpgradeConfirmation().put(item.getTechName(), true);
                    player.closeInventory();
                    gamer.sendMessage("&cПредмет, который вы хотите улучшить станет вам недоступен из-за &4минимального уровня&r&c!" +
                            "\n&cЕсли вы согласны на это - просто снова зайдите в &4/upgrade &r&cв течение 15 секунд!");
                    gamer.sendTitle("&cПРЕДУПРЕЖДЕНИЕ");

                    new SyncTask(() -> {
                        if (!gamer.getUpgradeConfirmation().containsKey(item.getTechName())) return;
                        gamer.getUpgradeConfirmation().remove(item.getTechName());
                    }, 15 * 20);
                    return;
                }
                gamer.getUpgradeConfirmation().remove(item.getTechName());
                for (Require require : item.getUpgradeRequireList().getRequireList()) {
                    require.doAfter(gamer);
                }
                ItemStack nextItem = ItemManager.getPrisonItem(nextPrisonItem).getItemStack();
                nextItem = new ParameterMeta(player.getInventory().getItemInMainHand()).applyTo(nextItem);
                for (Rune rune : RuneManager.getRunes(player.getInventory().getItemInMainHand())) {
                    nextItem = RuneManager.addRune(nextItem, rune);
                }
                player.getInventory().setItemInMainHand(nextItem);
                gamer.sendMessage(EMessage.SUCCESSFULUPGRADE);
                Bukkit.getServer().getPluginManager().callEvent(new UpgradeEvent(player));
                player.closeInventory();
                Achievement.FIRST_UPGRADE.get(player);
                if (gamer.getTrainingLevel(TypeTrainings.UPGRADE.name()) > 0) {
                    //TODO Trainer 'upgrades'
                }
            }
        } catch (Exception ex) {
            player.sendMessage(Utils.colored("&cОшибка при прокачке! Сообщите администрации об этом."));
            ex.printStackTrace();
        }
    }

    static EnumMap<UpgradeProperty, String> getData(Gamer gamer) {
        EnumMap<UpgradeProperty, String> map = new EnumMap<>(UpgradeProperty.class);
        for (UpgradeProperty up : UpgradeProperty.values()) {
            map.put(up, getProp(up, gamer));
        }
        return map;
    }

    public static String getProp(UpgradeProperty prop, Gamer gamer) {
        switch (prop) {
            case COST: {
                return String.valueOf(Math.round(gamer.getMoney()));
            }
            case STARS: {
                return String.valueOf(StarsRequire.getAmount(gamer.getPlayer(), ItemManager.getPrisonItem("star").getItemStack()));
            }
            case LEVEL: {
                return String.valueOf(gamer.getIntStatistics(EStat.LEVEL));
            }
            case RATS: {
                return String.valueOf(gamer.getMobKills("rat"));
            }
            case KILLS: {
                return String.valueOf(gamer.getStatistics(EStat.KILLS));
            }
            case WOOD: {
                return String.valueOf(gamer.getCurrentBlocks("DARK_OAK_LOG"));
            }
            case BLOCKS: {
                return String.valueOf(Math.round(gamer.getDoubleStatistics(EStat.BLOCKS)));
            }
            case MINE_BLOCKS: {
                return String.valueOf(gamer.getCurrentBlocks("STONE") + gamer.getCurrentBlocks("COBBLESTONE"));
            }
            case SHOVEL_BLOCKS: {
                return String.valueOf(gamer.getCurrentBlocks("DIRT") + gamer.getCurrentBlocks("GRAVEL") + gamer.getCurrentBlocks("SAND"));
            }
            case STONE: {
                return String.valueOf(gamer.getCurrentBlocks("STONE"));
            }
            case NETHERRACK: {
                return String.valueOf(gamer.getCurrentBlocks("NETHERRACK"));
            }
            case IRON_ORE: {
                return String.valueOf(gamer.getCurrentBlocks("IRON_ORE"));
            }
            case COAL_ORE: {
                return String.valueOf(gamer.getCurrentBlocks("COAL_ORE"));
            }
            case GOLD_ORE: {
                return String.valueOf(gamer.getCurrentBlocks("GOLD_ORE"));
            }
            case COBWEB: {
                return String.valueOf(gamer.getCurrentBlocks("COBWEB"));
            }
            case WHITE_WOOL: {
                return String.valueOf(gamer.getCurrentBlocks("WHITE_WOOL"));
            }
            case BOW_KILL: {
                return String.valueOf(gamer.getStatistics(EStat.BOW_KILL));
            }
            case DIRT: {
                return String.valueOf(gamer.getCurrentBlocks("DIRT"));
            }
            case SAND: {
                return String.valueOf(gamer.getCurrentBlocks("SAND"));
            }
            case GRAVEL: {
                return String.valueOf(gamer.getCurrentBlocks("GRAVEL"));
            }
            default: {
                return "null";
            }
        }
    }
}
