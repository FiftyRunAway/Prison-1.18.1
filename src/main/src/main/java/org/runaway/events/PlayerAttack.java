package org.runaway.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.enums.EMessage;
import org.runaway.enums.EStat;
import org.runaway.enums.FactionType;
import org.runaway.events.custom.PlayerDamageEvent;
import org.runaway.managers.GamerManager;
import org.runaway.passiveperks.perks.Killer;
import org.runaway.runes.armor.*;
import org.runaway.runes.sword.*;
import org.runaway.runes.utils.Rune;
import org.runaway.runes.utils.RuneManager;
import org.runaway.trainer.TypeTrainings;
import org.runaway.utils.Utils;

import java.util.List;

public class PlayerAttack implements Listener {

    @EventHandler
    public void onPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;
        if (event.getEntity() instanceof Player && (event.getDamager() instanceof Player ||
                (event.getDamager() instanceof Projectile && ((Projectile)event.getDamager()).getShooter() instanceof Player) ||
                (event.getDamager() instanceof FishHook && ((FishHook)event.getDamager()).getShooter() instanceof Player))) {
            Player damager = null;
            if (event.getDamager() instanceof Player) damager = (Player) event.getDamager();
            if (event.getDamager() instanceof Projectile) damager = (Player) ((Projectile) event.getDamager()).getShooter();
            Gamer source = GamerManager.getGamer(damager.getUniqueId());
            if (!canAttack(damager, (Player)event.getEntity())) {
                source.sendMessage(EMessage.FRIENDATTACK);
                event.setCancelled(true);
            } else {
                source.inPvpWith(GamerManager.getGamer((Player)event.getEntity()));
                Gamer taker = GamerManager.getGamer((Player) event.getEntity());
                //RUNES
                List<Rune> activeRunes = taker.getActiveRunes();
                List<Rune> activeRunesAttacker = source.getActiveRunes();
                if (taker.isActiveRune(new FreezeRune(), activeRunes)) {
                    RuneManager.runeAction(source, "freeze");
                }
                if (taker.isActiveRune(new EnlightenedRune(), activeRunes)) {
                    RuneManager.runeAction(taker, "enlightened");
                }
                if (taker.isActiveRune(new FortifyRune(), activeRunes)) {
                    RuneManager.runeAction(source, "fortify");
                }
                if (taker.isActiveRune(new MoltenRune(), activeRunes)) {
                    RuneManager.runeAction(source, "molten");
                }
                if (taker.isActiveRune(new PainGiverRune(), activeRunes)) {
                    RuneManager.runeAction(source, "paingiver");
                }
                if (taker.isActiveRune(new SmokeBombRune(), activeRunes)) {
                    RuneManager.runeAction(source, "smokebomb");
                }
                if (taker.isActiveRune(new VoodooRune(), activeRunes)) {
                    RuneManager.runeAction(source, "voodoo");
                }

                if (source.isActiveRune(new InsomniaRune(), activeRunesAttacker)) {
                    if (Math.random() < 0.2) {
                        event.setDamage(event.getDamage() * 2);
                        source.sendMessage("&fПрименена руна " + RuneManager.getRuneName(new InsomniaRune()));
                    }
                }
                if (source.isActiveRune(new ExecutionRune(), activeRunesAttacker)) {
                    if (taker.getPlayer().getHealth() <= 2 && Math.random() < 0.08) {
                        source.addEffect(PotionEffectType.INCREASE_DAMAGE, 100, 3);
                        source.sendMessage("&fПрименена руна " + RuneManager.getRuneName(new ExecutionRune()));
                    }
                }
                if (source.isActiveRune(new ObliterateRune(), activeRunesAttacker)) {
                    if (Math.random() < 0.08) {
                        taker.getPlayer().setVelocity(damager.getLocation().getDirection().multiply(2).setY(1.25));
                        source.sendMessage("&fПрименена руна " + RuneManager.getRuneName(new ObliterateRune()));
                    }
                }
                if (source.isActiveRune(new SnareRune(), activeRunesAttacker)) {
                    RuneManager.runeAction(taker, "snare");
                }
                if (source.isActiveRune(new TrapRune(), activeRunesAttacker)) {
                    RuneManager.runeAction(taker, "trap");
                }
                if (source.isActiveRune(new WitherRune(), activeRunesAttacker)) {
                    RuneManager.runeAction(taker, "wither");
                }
                if (source.isActiveRune(new ParalyzeRune(), activeRunesAttacker)) {
                    if (Math.random() < 0.05) {
                        Player player = taker.getPlayer();
                        damager.getWorld().spigot().strikeLightningEffect(player.getLocation(), true);
                        try {
                            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 10f, 1);
                        } catch (Exception ignore) { }
                        for (Entity entity : player.getWorld().getNearbyEntities(player.getLocation(), 10, 10, 10)) {
                            if (entity.getUniqueId().equals(player.getUniqueId())) {
                                ((LivingEntity)entity).damage(5D);
                                break;
                            }
                        }
                        taker.addEffect(PotionEffectType.SLOW, 60, 2);
                        taker.addEffect(PotionEffectType.SLOW_DIGGING, 60, 2);
                        source.sendMessage("&fПрименена руна " + RuneManager.getRuneName(new ParalyzeRune()));
                    }
                }
                if (source.isActiveRune(new NutritionRune(), activeRunesAttacker)) {
                    RuneManager.runeAction(source, "nutrition");
                }
                if (source.isActiveRune(new StormCallerRune(), activeRunesAttacker)) {
                    RuneManager.runeAction(source, "stormcaller");
                }
                if (source.isActiveRune(new SlowMoRune(), activeRunesAttacker)) {
                    RuneManager.runeAction(source, "slowmo");
                }
                if (source.isActiveRune(new BlindnessRune(), activeRunesAttacker)) {
                    RuneManager.runeAction(source, "blindness");
                }
                if (source.isActiveRune(new ViperRune(), activeRunesAttacker)) {
                    RuneManager.runeAction(source, "viper");
                }
                if (taker.isActiveRune(new SaviorRune(), activeRunes)) {
                    if (Math.random() < 0.09) {
                        event.setDamage(event.getDamage() / 2);
                        taker.sendMessage("&fПрименена руна " + RuneManager.getRuneName(new SaviorRune()));
                    }
                }
                if (taker.isActiveRune(new CactusRune(), activeRunes)) {
                    if (Math.random() < 0.1) {
                        damager.damage(event.getDamage() * 0.75);
                        taker.sendMessage("&fПрименена руна " + RuneManager.getRuneName(new CactusRune()));
                    }
                }
                if (source.isActiveRune(new VampireRune(), activeRunesAttacker)) {
                    if (Math.random() < 0.1) {
                        double maxHealth = damager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                        if (damager.getHealth() + event.getDamage() / 2 < maxHealth) {
                            damager.setHealth(damager.getHealth() + event.getDamage() / 2);
                        } else {
                            damager.setHealth(maxHealth);
                        }
                        source.sendMessage("&fПрименена руна " + RuneManager.getRuneName(new VampireRune()));
                    }
                }
                if (source.isActiveRune(new LifeStealRune(), activeRunesAttacker)) {
                    RuneManager.runeAction(source, "lifesteal");
                }
                if (source.isActiveRune(new DoubleDamageRune(), activeRunesAttacker)) {
                    if (Math.random() < 0.075) {
                        event.setDamage(event.getDamage() * 2);
                        source.sendMessage("&fПрименена руна " + RuneManager.getRuneName(new DoubleDamageRune()));
                    }
                }
                Bukkit.getServer().getPluginManager().callEvent(new PlayerDamageEvent(damager, (Player)event.getEntity()));
            }
        }
        if (event.getDamager() instanceof Player) {
            Player damager = (Player)event.getDamager();
            Gamer attacker = GamerManager.getGamer(damager);
            if (damager.getInventory().getItemInMainHand().getType().toString().endsWith("SWORD")) {
                if (attacker.getIntStatistics(EStat.LEVEL) < attacker.getLevelItem()) {
                    event.setCancelled(true);
                    attacker.sendMessage(Utils.colored(EMessage.MINLEVELITEM.getMessage()).replace("%level%", attacker.getLevelItem() + ""));
                    return;
                }
            }
            int boost = 0;
            if (attacker.hasPassivePerk(new Killer())) boost += 1;
            double resulteDamage = event.getDamage() + (double) (attacker.getTrainingLevel(TypeTrainings.GYM.name()) + boost) * 4 / 100;

            event.setDamage(resulteDamage);
        }
    }

    @EventHandler
    public void onPlayerFishing(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (event.isCancelled()) return;
        if (event.getCaught() instanceof Player &&
                player.getInventory().getItemInMainHand().getType().equals(Material.FISHING_ROD) &&
                !canAttack(player, (Player) event.getCaught())) {
            GamerManager.getGamer(player).sendMessage(EMessage.FRIENDATTACK);
            event.setCancelled(true);
        }
    }

    private boolean canAttack(Player attacker, Player getDamager) {
        if (attacker == null) return true;
        Gamer at = GamerManager.getGamer(attacker.getUniqueId());
        Gamer get = GamerManager.getGamer(getDamager.getUniqueId());
        if (at.getFaction().equals(get.getFaction())) {
            return at.getFaction().equals(FactionType.DEFAULT);
        }
        return true;
    }
}
