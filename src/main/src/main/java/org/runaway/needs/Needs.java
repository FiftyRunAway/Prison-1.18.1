package org.runaway.needs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import org.bukkit.scheduler.BukkitTask;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.donate.features.NeedsLonger;
import org.runaway.enums.EMessage;
import org.runaway.managers.GamerManager;
import org.runaway.passiveperks.perks.Vaccine;
import org.runaway.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class Needs implements Listener {

    public static HashMap<String, NeedsType> needs = new HashMap<>();
    private static HashMap<String, BukkitTask> tasks = new HashMap<>();
    private static ArrayList<String> data = new ArrayList<>();
    private static ArrayList<String> bed_cd = new ArrayList<>();

    public static void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Gamer gamer = GamerManager.getGamer(player);
        if (gamer.getOfflineDonateValue("needs").equals("1") || tasks.containsKey(player.getName())) {
            return;
        }
        int cooldown = 10; // 10 were
        Object obj = GamerManager.getGamer(player).getPrivilege().getValue(new NeedsLonger());
        if (obj != null) {
            cooldown = Integer.parseInt(obj.toString());
        }
        if (gamer.hasPassivePerk(new Vaccine())) cooldown *= 1.5;
        tasks.put(player.getName(), Bukkit.getScheduler().runTaskTimer(Prison.getInstance(), () -> {
            if(!player.hasPlayedBefore() || !gamer.isEndedCooldown("newPlayer")) {
                return;
            }
            if (player.getName() == null || needs.containsKey(player.getName())) return;
            int type = ThreadLocalRandom.current().nextInt(NeedsType.values().length);
            NeedsType t = NeedsType.values()[type];
            needs.put(player.getName(), t);
            player.addPotionEffect(new PotionEffect(NeedsType.getEffect(t), 999999, 5), true);
            check(player);
        }, cooldown * 1200L, cooldown * 1200L));
    }

    public static void onQuit(PlayerQuitEvent event) {
        String name = event.getPlayer().getName();
        if (tasks.containsKey(name)) {
            tasks.get(name).cancel();
            tasks.remove(name);
        }
    }

    private static void check(Player player) {
        ArrayList<String> list = NeedsType.getProperties(needs.get(player.getName()));
        Gamer gamer = GamerManager.getGamer(player);
        gamer.sendTitle(ChatColor.AQUA + list.get(1).split("%")[0], ChatColor.WHITE + list.get(1).split("%")[1]);
        player.sendMessage(Utils.colored(list.get(2)));
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if ((needs.containsKey(event.getPlayer().getName())) &&
                (NeedsType.getType(needs.get(event.getPlayer().getName())) == 1)) {
            Material m = player.getLocation().getBlock().getType();
            if (m.equals(Material.WATER)) {
                Gamer gamer = GamerManager.getGamer(player);
                gamer.sendMessage(EMessage.WASHED);
                needs.remove(player.getName());
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    if (effect.getType().equals(NeedsType.getEffect(NeedsType.WASH))) {
                        player.removePotionEffect(effect.getType());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType().equals(Material.LEGACY_BED_BLOCK)) {
            event.setCancelled(true);
            if (needs.containsKey(player.getName()) && NeedsType.getType(needs.get(player.getName())) == 3) {
                if (bed_cd.contains(player.getName())) return;
                bed_cd.add(player.getName());
                Bukkit.getScheduler().runTaskLater(Prison.getInstance(), () -> {
                    bed_cd.remove(player.getName());
                }, 40L);
                Gamer gamer = GamerManager.getGamer(player);
                gamer.sendMessage(EMessage.STARTSLEEPING);
                Bukkit.getScheduler().runTaskLater(Prison.getInstance(), () -> {
                    if (Math.random() > 0.5) {
                        needs.remove(player.getName());
                        for (PotionEffect effect : player.getActivePotionEffects()) {
                            if (effect.getType().equals(NeedsType.getEffect(NeedsType.SLEEP))) {
                                player.removePotionEffect(effect.getType());
                            }
                        }
                    } else {
                        gamer.sendMessage(EMessage.STOPSLEEPING);
                    }
                }, 40L);
            }
        }
        if ((event.getClickedBlock().getType().equals(Material.LEVER)) &&
                (needs.containsKey(player.getName())) &&
                (NeedsType.getType(needs.get(event.getPlayer().getName())) == 2)) {
            Gamer gamer = GamerManager.getGamer(player);
            gamer.sendMessage(EMessage.TOILET);
            needs.remove(player.getName());
            for (PotionEffect effect : player.getActivePotionEffects()) {
                /*if (effect.getType().equals(NeedsType.getEffect(NeedsType.TOILET))) {
                    player.removePotionEffect(effect.getType());
                } */
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        needs.remove(event.getEntity().getName());
    }
}
