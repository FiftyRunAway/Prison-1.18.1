package org.runaway.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.achievements.Achievement;
import org.runaway.board.Board;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.events.custom.PlayerKillEvent;
import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;
import org.runaway.items.parameters.ParameterManager;
import org.runaway.managers.GamerManager;
import org.runaway.needs.Need;
import org.runaway.runes.armor.RecoverRune;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;
import org.runaway.tasks.SyncTask;
import org.runaway.utils.Utils;

import java.util.List;
import java.util.Objects;

/*
 * Created by _RunAway_ on 27.1.2019
 */

public class PlayerDeath implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        Player player = event.getEntity();
        Gamer gamer = GamerManager.getGamer(player);
        event.setKeepInventory(true); event.setKeepLevel(true);
        boolean givenot = dropItems(event.getDrops(), gamer);
        combatLog(gamer);
        double money = gamer.getLevel();

        gamer.sendMessage(EMessage.DIEDPLAYER.getMessage().replace("%money%", Board.FormatMoney(money)));
        gamer.increaseIntStatistics(EStat.DEATHES);
        if (gamer.getIntStatistics(EStat.DEATHES) >= 5) Achievement.DEAD_5.get(player);
        if (gamer.getIntStatistics(EStat.DEATHES) >= 100) Achievement.DEAD_100.get(player);

        if (event.getEntity().getKiller() != null) {
            Gamer gamerKiller = GamerManager.getGamer(event.getEntity().getKiller().getUniqueId());
            gamerKiller.increaseIntStatistics(EStat.KILLS);
            if (gamerKiller.getIntStatistics(EStat.KILLS) >= 5) Achievement.KILL_5.get(gamerKiller.getPlayer());
            if (gamerKiller.getIntStatistics(EStat.KILLS) >= 100) Achievement.KILL_100.get(gamerKiller.getPlayer());
            if (givenot) return;

            Bukkit.getServer().getPluginManager().callEvent(new PlayerKillEvent(gamerKiller.getPlayer()));

            gamerKiller.depositMoney(money);
            gamerKiller.sendMessage(EMessage.KILLPLAYER.getMessage().replace("%player%", gamer.getGamer()).replace("%money%", Board.FormatMoney(money)));

            //RUNES
            List<Rune> runes = gamerKiller.getActiveRunes();
            if (gamerKiller.isActiveRune(new RecoverRune(), runes)) {
                RuneManager.runeAction(gamerKiller, "recover");
            }
            if (gamerKiller.getPlayer().hasPermission("*")) Achievement.KILL_ADMIN.get(gamerKiller.getPlayer());
        } else if (event.getEntity() instanceof Projectile && ((Projectile)event.getEntity()).getShooter() instanceof Player) {
            if (event.getEntity().getKiller().getName().equals(event.getEntity().getName())) Achievement.KILL_ARROW.get(player);
            Gamer gamerKiller = GamerManager.getGamer(event.getEntity().getKiller().getUniqueId());
            gamerKiller.increaseIntStatistics(EStat.BOW_KILL);
        }
    }

    private static void combatLog(Gamer gamer) {
        if (!Gamer.isEnabledCombatRelog()) return;
        Gamer.leaveCombat(gamer);
        gamer.getCombatLog().forEach(s -> {
            Gamer g = GamerManager.getGamer(s);
            g.getCombatLog().remove(gamer.getGamer());
            if (!g.getCombatLog().isEmpty()) return;
            Gamer.leaveCombat(g);
        });
    }

    public static boolean dropItems(List<ItemStack> items, Gamer gamer) {
        Location loc = gamer.getPlayer().getLocation();
        boolean result = false;
        double money = gamer.getLevel();
        if (gamer.getMoney() >= money) {
            gamer.withdrawMoney(money);
            if (gamer.isInPvp()) result = true;
        } else {
            gamer.setStatistics(EStat.MONEY, 0);
            result = true;
        }
        for (ItemStack itemStack : items) {
            if (itemStack == null) continue;
            if (ItemManager.isDropable(itemStack)) {
                Objects.requireNonNull(loc.getWorld()).dropItemNaturally(loc, itemStack);
                gamer.getPlayer().getInventory().remove(itemStack);
            }
        }
        return result;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        event.setRespawnLocation(Prison.SPAWN);
        Player player = event.getPlayer();
        Gamer gamer = GamerManager.getGamer(player);
        new SyncTask(() -> {
            gamer.getPassivePerks().forEach(passive -> {
                if (!passive.isEffectAction()) return;
                passive.getPerkAction(gamer);
            });
        }, 80);

        //RUNES
        if (player.getEquipment() != null) {
            for (ItemStack armor : player.getEquipment().getArmorContents()) {
                if (armor == null) continue;
                List<Rune> runes = RuneManager.getRunes(armor);
                for (Rune rune : runes) {
                    if (rune == null) continue;
                    if (rune.constantEffects().isEmpty()) continue;
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (PotionEffect effect : rune.constantEffects()) {
                                player.addPotionEffect(effect);
                            }
                        }
                    }.runTask(Prison.getInstance());
                }
            }
        }
        new SyncTask(() -> {
            for (Need need : gamer.getNeeds()) {
                if (need.isActive()) need.addPotion();
            }
        }, 80);
    }
}
