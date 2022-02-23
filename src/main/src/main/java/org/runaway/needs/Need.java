package org.runaway.needs;

import dev.lone.itemsadder.api.FontImages.PlayerHudsHolderWrapper;
import dev.lone.itemsadder.api.FontImages.PlayerQuantityHudWrapper;
import lombok.Getter;
import me.bigteddy98.bannerboard.api.BannerBoardManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.runaway.Gamer;
import org.runaway.tasks.SyncRepeatTask;
import org.runaway.tasks.SyncTask;
import org.runaway.utils.Utils;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class Need {
    private final NeedsType type;
    private final Gamer gamer;
    private SyncRepeatTask messageTask;
    private SyncTask timer;
    private SyncRepeatTask hudTimer;
    private int floatValue;
    private final long openMillis;
    private long startMillis;

    public Need(NeedsType type, Gamer gamer) {
        boolean rejoin = false;
        for (PotionEffect effect : gamer.getPlayer().getActivePotionEffects()) {
            if (effect.getAmplifier() == 6 && effect.getType().equals(NeedsType.getEffect(type))) {
                rejoin = true;
                break;
            }
        }
        this.type = type;
        this.gamer = gamer;
        this.openMillis = System.currentTimeMillis();
        this.floatValue = NeedsType.getHudMaxAmount(type);

        int cd = rejoin ? 0 : gamer.getNeedCooldown(type);
        Player player = gamer.getPlayer();

        int unpause = gamer.getIntQuestValue(type.name().toLowerCase(Locale.ROOT) + "Left");
        if (rejoin) unpause = 0;

        this.startMillis = this.openMillis + (unpause == 0 ? (TimeUnit.MINUTES.toMillis(cd)) : unpause);

        this.timer = new SyncTask(() -> {
            this.startMillis = System.currentTimeMillis();
            addPotion();

            List<String> list = NeedsType.getProperties(type);
            gamer.sendTitle(ChatColor.AQUA + list.get(1).split("%")[0], ChatColor.WHITE + list.get(1).split("%")[1]);
            gamer.sendMessage(list.get(2));

            this.messageTask = new SyncRepeatTask(() -> gamer.sendMessage(NeedsType.getProperties(type).get(2)), 250, 250);

            setHudValue(gamer, type, 0);
        }, unpause == 0 ? 1200 * cd : (int) TimeUnit.MILLISECONDS.toMinutes(unpause) * 1200);

        Bukkit.getConsoleSender().sendMessage("secs - " + TimeUnit.MILLISECONDS.toSeconds(unpause));

        if (rejoin) return;

        this.hudTimer = new SyncRepeatTask(() -> {
            PlayerHudsHolderWrapper hudsHolderWrapper = new PlayerHudsHolderWrapper(player);
            PlayerQuantityHudWrapper hud = new PlayerQuantityHudWrapper(hudsHolderWrapper, NeedsType.getHud(type));
            if (hud.exists() && floatValue > 0) {
                hud.setFloatValue(floatValue);
                floatValue--;
            } else {
                this.hudTimer.stop();
            }
        }, unpause == 0 ? (1200 * cd / NeedsType.getHudMaxAmount(type)) : (int) (TimeUnit.MILLISECONDS.toMinutes(unpause) * 1200 / NeedsType.getHudMaxAmount(type)));
    }

    public static void stopAll(Gamer gamer) {
        for (Need need : gamer.getNeeds()) {
            need.cancelAllTasks();

            save(gamer, need);
        }
        gamer.getNeeds().clear();
    }

    public static void stopOne(Gamer gamer, NeedsType type) {
        AtomicReference<Need> toDelete = new AtomicReference<>();
        gamer.getNeeds().forEach(need -> {
            if (need.getType().equals(type)) {
                need.cancelAllTasks();
                toDelete.set(need);

                save(gamer, need);
            }
        });
        if (gamer.getNeeds().stream().map(Need::getType).anyMatch(type1 -> type1.equals(type))) gamer.getNeeds().remove(toDelete.get());
    }

    public static void runAll(Gamer gamer) {
        for (NeedsType type : NeedsType.values()) {
            gamer.getNeeds().add(new Need(type, gamer));
        }
    }

    public static void runOne(Gamer gamer, NeedsType needsType) {
        for (NeedsType type : NeedsType.values()) {
            if (type.equals(needsType)) {
                gamer.getNeeds().add(new Need(type, gamer));
            }
        }
    }

    public static void rerun(Gamer gamer, NeedsType type) {
        Need.removeEffect(gamer, type);
        stopOne(gamer, type);
        runOne(gamer, type);

        setHudValue(gamer, type, NeedsType.getHudMaxAmount(type));
    }

    private static void setHudValue(Gamer gamer, NeedsType type, float value) {
        PlayerHudsHolderWrapper hudsHolderWrapper = new PlayerHudsHolderWrapper(gamer.getPlayer());
        PlayerQuantityHudWrapper hud = new PlayerQuantityHudWrapper(hudsHolderWrapper, NeedsType.getHud(type));
        if (hud.exists()) hud.setFloatValue(value);
    }

    public static void offHuds(Gamer gamer) {
        for (NeedsType needsType : NeedsType.values()) {
            PlayerHudsHolderWrapper hudsHolderWrapper = new PlayerHudsHolderWrapper(gamer.getPlayer());
            PlayerQuantityHudWrapper hud = new PlayerQuantityHudWrapper(hudsHolderWrapper, NeedsType.getHud(needsType));
            if (hud.exists()) {
                hud.setFloatValue(NeedsType.getHudMaxAmount(needsType));
            }
        }
    }

    private void cancelAllTasks() {
        if (this.messageTask != null) this.messageTask.stop();
        if (this.hudTimer != null) this.hudTimer.stop();
        if (this.timer != null) this.timer.stop();
        this.timer = null;
    }

    public static void removeEffect(Gamer gamer, NeedsType type) {
        for (PotionEffect effect : gamer.getPlayer().getActivePotionEffects()) {
            if (effect.getType().equals(NeedsType.getEffect(type)) &&
                    effect.getAmplifier() == 6) {
                gamer.getPlayer().removePotionEffect(effect.getType());

                gamer.getActiveRunes().forEach(rune -> {
                    for (PotionEffect potionEffect : rune.constantEffects()) {
                        if (potionEffect.getType().equals(effect.getType())) {
                            gamer.getPlayer().addPotionEffect(potionEffect);
                        }
                    }
                });
            }
        }
    }

    public static boolean save(Gamer gamer, Need need) {
        if (need.startMillis <= System.currentTimeMillis()) {
            gamer.setQuestValue(need.getType().name().toLowerCase(Locale.ROOT) + "Left", 0);
            return true;
        }
        gamer.setQuestValue(need.getType().name().toLowerCase(Locale.ROOT) + "Left", (int) (need.startMillis - System.currentTimeMillis()));
        return false;
    }

    public boolean isActive() {
        return this.messageTask != null;
    }

    public void addPotion() {
        this.gamer.addEffect(NeedsType.getEffect(this.type), Integer.MAX_VALUE, 6);
    }
}
