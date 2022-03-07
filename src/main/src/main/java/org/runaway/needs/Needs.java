package org.runaway.needs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.enums.EMessage;
import org.runaway.managers.GamerManager;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Needs implements Listener {

    private static ArrayList<String> bed_cd = new ArrayList<>();

    public static void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = GamerManager.getGamer(player);
        if (gamer.getOfflineDonateValue("needs").equals("1") ||
                !gamer.isEndedCooldown("newPlayer") ||
                !player.hasPlayedBefore()) {
            turnOff(gamer);
            return;
        }
        for (NeedsType type : NeedsType.values()) {
            gamer.getNeeds().add(new Need(type, gamer));
        }
    }

    public static void onQuit(PlayerQuitEvent event) {
        String name = event.getPlayer().getName();
    }

    public static void turnOff(Gamer gamer) {
        for (PotionEffect active : gamer.getPlayer().getActivePotionEffects()) {
            if (active.getAmplifier() != 6) continue;
            for (NeedsType needType : NeedsType.values()) {
                if (NeedsType.getEffect(needType).equals(active.getType())) {
                    gamer.getPlayer().getActivePotionEffects().remove(active);
                }
            }
        }
        Need.offHuds(gamer);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getLocation().getBlock().getType().equals(Material.WATER) &&
                GamerManager.getGamer(player).getNeeds().stream().anyMatch(need -> need.getType().equals(NeedsType.WASH) &&
                need.getMessageTask() != null)) {
            Gamer gamer = GamerManager.getGamer(player);
            gamer.sendMessage(EMessage.WASHED);
            Need.rerun(gamer, NeedsType.WASH);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType().equals(Material.RED_BED)) {
            event.setCancelled(true);
            if (GamerManager.getGamer(player).getNeeds().stream().anyMatch(need -> need.getType().equals(NeedsType.SLEEP) &&
                    need.getMessageTask() != null)) {
                if (bed_cd.contains(player.getName())) return;
                bed_cd.add(player.getName());
                Bukkit.getScheduler().runTaskLater(Prison.getInstance(), () -> bed_cd.remove(player.getName()), 40L);
                Gamer gamer = GamerManager.getGamer(player);
                gamer.sendMessage(EMessage.STARTSLEEPING);
                Bukkit.getScheduler().runTaskLater(Prison.getInstance(), () -> {
                    if (ThreadLocalRandom.current().nextFloat() > 0.6) {
                        Need.rerun(gamer, NeedsType.SLEEP);
                        gamer.sendMessage("&aХорошо выспались, а теперь пора в шахту!");
                    } else {
                        gamer.sendMessage(EMessage.STOPSLEEPING);
                    }
                }, 40L);
            }
        }/*
        if ((event.getClickedBlock().getType().equals(Material.LEVER)) &&
                (needs.containsKey(player.getName())) &&
                (NeedsType.getType(needs.get(event.getPlayer().getName())) == 2)) {
            Gamer gamer = GamerManager.getGamer(player);
            gamer.sendMessage(EMessage.TOILET);
            needs.remove(player.getName());
            for (PotionEffect effect : player.getActivePotionEffects()) {
                if (effect.getType().equals(NeedsType.getEffect(NeedsType.TOILET))) {
                    player.removePotionEffect(effect.getType());
                }
            }
        }*/
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        //needs.remove(event.getEntity().getName());
    }
}
