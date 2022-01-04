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
import org.runaway.inventories.Confirmation;
import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;
import org.runaway.items.parameters.ParameterMeta;
import org.runaway.managers.GamerManager;
import org.runaway.requirements.Require;
import org.runaway.trainer.Trainer;
import org.runaway.trainer.TypeTrainings;
import org.runaway.utils.Utils;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/*
 * Created by _RunAway_ on 4.5.2019
 */

public class Upgrade {

    public static void upgrade(Player player, boolean fastGet) {
        try {
            if (player.isOnline()) {
                Gamer gamer = GamerManager.getGamer(player);
                String nextPrisonItem = ItemManager.getPrisonItem(player.getInventory().getItemInMainHand()).getNextPrisonItem();

                for (Require require : ItemManager.getPrisonItem(ItemManager.getPrisonItem(player.getInventory().getItemInMainHand()).getNextPrisonItem()).getUpgradeRequireList().getRequireList()) {
                    if (!require.canAccess(gamer, true).isAccess()) {
                        player.closeInventory();
                        return;
                    }
                }
                for (Require require : ItemManager.getPrisonItem(ItemManager.getPrisonItem(player.getInventory().getItemInMainHand()).getNextPrisonItem()).getUpgradeRequireList().getRequireList()) {
                    require.doAfter(gamer);
                }
                ItemStack nextItem = ItemManager.getPrisonItem(nextPrisonItem).getItemStack();
                nextItem = new ParameterMeta(player.getInventory().getItemInMainHand()).applyTo(nextItem);
                /*if (!fastGet && gamer.getLevelItem(save) > gamer.getIntStatistics(EStat.LEVEL)) {
                    new Confirmation(player, null, null, () ->
                            Upgrade.upgrade(player, true));
                    return;
                }
                for (UpgradeProperty prop : itemdata.keySet()) {
                    if (prop.isTaken()) {
                        Upgrade.take(prop, Integer.parseInt(itemdata.get(prop)), player);
                    }
                }*/
                player.getInventory().setItemInMainHand(nextItem);
                gamer.sendMessage(EMessage.SUCCESSFULUPGRADE);
                Bukkit.getServer().getPluginManager().callEvent(new UpgradeEvent(player));
                player.closeInventory();
                Achievement.FIRST_UPGRADE.get(player);
                if (gamer.getTrainingLevel(TypeTrainings.UPGRADE.name()) > 0) {
                    /*Utils.trainer.forEach(trainer -> {
                        if (trainer.getType() != TypeTrainings.UPGRADE) return;
                        if (UpgradeMisc.getSection(player) == null) return;
                        if (Math.random() < trainer.getValue(player)) {
                            if (nextItem.getItemMeta().equals(player.getInventory().getItemInMainHand().getItemMeta())) {
                                String next2 = UpgradeMisc.getNext(UpgradeMisc.getSection(player));
                                player.getInventory().setItemInMainHand(UpgradeMisc.buildItem(next2, false, player, false));
                                gamer.sendMessage(EMessage.TRAINERUPGRADE);
                            } else {
                                gamer.sendMessage(EMessage.TRAINERUPGRADEDUPE);
                            }
                        }
                    });*/
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
            case LEVEL: {
                return String.valueOf(gamer.getStatistics(EStat.LEVEL));
            }
            case RATS: {
                return String.valueOf(gamer.getMobKills("rat"));
            }
            case KILLS: {
                return String.valueOf(gamer.getStatistics(EStat.KILLS));
            }
            case WOOD: {
                return String.valueOf(gamer.getCurrentBlocks("LOG_2"));
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
            case WEB: {
                return String.valueOf(gamer.getCurrentBlocks("WEB"));
            }
            case WOOL: {
                return String.valueOf(gamer.getCurrentBlocks("WOOL"));
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

    private static void take(UpgradeProperty prop, int p, Player player) {
        Gamer gamer = GamerManager.getGamer(player);
        switch (prop) {
            case COST: {
                gamer.withdrawMoney(p);
                break;
            }
            case BLOCKS: {
                gamer.setStatistics(EStat.BLOCKS, gamer.getDoubleStatistics(EStat.BLOCKS) - p);
                break;
            }
        }
    }

    /*
    Зачарования:
    -
    -
    Уровень >> 5

     */

}
