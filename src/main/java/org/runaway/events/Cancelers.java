package org.runaway.events;

import com.codingforcookies.armorequip.ArmorEquipEvent;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.utils.Utils;

public class Cancelers implements Listener {

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
        if (event.getItemDrop().getItemStack().getItemMeta().getLore() != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEquip(ArmorEquipEvent e) {
        if(e.getNewArmorPiece() != null && e.getNewArmorPiece().getType() != Material.AIR) {
                Gamer gamer = Main.gamers.get(e.getPlayer().getUniqueId());
                if (gamer.getIntStatistics(EStat.LEVEL) < gamer.getLevelItem(e.getNewArmorPiece())) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(Utils.colored(EMessage.MINLEVELITEM.getMessage()).replaceAll("%level%", gamer.getLevelItem() + ""));
                }
            }
        }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        Material t = event.getClickedBlock().getType();
        if (t.equals(Material.ANVIL) || t.equals(Material.HOPPER) || t.equals(Material.FURNACE) || t.equals(Material.BURNING_FURNACE)) event.setCancelled(true);
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
        Entity[] arrayOfEntity = event.getChunk().getEntities();
        for (Entity cEntity : arrayOfEntity) {
            if (cEntity.getType() != EntityType.PLAYER) {
                event.setCancelled(true);
                break;
            }
        }
    }
}
