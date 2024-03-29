package org.runaway.events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.board.Board;
import org.runaway.cases.CaseManager;
import org.runaway.cases.CaseRefactored;
import org.runaway.enums.*;
import org.runaway.inventories.BlockShopMenu;
import org.runaway.inventories.MainMenu;
import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;
import org.runaway.jobs.EJobs;
import org.runaway.jobs.job.Mover;
import org.runaway.managers.GamerManager;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

/*
 * Created by _RunAway_ on 20.1.2019
 */

public class PlayerInteract implements Listener {

    private static final HashMap<String, Double> prices = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = GamerManager.getGamer(player);

        ItemStack main = player.getInventory().getItemInMainHand();
        PrisonItem prisonItem = ItemManager.getPrisonItem(main);
        if(prisonItem != null && prisonItem.getConsumerOnClick() != null &&
                (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            prisonItem.getConsumerOnClick().accept(gamer);
        }
        if (prisonItem != null && !main.getType().toString().endsWith("SWORD") &&
                gamer.getLevelItem() > gamer.getLevel()) {
            gamer.sendMessage(EMessage.MINLEVELITEM.getMessage().replace("%level%", gamer.getLevelItem(event.getItem()) + ""));
            event.setCancelled(true);
        }
        Block block = event.getClickedBlock();
        if (block != null) {
            if(block.getType() == Material.CHEST &&
                    Utils.getLocation("case").getBlock().equals(block)) {
                if(prisonItem == null) return;
                if(prisonItem.getCategory() != PrisonItem.Category.KEYS) return;
                CaseRefactored caseRefactored = CaseManager.getCase(prisonItem.getTechName());
                if(caseRefactored == null) return;
                event.setCancelled(true);
                if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    caseRefactored.open(gamer);
                    if(main.getAmount() == 1) {
                        player.getInventory().setItemInMainHand(null);
                    } else {
                        main.setAmount(main.getAmount() - 1);
                    }
                } else if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    caseRefactored.getChancesMenu(gamer).open(gamer);
                }
            }
            if (block.getType().equals(Mover.boxMaterial) &&
                    event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                if(!gamer.isEndedCooldown("boxCd")) {
                    return;
                }
                gamer.addCooldown("boxCd", 300);
                //Admin edit mode
                if (player.isOp() && main.getType().equals(Material.STICK)) {
                    if (Mover.getBoxes().contains(block.getLocation())) {
                        Mover.getBoxes().remove(block.getLocation());
                        gamer.debug("&cУбран ящик для работы с локации " + block.getLocation().getWorld().getName() + " " + block.getX() + " " + block.getY() + " " + block.getZ());
                    } else {
                        Mover.getBoxes().add(block.getLocation());
                        gamer.debug("&aДобавлен ящик для работы в локации " + block.getLocation().getWorld().getName() + " " + block.getX() + " " + block.getY() + " " + block.getZ());
                    }
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1f, 1f);
                    return;
                } else if (player.isOp() && main.getType().equals(Material.BLAZE_ROD)) {
                    if (Mover.getDestinations().contains(block.getLocation())) {
                        Mover.getDestinations().remove(block.getLocation());
                        gamer.debug("&cУбрана точка доставки с локации " + block.getLocation().getWorld().getName() + " " + block.getX() + " " + block.getY() + " " + block.getZ());
                    } else {
                        Mover.getDestinations().add(block.getLocation());
                        gamer.debug("&aДобавлена точка доставки в локации " + block.getLocation().getWorld().getName() + " " + block.getX() + " " + block.getY() + " " + block.getZ());
                    }
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1f, 1f);
                    return;
                }
                Mover.takeBoxListener(event);
                event.setCancelled(true);
                return;
            }
            if (main != null && main.hasItemMeta()) {
                if (Prison.getInstance().fish_food.contains(ChatColor.stripColor(main.getItemMeta().getDisplayName()))) {
                    gamer.sendMessage(EMessage.SELLTIT);
                    event.setCancelled(true);
                    return;
                }

                if (main.getType().equals(Material.FISHING_ROD) && !event.isCancelled()) {
                    if (BlockBreak.isLocation(player.getLocation(), "fisherman")) {
                        if (gamer.getIntStatistics(EStat.LEVEL) < EJobs.FISHERMAN.getJob().getLevel()) {
                            player.sendMessage(Vars.getPrefix() + Utils.colored(EMessage.JOBLEVEL.getMessage().replace("%level%", EJobs.FISHERMAN.getJob().getLevel() + "")));
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }

            if (block.getType().equals(Material.OAK_WALL_SIGN) || block.getType().equals(Material.OAK_SIGN)) {
                Sign sign = (Sign)block.getState();
                String[] lines = sign.getLines();
                if (lines.length == 4 && ChatColor.stripColor(lines[1]).equalsIgnoreCase("Нажми, чтобы") && ChatColor.stripColor(lines[2]).equalsIgnoreCase("всё продать")) {
                    if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                        sellAll(player);
                    } else if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                        new BlockShopMenu(player);
                    }
                }
            }
            //Сундук (сокровища)
            if (block.getType().equals(Material.CHEST) && event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                try {
                    if (!BlockBreak.chests.containsKey(player.getName())) return;
                    if (BlockBreak.chests.get(player.getName()).equals(block)) {
                        block.setType(Material.AIR);
                        int money = (ThreadLocalRandom.current().nextInt(6) + 5) * gamer.getIntStatistics(EStat.LEVEL);
                        player.sendMessage(Utils.colored(EMessage.TREASUREOPEN.getMessage()).replace("%reward%", Board.FormatMoney(money)));
                        if (BlockBreak.treasure_holo.containsKey(player.getName())) {
                            BlockBreak.treasure_holo.get(player.getName()).delete();
                            BlockBreak.treasure_holo.remove(player.getName());
                        }
                        gamer.depositMoney(money, true);
                        BlockBreak.chests.remove(player.getName());
                        BlockBreak.chests_tasks.remove(player.getName());
                    }
                } catch (Exception ignored) { }
            }
        }
    }

    //Продажа предметов
    public static void sellAll(Player player) {
        double tod = 0.0; int amount = 0;
        for (int h = 0; h <= 35; ++h) {
            ItemStack itemStack = player.getInventory().getItem(h);
            if (itemStack != null && itemStack.getAmount() != 0) {
                if (prices.containsKey(itemStack.getType() + "|" + itemStack.getDurability())) {
                    tod += prices.get(itemStack.getType() + "|" + itemStack.getDurability()) * itemStack.getAmount();
                    amount += itemStack.getAmount();
                    player.getInventory().setItem(h, null);
                }
            }
        }
        Gamer gamer = GamerManager.getGamer(player);
        if (amount == 0) {
            gamer.sendMessage(EMessage.NOBLOCKSFORSALE);
            return;
        }
        tod *= gamer.getBoosterMoney();
        String ret = String.valueOf(BigDecimal.valueOf(tod).setScale(2, RoundingMode.UP).doubleValue());
        String format = Utils.colored(EMessage.ACTIONBARSELL.getMessage()).replace("%amount%", String.valueOf(amount)).replace("%money%", ret + " " + MoneyType.RUBLES.getShortName()).replace("%booster%", String.valueOf(gamer.getBoosterMoney()));
        gamer.depositMoney(tod);
        Utils.sendClickableMessage(gamer, format, "boosters");
    }

    //Подгрузка цен
    public static void loadShop() {
        try {
            EConfig.SHOP.getConfig().getStringList("shop").forEach(s -> {
                String[] var = s.split(" ");
                Material mat = Material.valueOf(var[0]);
                double price = Double.parseDouble(var[1]);
                int data = Integer.parseInt(var[2]);
                prices.put(mat.name() + "|" + data, price);
            });
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with loading shop prices!");
            Bukkit.getPluginManager().disablePlugin(Prison.getInstance());
            //Prison.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }
}
