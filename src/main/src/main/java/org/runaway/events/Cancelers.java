package org.runaway.events;

import com.codingforcookies.armorequip.ArmorEquipEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.items.ItemManager;
import org.runaway.managers.GamerManager;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;

import java.util.ArrayList;
import java.util.List;

public class Cancelers implements Listener {

    public static List<Entity> toRemove = new ArrayList<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWeather(WeatherChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSpawn(CreatureSpawnEvent e) {
        if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDrop(PlayerDropItemEvent event) {
        ItemStack itemStack = event.getItemDrop().getItemStack();
        if (!ItemManager.isDropable(itemStack)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEquip(ArmorEquipEvent e) {
        Player player = e.getPlayer();
        if(e.getNewArmorPiece() != null && e.getNewArmorPiece().getType() != Material.AIR) {
            Gamer gamer = GamerManager.getGamer(e.getPlayer().getUniqueId());
            int lev = gamer.getLevelItem(e.getNewArmorPiece());
            if (gamer.getIntStatistics(EStat.LEVEL) < lev) {
                gamer.sendMessage(EMessage.MINLEVELITEM.getMessage().replace("%level%", lev + "") + "");
                e.setCancelled(true);
                return;
            }
        }
        //RUNES
        ItemStack newItem = null;
        if (e.getNewArmorPiece() != null) newItem = e.getNewArmorPiece().clone();
        ItemStack oldItem = null;
        if (e.getOldArmorPiece() != null) oldItem = e.getOldArmorPiece().clone();

        ItemStack finalOldItem = oldItem;
        ItemStack finalNewItem = newItem;
        new BukkitRunnable() {
            @Override
            public void run() {
                if (finalOldItem != null) {
                    List<Rune> runes1 = RuneManager.getRunes(finalOldItem);
                    if (!runes1.isEmpty()) {
                        for (Rune rune : runes1) {
                            if (rune == null) continue;
                            if (rune.constantEffects().isEmpty()) continue;
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    for (PotionEffect effect : rune.constantEffects()) {
                                        if (player.getActivePotionEffects().stream().anyMatch(potionEffect -> potionEffect.getType().equals(effect.getType()) && potionEffect.getAmplifier() == 6)) continue;
                                        player.removePotionEffect(effect.getType());
                                    }
                                }
                            }.runTask(Prison.getInstance());
                        }
                    }
                }
                if (finalNewItem != null) {
                    List<Rune> runes = RuneManager.getRunes(finalNewItem);
                    if (!runes.isEmpty()) {
                        for (Rune rune : runes) {
                            if (rune == null) continue;
                            if (rune.constantEffects().isEmpty()) continue;
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    for (PotionEffect effect : rune.constantEffects()) {
                                        boolean skip = false;
                                        for (PotionEffect active : player.getActivePotionEffects()) {
                                            if (active.getType().equals(effect.getType()) && active.getAmplifier() == 6) {
                                                skip = true;
                                            }
                                        }
                                        if (!skip) player.addPotionEffect(effect);
                                    }
                                }
                            }.runTask(Prison.getInstance());
                        }
                    }
                }
            }
        }.runTaskAsynchronously(Prison.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        Material t = event.getClickedBlock().getType();
        if (t.equals(Material.ANVIL) || t.equals(Material.HOPPER) || t.equals(Material.FURNACE)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeathEntity(EntityDeathEvent event) {
        event.setDroppedExp(0);
        event.getDrops().clear();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void craftItem(PrepareItemCraftEvent e) {
        e.getInventory().setResult(new ItemStack(Material.AIR));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockDrop(BlockPhysicsEvent e) {
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChunkUnload(ChunkUnloadEvent event) {
        /*Entity[] arrayOfEntity = event.getChunk().getEntities();
        for (Entity cEntity : arrayOfEntity) {
            if (cEntity.getType() != EntityType.PLAYER) {

            }
        }*/
    }
}
