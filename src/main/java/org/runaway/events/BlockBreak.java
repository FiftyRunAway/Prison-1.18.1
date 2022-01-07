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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;
import org.runaway.Gamer;
import org.runaway.items.Item;
import org.runaway.Prison;
import org.runaway.achievements.Achievement;
import org.runaway.enums.*;
import org.runaway.events.custom.BreakWoodEvent;
import org.runaway.events.custom.DropKeyEvent;
import org.runaway.events.custom.PlayerBlockBreakEvent;
import org.runaway.events.custom.TreasureFindEvent;
import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;
import org.runaway.items.parameters.Parameter;
import org.runaway.items.parameters.ParameterManager;
import org.runaway.managers.GamerManager;
import org.runaway.passiveperks.perks.KeyFirst;
import org.runaway.passiveperks.perks.KeySecond;
import org.runaway.trainer.TypeTrainings;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/*
 * Created by _RunAway_ on 17.1.2019
 */

public class BlockBreak implements Listener {

    //private static final List<Material> blocktolog = new ArrayList<>();
    static HashMap<String, Block> chests = new HashMap<>();
    static HashMap<String, BukkitTask> chests_tasks = new HashMap<>();

    private static int LeftChest;

    static HashMap<String, Hologram> treasure_holo = new HashMap<>();

    // Ломание предметов с аукциона
    static HashMap<String, Integer> to_break = new HashMap<>();
    private static final int damage_per = 10;

