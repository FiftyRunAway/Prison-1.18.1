package org.runaway.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.Requires;
import org.runaway.achievements.Achievement;
import org.runaway.boosters.LBlocks;
import org.runaway.boosters.LMoney;
import org.runaway.boosters.Serializer;
import org.runaway.enums.*;
import org.runaway.managers.GamerManager;
import org.runaway.upgrades.Upgrade;
import org.runaway.upgrades.UpgradeMisc;
import org.runaway.utils.Utils;

import java.util.ArrayList;

/*
 * Created by _RunAway_ on 23.1.2019
 */

public class PlayerInventoryClick implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        try {
            if (event.getInventory().getName().equals(Utils.colored("&eАктивация ускорителей &7• &eУскорители блоков"))) {
                event.setCancelled(true);
                if (event.getCurrentItem() != null) {
                    Gamer gamer = GamerManager.getGamer(player);
                    if (event.getCurrentItem().getType().isBlock()) {
                        if (!Main.gBlocks.isActive()) {
                            String[] var = new Serializer().unserial(event.getCurrentItem(), gamer, BoosterType.BLOCKS).split("-");
                            Main.gBlocks.start(player.getName(), Long.parseLong(var[1]), Double.parseDouble(var[0]));
                            player.closeInventory();
                        } else {
                            gamer.sendMessage(EMessage.BOOSTERALREADYACTIVE);
                        }
                    } else {
                        if (!gamer.isActiveLocalBlocks()) {
                            LBlocks blocks = new LBlocks();
                            String[] var = new Serializer().unserial(event.getCurrentItem(), gamer, BoosterType.BLOCKS).split("-");
                            blocks.start(player.getName(), Long.parseLong(var[1]), Double.parseDouble(var[0]));
                            player.closeInventory();
                        } else {
                            gamer.sendMessage(EMessage.BOOSTERALREADYACTIVE);
                        }
                    }
                }
            }
            if (event.getInventory().getName().equals(Utils.colored("&eАктивация ускорителей &7• &eУскорители денег"))) {
                event.setCancelled(true);
                if (event.getCurrentItem() != null) {
                    Gamer gamer = GamerManager.getGamer(player);
                    if (event.getCurrentItem().getType().isBlock()) {
                        if (!Main.gMoney.isActive()) {
                            String[] var = new Serializer().unserial(event.getCurrentItem(), gamer, BoosterType.MONEY).split("-");
                            Main.gMoney.start(player.getName(), Long.parseLong(var[1]), Double.parseDouble(var[0]));
                            player.closeInventory();
                        } else {
                            gamer.sendMessage(EMessage.BOOSTERALREADYACTIVE);
                        }
                    } else {
                        if (!gamer.isActiveLocalMoney()) {
                            LMoney money = new LMoney();
                            String[] var = new Serializer().unserial(event.getCurrentItem(), gamer, BoosterType.MONEY).split("-");
                            money.start(player.getName(), Long.parseLong(var[1]), Double.parseDouble(var[0]));
                            player.closeInventory();
                        } else {
                            gamer.sendMessage(EMessage.BOOSTERALREADYACTIVE);
                        }
                    }
                }
            }
            if (event.getInventory().getName().equals(ChatColor.YELLOW + "Повышение уровня")) {
                event.setCancelled(true);
                if (event.getCurrentItem().getType().equals(Material.NETHER_STAR)) {
                    Gamer gamer = GamerManager.getGamer(player);
                    double price = new Requires(gamer).costNextLevel();
                    if (gamer.getMoney() >= price) {
                        double blocks = new Requires(gamer).blocksNextLevel();
                        if (gamer.getDoubleStatistics(EStat.BLOCKS) >= blocks) {
                            gamer.sendTitle(ChatColor.YELLOW + "Поздравляем", ChatColor.YELLOW + "с повышением уровня!");
                            gamer.withdrawMoney(price);
                            gamer.increaseIntStatistics(EStat.LEVEL);
                            player.closeInventory();
                            gamer.setLevelBar();
                            gamer.setExpProgress();
                            gamer.setHearts();
                            int newLevel = gamer.getIntStatistics(EStat.LEVEL);
                            if (newLevel == 5) {
                                Achievement.FIVE_LEVEL.get(player);
                            } else if (newLevel == 10) {
                                Achievement.TEN_LEVEL.get(player);
                            } else if (newLevel == 15) {
                                Achievement.FIFTEEN_LEVEL.get(player);
                            } else if (newLevel == 20) {
                                Achievement.TWENTY_LEVEL.get(player);
                            }
                        } else {
                            gamer.sendMessage(EMessage.LEVELNEEDBLOCKS);
                            player.closeInventory();
                        }
                    } else {
                        gamer.sendMessage(EMessage.MONEYNEEDS);
                        player.closeInventory();
                    }
                }
            }
            if (event.getInventory().getName().contains(ChatColor.YELLOW +  "Магазин")) {
                event.setCancelled(true);
                if (event.getCurrentItem().getItemMeta().hasLore()) {
                    Gamer gamer = GamerManager.getGamer(player);
                    if (!gamer.isInventory()) {
                        gamer.sendMessage(EMessage.NOINVENTORY);
                        return;
                    }
                    String last_lore;
                    ItemStack item = event.getCurrentItem().clone();
                    if (EConfig.SHOP.getConfig().getString("event") != null && item.getItemMeta().getLore().get(item.getItemMeta().getLore().size() - 1).contains(ChatColor.RED + "")) {
                        last_lore = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(event.getCurrentItem().getItemMeta().getLore().size() - 2)).toLowerCase();
                    } else {
                        last_lore = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(event.getCurrentItem().getItemMeta().getLore().size() - 1)).toLowerCase();
                    }
                    if (last_lore.contains("цена")) {
                        float cost = Float.parseFloat(last_lore.replace("•", "").replace("цена", "").replace(MoneyType.RUBLES.getShortName(), ""));
                        if (gamer.getMoney() < cost) {
                            gamer.sendMessage(EMessage.MONEYNEEDS);
                            player.closeInventory();
                            return;
                        }
                        ItemMeta meta = item.getItemMeta();
                        ArrayList<String> lore = new ArrayList<>(meta.getLore());
                        if (EConfig.SHOP.getConfig().getString("event") != null && item.getItemMeta().getLore().get(item.getItemMeta().getLore().size() - 1).contains(ChatColor.RED + "")) {
                            lore.remove(meta.getLore().size() - 3); lore.remove(meta.getLore().size() - 4);
                        }
                        lore.remove(meta.getLore().size() - 1); lore.remove(meta.getLore().size() - 2);
                        meta.setLore(lore);
                        item.setItemMeta(meta);

                        player.getInventory().addItem(item);
                        gamer.withdrawMoney(cost);
                        gamer.sendMessage(EMessage.SUCCESSFULBUY);
                    }
                }
            }
            if (event.getInventory().getName().equals(ChatColor.YELLOW + "Прокачка предмета")) {
                event.setCancelled(true);
                if (event.getCurrentItem() == null || event.getCurrentItem().getAmount() == 0 || player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getAmount() == 0 || !player.getInventory().getItemInMainHand().hasItemMeta() || UpgradeMisc.getSection(player) == null) {
                    return;
                }
                switch (event.getCurrentItem().getData().getData()) {
                    case 5: {
                        Upgrade.upgrade(player, false);
                        break;
                    }
                    case 14: {
                        player.closeInventory();
                        break;
                    }
                }
            }
            if (event.getInventory().getName().equals(ChatColor.YELLOW + "Ваши достижения") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Вскопанные блоки") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Меню доната") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Выбор фракции") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Профиль") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Список шахт") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Тренер") ||
                    event.getInventory().getName().contains(ChatColor.YELLOW + "Выберите предмет прокачки") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Ваши активные ускорители") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Активация ускорителей") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Аукцион") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Уведомления") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Перерождение") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Привилегии") ||
                    event.getInventory().getName().contains("Просмотр") ||
                    event.getInventory().getName().contains("Боевой пропуск") ||
                    event.getInventory().getName().contains("Вы уверены?") ||
                    event.getInventory().getName().contains("Работа") ||
                    event.getInventory().getName().contains("Задания шахты:") ||
                    event.getInventory().getName().contains("Магазин") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Продажа рыбы") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Повышение уровня") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Прокачка предмета")) {
                event.setCancelled(true);
            }
        } catch (Exception ignored) {  }
    }
}
