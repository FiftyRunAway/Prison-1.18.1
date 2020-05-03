package org.runaway.events;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.runaway.*;
import org.runaway.achievements.Achievement;
import org.runaway.enums.*;
import org.runaway.events.custom.BreakWoodEvent;
import org.runaway.events.custom.DropKeyEvent;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/*
 * Created by _RunAway_ on 17.1.2019
 */

public class BlockBreak implements Listener {

    private static List<Material> blocktolog = new ArrayList<>();
    static HashMap<String, Location> chests = new HashMap<>();

    private static int LeftChest;

    static HashMap<String, Hologram> treasure_holo = new HashMap<>();

    // Ломание предметов с аукциона
    static HashMap<String, Integer> to_break = new HashMap<>();
    private static int damage_per = 10;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = Main.gamers.get(player.getUniqueId());
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        if (!event.isCancelled()) {
            String name = player.getInventory().getItemInMainHand().getType().toString();
            if ((name.contains("AXE") || name.contains("SHOVEL") || name.contains("PICKAXE") || name.contains("SHEARS") || name.contains("SPADE"))) {
                Block block = event.getBlock();
                if ((block.getType().equals(Material.SAND) ||
                        block.getType().equals(Material.GRAVEL) ||
                        block.getType().equals(Material.DIRT)) && (int)gamer.getStatistics(EStat.LEVEL) < 2) {
                    gamer.sendMessage(EMessage.SECONDLEVEL);
                    return;
                }
                if ((int)gamer.getStatistics(EStat.LEVEL) < gamer.getLevelItem()) {
                    event.setCancelled(true);
                    player.sendMessage(Utils.colored(EMessage.MINLEVELITEM.getMessage()).replaceAll("%level%", gamer.getLevelItem() + ""));
                    return;
                }
                if (block.getType().equals(Material.CHEST) && chests.containsValue(block.getLocation())) {
                    event.setCancelled(true);
                    return;
                }
                if (Math.random() < (0.002 * ((int)gamer.getStatistics(EStat.LUCK_TRAINER) + 1)) && !block.getType().isTransparent()) {
                    gamer.sendTitle(Utils.colored(EMessage.FOUNDKEY.getMessage()));
                    event.getPlayer().getInventory().addItem(new Item.Builder(Material.GHAST_TEAR).name("&7Ключ к обычному сундуку").build().item());
                    Bukkit.getServer().getPluginManager().callEvent(new DropKeyEvent(event.getPlayer()));
                    gamer.setStatistics(EStat.KEYS, (int)gamer.getStatistics(EStat.KEYS) + 1);
                }
                double add = gamer.getBoosterBlocks();
                if (blocktolog.contains(block.getType())) {
                    if (EConfig.BLOCKS.getConfig().contains(player.getName() + "." + block.getType().toString() + "-" + block.getType().getMaxDurability())) {
                        add += EConfig.BLOCKS.getConfig().getDouble(player.getName() + "." + block.getType().toString() + "-" + block.getType().getMaxDurability());
                    }
                    String ret = String.valueOf(new BigDecimal(add).setScale(2, RoundingMode.UP).doubleValue());
                    EConfig.BLOCKS.getConfig().set(player.getName() + "." + block.getType().toString() + "-" + block.getType().getMaxDurability(), Double.valueOf(ret));
                    EConfig.BLOCKS.saveConfig();
                }
                gamer.setStatistics(EStat.BLOCKS, new BigDecimal((double)gamer.getStatistics(EStat.BLOCKS) + gamer.getBoosterBlocks()).setScale(2, RoundingMode.UP).doubleValue());
                gamer.setExpProgress();
                AutoSell(event, FindChest(event));
            } else {
                gamer.sendMessage(EMessage.BREAKBYTOOLS);
                event.setCancelled(true);
            }
        } else logForest(event);
    }

    private void logForest(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = Main.gamers.get(player.getUniqueId());
        Block block = event.getBlock();
        if (block.getType().equals(Material.LOG_2) && block.getData() == 1) {
            if ((int) gamer.getStatistics(EStat.LEVEL) < gamer.getLevelItem()) {
                event.setCancelled(true);
                player.sendMessage(Utils.colored(EMessage.MINLEVELITEM.getMessage()).replaceAll("%level%", gamer.getLevelItem() + ""));
                return;
            }
            if (!player.getInventory().getItemInMainHand().getType().toString().contains("AXE")) {
                gamer.sendMessage(EMessage.BREAKBYTOOLS);
                event.setCancelled(true);
                return;
            }
            if (isLocation(event.getBlock().getLocation(), "forest")) {
                if (Math.random() < (0.002 * ((int)gamer.getStatistics(EStat.LUCK_TRAINER) + 1)) && !block.getType().isTransparent()) {
                    gamer.sendTitle(Utils.colored(EMessage.FOUNDKEY.getMessage()));
                    player.getInventory().addItem(new Item.Builder(Material.GHAST_TEAR).name("&7Ключ к обычному сундуку").build().item());
                    Bukkit.getServer().getPluginManager().callEvent(new DropKeyEvent(player));
                    gamer.setStatistics(EStat.KEYS, (int)gamer.getStatistics(EStat.KEYS) + 1);
                }
                double add = gamer.getBoosterBlocks();
                if (EConfig.BLOCKS.getConfig().contains(player.getName() + ".LOG_2-0")) {
                    add += EConfig.BLOCKS.getConfig().getDouble(player.getName() + ".LOG_2-0");
                }
                String ret = String.valueOf(new BigDecimal(add).setScale(2, RoundingMode.UP).doubleValue());
                EConfig.BLOCKS.getConfig().set(player.getName() + ".LOG_2-0", ret);
                EConfig.BLOCKS.saveConfig();
                gamer.setStatistics(EStat.BLOCKS, (double)gamer.getStatistics(EStat.BLOCKS) + gamer.getBoosterBlocks());
                block.setTypeIdAndData(Material.WOOD.getId(), (byte)1, true);
                Bukkit.getServer().getPluginManager().callEvent(new BreakWoodEvent(player));
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    block.setType(Material.LOG_2);
                    block.setData((byte)1);
                }, 250L);
                AutoSell(event, false);
            }
        }
    }//normal random for key - 0.001

    //normal random for treasure - 0.00015
    private boolean FindChest(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Gamer gamer = Main.gamers.get(player.getUniqueId());
        if (Math.random() < 0.00015 && !block.getType().isTransparent()) {
            if (chests.containsKey(player.getName())) {
                chests.get(player.getName()).getBlock().setType(Material.AIR);
                chests.remove(player.getName());
                gamer.sendMessage(EMessage.DELETECHEST);
            }
            gamer.sendTitle (ChatColor.RED + "Да ты везунчик!", ChatColor.RED + "Вы откопали клад (" + LeftChest + " сек)");
            chests.put(player.getName(), block.getLocation());
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 20, 20);
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                if (chests.containsKey(player.getName())) {
                    block.setType(Material.AIR);
                    chests.remove(player.getName());
                    player.sendMessage(Utils.colored(EMessage.TIMELEFTCHEST.getMessage().replace("%time%", LeftChest + "")));
                }
            }, LeftChest * 20L);
            if (Main.useHolographicDisplays) {
                Hologram hologram = HologramsAPI.createHologram(Main.getInstance(), block.getLocation().add(0.5, 1.5, 0.5));
                hologram.appendTextLine(ChatColor.WHITE + "Нашёл " + ChatColor.YELLOW + player.getName());
                hologram.appendTextLine(ChatColor.RED + "Забери!");
                treasure_holo.put(player.getName(), hologram);
            }
            Achievement.FIRST_TREASURE.get(player, false);
            return true;
        }
        return false;
    }

    private void AutoSell(BlockBreakEvent event, boolean chest) {
        Player player = event.getPlayer();
        Gamer gamer = Main.gamers.get(player.getUniqueId());
        Block block = event.getBlock();
        event.setCancelled(true);
        if (!player.getInventory().getItemInMainHand().getItemMeta().isUnbreakable()) {
            if (!to_break.containsKey(player.getName())) to_break.put(player.getName(), 0);
            int al = to_break.get(player.getName());
            if (al == damage_per) {
                to_break.put(player.getName(), 0);
                ItemStack item = player.getInventory().getItemInMainHand();
                item.setDurability((short) (item.getDurability() + 1));
                if (item.getDurability() <= 0) {
                    item.setAmount(0);
                }
            } else {
                to_break.put(player.getName(), to_break.get(player.getName()) + 1);
            }
        }
        if (gamer.getStatistics(EStat.AUTOSELL).equals(true)) {
            if (!chest) {
                if (player.getInventory().getItemInMainHand().getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
                    ArrayList<ItemStack> i = new ArrayList<>();
                    i.add(new ItemStack(block.getType()));
                    sell(i, gamer);
                    block.setType(Material.AIR);
                    return;
                }
                sell(block.getDrops(), gamer);
                block.setType(Material.AIR);
                return;
            }
            block.setType(Material.CHEST);
        } else {
            if (!gamer.isInventory()) {
                gamer.sendMessage(EMessage.NOINVENTORY);
            }
            if (!chest) {
                if (player.getInventory().getItemInMainHand().getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
                    player.getInventory().addItem(new ItemStack(block.getType()));
                    block.setType(Material.AIR);
                    return;
                }
                block.getDrops().forEach(itemStack -> player.getInventory().addItem(itemStack));
                block.setType(Material.AIR);
                return;
            }
            block.setType(Material.CHEST);
        }
    }

    private void sell(Collection<ItemStack> items, Gamer gamer) {
        Player player = gamer.getPlayer();
        double price = 0;
        for (ItemStack itemStack : items) {
            String str = itemStack.getType().toString() + "|" + itemStack.getDurability();
            for (String s : EConfig.SHOP.getConfig().getStringList("shop")) {
                String[] var = s.split(" ");
                String ret = var[0] + "|" + var[2];
                if (str.equalsIgnoreCase(ret)) {
                    price += Double.parseDouble(var[1]);
                }
            }
        }
        if (price == 0.0) {
            for (ItemStack itemStack : items) {
                player.getInventory().addItem(itemStack);
            }
            gamer.sendMessage(EMessage.NOTADDEDBLOCK);
        } else {
            price *= gamer.getBoosterMoney();
            gamer.depositMoney(price);
            gamer.sendActionbar(ChatColor.translateAlternateColorCodes('&',
                    "&eАвтопродажа: &a+" + new BigDecimal(price).setScale(2, RoundingMode.UP).doubleValue() + " руб. &7[&e" + gamer.getBoosterMoney() + "x&7]"));
        }
    }

    public void loadLogger() {
        try {
            LeftChest = EConfig.CONFIG.getConfig().getInt("chest_time");
            EConfig.CONFIG.getConfig().getStringList("logger").forEach(s -> blocktolog.add(Material.valueOf(s)));
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with loading log blocks!");
            //Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            Main.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    public static boolean isLocation(Location obj, String config) {
        boolean b = false;
        for (String string : EConfig.CONFIG.getConfig().getStringList("locations." + config)) {
            String[] var = string.split(" ");
            int x1 = Integer.parseInt(var[0]);
            int y1 = Integer.parseInt(var[1]);
            int z1 = Integer.parseInt(var[2]);
            Location loc1 = new Location(Bukkit.getWorld(var[6]), x1, y1, z1);
            int x2 = Integer.parseInt(var[3]);
            int y2 = Integer.parseInt(var[4]);
            int z2 = Integer.parseInt(var[5]);
            Location loc2 = new Location(Bukkit.getWorld(var[6]), x2, y2, z2);
            if (isInRegion(loc1, loc2, obj)) {
                b = true;
            }
        }
        return b;
    }

    public static boolean isInRegion(Location l1, Location l2, Location location) {
        return isIn(l1.getBlockX(), l1.getBlockY(), l1.getBlockZ(), l2.getBlockX(), l2.getBlockY(), l2.getBlockZ(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    private static boolean isIn(int x1, int y1, int z1, int x2, int y2, int z2, int x, int y, int z) {
        return Math.min(x1, x2) <= x && Math.min(y1, y2) <= y && Math.min(z1, z2) <= z && Math.max(x1, x2) >= x && Math.max(y1, y2) >= y && Math.max(z1, z2) >= z;
    }
}