    //Что можно ломать
    private static HashMap<Material, ArrayList<Material>> canbreak;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = GamerManager.getGamer(player);
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        if (!event.isCancelled()) {
            String name = player.getInventory().getItemInMainHand().getType().toString();
            if ((name.contains("AXE") || name.contains("SHOVEL") || name.contains("PICKAXE") || name.contains("SHEARS") || name.contains("SPADE"))) {
                Block block = event.getBlock();
                if (name.contains("PICKAXE") && !canbreak.get(player.getInventory().getItemInMainHand().getType()).contains(block.getType())) {
                    event.setCancelled(true);
                    return;
                }
                if ((block.getType().equals(Material.SAND) ||
                        block.getType().equals(Material.GRAVEL) ||
                        block.getType().equals(Material.DIRT)) && gamer.getIntStatistics(EStat.LEVEL) < 2) {
                    gamer.sendMessage(EMessage.SECONDLEVEL);
                    event.setCancelled(true);
                    return;
                }
                if (gamer.getIntStatistics(EStat.LEVEL) < gamer.getLevelItem()) {
                    event.setCancelled(true);
                    gamer.sendMessage(EMessage.MINLEVELITEM.getMessage().replace("%level%", gamer.getLevelItem() + ""));
                    return;
                }
                if (block.getType().equals(Material.CHEST) && chests.containsValue(block)) {
                    event.setCancelled(true);
                    return;
                }
                double boost = 1;
                if (gamer.hasPassivePerk(new KeyFirst())) boost += 1;
                if (gamer.hasPassivePerk(new KeySecond())) boost += 1;
                if (Math.random() < (0.002 * (gamer.getTrainingLevel(TypeTrainings.LUCK.name()) + boost)) && !block.getType().isTransparent()) {
                    gamer.sendTitle(Utils.colored(EMessage.FOUNDKEY.getMessage()));
                    gamer.addItem("defaultKey");
                    Bukkit.getServer().getPluginManager().callEvent(new DropKeyEvent(event.getPlayer(), event.getBlock()));
                    gamer.setStatistics(EStat.KEYS, gamer.getIntStatistics(EStat.KEYS) + 1);
                }
                Bukkit.getServer().getPluginManager().callEvent(new PlayerBlockBreakEvent(player, block));
                double add = gamer.getBoosterBlocks();
                gamer.addCurrentBlocks(block.getType().toString(), block.getData(), add);
                gamer.setStatistics(EStat.BLOCKS, BigDecimal.valueOf(gamer.getDoubleStatistics(EStat.BLOCKS) + gamer.getBoosterBlocks()).setScale(2, RoundingMode.UP).doubleValue());
                gamer.setExpProgress();
                AutoSell(event, FindChest(event));

                /*ItemStack itemStack = player.getInventory().getItemInMainHand();
                if(itemStack == null) return;
                PrisonItem prisonItem = ItemManager.getPrisonItem(player.getInventory().getItemInMainHand());
                if(prisonItem == null) return;
                Parameter stBlocksParameter = ParameterManager.getStattrakBlocksParameter();
                if(!prisonItem.getParameters().contains(stBlocksParameter)) return;

                double oldSTBlocks = (double) stBlocksParameter.getParameterGetter().
                        apply(itemStack, null);
                stBlocksParameter.changeValues(itemStack, BigDecimal.valueOf(oldSTBlocks + add).setScale(1, RoundingMode.UP).doubleValue()); */
            } else {
                gamer.sendMessage(EMessage.BREAKBYTOOLS);
                event.setCancelled(true);
            }
        } else logForest(event);
    }

    private void logForest(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = GamerManager.getGamer(player);
        Block block = event.getBlock();
        if (block.getType().equals(Material.LOG_2) && block.getData() == 13) {
            if (player.getInventory().getItemInMainHand() != null && player.getInventory().getItemInMainHand().hasItemMeta() && !player.getInventory().getItemInMainHand().getItemMeta().isUnbreakable()) {
                if (!to_break.containsKey(player.getName())) to_break.put(player.getName(), 0);
                int al = to_break.get(player.getName());
                if (al == damage_per) {
                    to_break.put(player.getName(), 0);
                    ItemStack item = player.getInventory().getItemInMainHand();
                    item.setDurability((short) (item.getDurability() + 1));
                    if (item.getDurability() - item.getType().getMaxDurability() >= 0) {
                        player.getInventory().getItemInMainHand().setType(Material.AIR);
                    }
                } else {
                    to_break.put(player.getName(), to_break.get(player.getName()) + 1);
                }
            }
            if (gamer.getIntStatistics(EStat.LEVEL) < gamer.getLevelItem()) {
                event.setCancelled(true);
                player.sendMessage(Utils.colored(EMessage.MINLEVELITEM.getMessage()).replace("%level%", gamer.getLevelItem() + ""));
                return;
            }
            if (!player.getInventory().getItemInMainHand().getType().toString().contains("AXE")) {
                gamer.sendMessage(EMessage.BREAKBYTOOLS);
                event.setCancelled(true);
                return;
            }
            if (isLocation(event.getBlock().getLocation(), "forest")) {
                AutoSell(event, false);
                if (Math.random() < (0.0025 * gamer.getTrainingLevel(TypeTrainings.LUCK.name()) + 1) && !block.getType().isTransparent()) {
                    gamer.sendTitle(Utils.colored(EMessage.FOUNDKEY.getMessage()));
                    gamer.addItem("defaultKey");
                    Bukkit.getServer().getPluginManager().callEvent(new DropKeyEvent(player, event.getBlock()));
                    gamer.increaseIntStatistics(EStat.KEYS);
                }
                double add = gamer.getBoosterBlocks();
                gamer.addCurrentBlocks("LOG_2", 1, add);
                gamer.setStatistics(EStat.BLOCKS, gamer.getDoubleStatistics(EStat.BLOCKS) + gamer.getBoosterBlocks());
                block.setTypeIdAndData(Material.WOOD.getId(), (byte)5, true);
                Bukkit.getServer().getPluginManager().callEvent(new BreakWoodEvent(player));
                Bukkit.getScheduler().runTaskLater(Prison.getInstance(), () -> {
                    block.setType(Material.LOG_2);
                    block.setData((byte) 13);
                }, 250L);
            }
        }
    }//normal random for key - 0.005

    //normal random for treasure - 0.00015
    private boolean FindChest(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Gamer gamer = GamerManager.getGamer(player);
        if (Math.random() < 0.00015 && !block.getType().isTransparent()) { // 0.00015
            if (chests.containsKey(player.getName())) {
                chests.get(player.getName()).setType(Material.AIR);
                chests.remove(player.getName());
                chests_tasks.remove(player.getName());
                treasure_holo.get(player.getName()).delete();
                gamer.sendMessage(EMessage.DELETECHEST);
            }
            gamer.sendTitle (ChatColor.RED + "Да ты везунчик!", ChatColor.RED + "Вы откопали клад (" + LeftChest + " сек)");
            chests.put(player.getName(), block);
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 20, 20);
            Bukkit.getServer().getPluginManager().callEvent(new TreasureFindEvent(player));
            chests_tasks.put(player.getName(), Bukkit.getScheduler().runTaskLater(Prison.getInstance(), () -> {
                if (!chests.containsKey(player.getName())) return;
                block.setType(Material.AIR);
                chests.remove(player.getName());
                treasure_holo.get(player.getName()).delete();
                //player.sendMessage(Utils.colored(EMessage.TIMELEFTCHEST.getMessage().replace("%time%", LeftChest + "")));
                chests_tasks.remove(player.getName());
            }, LeftChest * 20L));
            if (Prison.useHolographicDisplays) {
                Hologram hologram = HologramsAPI.createHologram(Prison.getInstance(), block.getLocation().add(0.5, 1.5, 0.5));
                hologram.appendTextLine(ChatColor.WHITE + "Нашёл " + ChatColor.YELLOW + player.getName());
                hologram.appendTextLine(ChatColor.RED + "Забери!");
                treasure_holo.put(player.getName(), hologram);
            }
            Achievement.FIRST_TREASURE.get(player);
            return true;
        }
        return false;
    }

    private void AutoSell(BlockBreakEvent event, boolean chest) {
        Player player = event.getPlayer();
        Gamer gamer = GamerManager.getGamer(player);
        Block block = event.getBlock();
        event.setCancelled(true);
        PlayerInventory inventory = player.getInventory();

        if (!inventory.getItemInMainHand().getItemMeta().isUnbreakable()) {
            if (!to_break.containsKey(player.getName())) to_break.put(player.getName(), 0);
            int al = to_break.get(player.getName());
            if (al == damage_per - 1) {
                to_break.put(player.getName(), 0);
                ItemStack item = inventory.getItemInMainHand();
                item.setDurability((short) (item.getDurability() + 1));
                if (item.getDurability() - item.getType().getMaxDurability() >= 0) {
                    inventory.getItemInMainHand().setAmount(0);
                }
            } else {
                to_break.put(player.getName(), to_break.get(player.getName()) + 1);
            }
        }

        if (chest) {
            block.setType(Material.CHEST);
            return;
        }

        ArrayList<ItemStack> i = new ArrayList<>();
        if (!inventory.getItemInMainHand().getType().equals(Material.AIR) && inventory.getItemInMainHand().getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
            i.add(new ItemStack(block.getType(), 1, block.getData()));
        } else {
            block.getDrops().forEach(itemStack -> i.add(new ItemStack(itemStack.getType(), 1, itemStack.getDurability())));
        }
        block.setType(Material.AIR);

        if (!gamer.getBooleanStatistics(EStat.AUTOSELL)) {
            if (!gamer.isInventory()) {
                gamer.sendMessage(EMessage.NOINVENTORY);
            }
            i.forEach(itemStack -> player.getInventory().addItem(itemStack));
        } else {
            if (!gamer.hasPermission("autosell")) {
                gamer.setStatistics(EStat.AUTOSELL, false);
            }
            sell(i, gamer);
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
        if (price == 0) {
            for (ItemStack itemStack : items) {
                player.getInventory().addItem(itemStack);
            }
            gamer.sendMessage(EMessage.NOTADDEDBLOCK);
        } else {
            price *= gamer.getBoosterMoney();
            gamer.depositMoney(price);
            gamer.sendActionbar(ChatColor.translateAlternateColorCodes('&',
                    "&eАвто-продажа: &a+" + BigDecimal.valueOf(price).setScale(2, RoundingMode.UP).doubleValue() + " " + MoneyType.RUBLES.getShortName() + " &7[&e" + gamer.getBoosterMoney() + "x&7]"));
        }
    }

    public void loadLogger() {
        try {
            LeftChest = EConfig.CONFIG.getConfig().getInt("chest_time");
            //EConfig.CONFIG.getConfig().getStringList("logger").forEach(s -> blocktolog.add(Material.valueOf(s)));

            canbreak = new HashMap<>();
            canbreak.put(Material.WOOD_PICKAXE, breakableByPickaxe(Material.WOOD_PICKAXE));
            canbreak.put(Material.STONE_PICKAXE, breakableByPickaxe(Material.STONE_PICKAXE));
            canbreak.put(Material.IRON_PICKAXE, breakableByPickaxe(Material.IRON_PICKAXE));
            canbreak.put(Material.DIAMOND_PICKAXE, breakableByPickaxe(Material.DIAMOND_PICKAXE));
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error with loading log blocks!");
            //Bukkit.getPluginManager().disablePlugin(Prison.getInstance());
            Prison.getInstance().setStatus(ServerStatus.ERROR);
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

    private static ArrayList<Material> breakableByPickaxe(Material item) {
        ArrayList<Material> list = new ArrayList<>();
        switch (item) {
            case WOOD_PICKAXE: {
                list.add(Material.STONE);
                list.add(Material.SMOOTH_BRICK);
                list.add(Material.COAL_ORE);
                list.add(Material.PRISMARINE);
                list.add(Material.STAINED_GLASS);
                list.add(Material.NETHERRACK);
                list.add(Material.BRICK);
                list.add(Material.SANDSTONE);
                list.add(Material.COAL_BLOCK);
                list.add(Material.QUARTZ_ORE);
                list.add(Material.PACKED_ICE);
                list.add(Material.ICE);
                list.add(Material.STAINED_CLAY);
                list.add(Material.CONCRETE);
                break;
            }
            case STONE_PICKAXE: {
                list = breakableByPickaxe(Material.WOOD_PICKAXE);
                list.add(Material.ENDER_STONE);
                list.add(Material.END_BRICKS);
                list.add(Material.IRON_ORE);
                list.add(Material.SMOOTH_BRICK);
                list.add(Material.LAPIS_BLOCK);
                list.add(Material.RED_NETHER_BRICK);
                list.add(Material.MAGMA);
                list.add(Material.QUARTZ_BLOCK);
                list.add(Material.IRON_BLOCK);
                break;
            }
            case IRON_PICKAXE: {
                list = breakableByPickaxe(Material.STONE_PICKAXE);
                list.add(Material.GOLD_ORE);
                list.add(Material.DIAMOND_ORE);
                list.add(Material.EMERALD_BLOCK);
                list.add(Material.DIAMOND_BLOCK);
                list.add(Material.GOLD_BLOCK);
                break;
            }
            case DIAMOND_PICKAXE: {
                list = breakableByPickaxe(Material.IRON_PICKAXE);
                list.add(Material.OBSIDIAN);
                break;
            }
        }
        return list;
    }
}
