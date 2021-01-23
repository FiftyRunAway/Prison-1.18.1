package org.runaway.entity;

import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import org.bukkit.Material;
import org.bukkit.configuration.file.*;
import net.minecraft.server.v1_12_R1.*;
import com.gmail.filoghost.holographicdisplays.api.*;
import org.bukkit.scheduler.*;
import java.util.*;
import org.bukkit.configuration.*;
import org.bukkit.*;
import org.runaway.Prison;
import org.runaway.donate.Privs;
import org.runaway.donate.features.BossNotify;
import org.runaway.items.Item;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;
import org.runaway.enums.EConfig;
import org.runaway.enums.Mobs;
import org.runaway.enums.TypeMessage;

public class Spawner {

    private static final FileConfiguration config = EConfig.MOBS.getConfig();
    public static Map<UUID, Spawner> spawners = new HashMap<>();
    private Location location;
    private Mobs type;
    private Entity current;
    private long deathTime;
    private int interval;
    private UUID uuid;
    private BukkitTask holoTask;
    private Hologram hologram;

    private Spawner(Location location, Mobs type, int interval) {
        this.location = location;
        this.current = null;
        this.deathTime = System.currentTimeMillis();
        this.uuid = UUID.randomUUID();
        this.type = type;
        this.interval = interval;
        Spawner.spawners.put(this.uuid, this);
        createHologram();
    }

    private void spawn() {
        try {
            if (this.location != null && this.location.getChunk() != null && this.location.getChunk().isLoaded()) {
                CustomEntity.spawnEntity(this.type, this.location.clone().add(0.0, 2.5, 0.0), this);
                if (this.holoTask != null) {
                    this.holoTask.cancel();
                    this.holoTask = null;
                }
                if (this.hologram != null) {
                    this.hologram.delete();
                    this.hologram = null;
                }
            }
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }

    public void dead() {
        this.current = null;
        this.deathTime = System.currentTimeMillis();
        createHologram();
    }

    private void createHologram() {
        if (!this.type.isMultispawn()) {
            (this.hologram = HologramsAPI.createHologram(Prison.getInstance(), this.location.clone().add(0.5, 2.5, 0.5))).setAllowPlaceholders(true);
            this.hologram.getVisibilityManager().setVisibleByDefault(true);
            this.holoTask = Bukkit.getScheduler().runTaskTimer(Prison.getInstance(), () -> {
                this.updateHolo();
                this.update();
            }, 0L, 1200L);
        }
    }

    private void updateHolo() {
        if (this.hologram == null) {
            return;
        }
        this.hologram.clearLines();
        this.hologram.appendTextLine(Utils.colored("&fБосс &7• " + EConfig.MOBS.getConfig().getString(type.toString().toLowerCase() + ".name")));
        long ticks = (this.interval * 1000L) - (System.currentTimeMillis() - this.deathTime);
        long hours = ticks / 3600000; ticks %= 3600000; long minutes = ticks / 60000;
        String time = (hours < 10 ? "0" : "" + hours) + " ч " + (minutes < 10 ? "0" + minutes : minutes) + " мин";
        if (minutes == 0) time = "< 1 мин";
        String format = String.format("&fВозрождение &7• &c%s", time);
        this.hologram.appendTextLine(Utils.colored(format));
        this.hologram.appendTextLine(" ");
        ItemLine line = this.hologram.appendItemLine(type.getIcon());
        StandardMenu menu = StandardMenu.create(1, "&eУведомления");

        line.setTouchHandler(player -> {
            boolean has = false;

            IMenuButton reset = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.DIAMOND).name("&aЗаспавнить сейчас").build().item());
            reset.setClickEvent(event -> spawn());
            reset.setSlot(0);
            if (player.hasPermission("prison.admin")) menu.addButton(reset);

            Object obj = Privs.DEFAULT.getPrivilege(player).getValue(new BossNotify());
            if (obj != null) has = true;
            if (has) {
                menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(type.getIcon().getType())
                        .name("&eУведомления о возрождении боссов")
                        .lore(new Lore.BuilderLore()
                        .addSpace()
                        .addString("&7>> &aВключены").build())
                .build().item()).setSlot(4));
            } else {
                menu.addButton(DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(type.getIcon().getType())
                        .name("&eУведомления о возрождении боссов")
                        .lore(new Lore.BuilderLore()
                                .addSpace()
                                .addString("&7>> &cВыключены")
                                .addString("&cДля получения доступа к этой функции")
                                .addString("&cПриобретите &bCrystal").build())
                        .build().item()).setSlot(4));
            }

            player.openInventory(menu.build());
        });
        this.hologram.appendTextLine(Utils.colored("&7[ПКМ]"));
    }

    public Location getSpawnLocation() {
        return this.location;
    }

    private void update() {
        if (this.current == null) {
            if ((System.currentTimeMillis() - this.deathTime >= this.interval * 1000) && this.interval > 0) {
                this.spawn();
            }
        } else if (this.current.getBukkitEntity().getLocation().distance(this.location) > 64.0) this.reset();
    }

    public void register(Entity me) {
        this.current = me;
    }

    private void reset() {
        if (this.current != null) {
            if (this.current.passengers != null) this.current.passengers.forEach(Entity::ejectPassengers);
            this.current.getBukkitEntity().remove();
        }
        this.deathTime = -1L;
        this.spawn();
    }

    public Entity getCurrent() {
        return this.current;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public Mobs getType() {
        return this.type;
    }

    public static class SpawnerUpdater extends BukkitRunnable {
        public void run() {
            for (Spawner cSpawner : Spawner.spawners.values()) cSpawner.update();
        }
    }

    public static class SpawnerUtils {

        public static void init() {
            ConfigurationSection section = Spawner.config.getConfigurationSection("mobs");
            for (String currentSpawn : section.getKeys(false)) {
                ConfigurationSection path = section.getConfigurationSection(currentSpawn);
                Location loc = Utils.unserializeLocation(path.getString("location"));
                Mobs type = Mobs.valueOf(path.getString("type").toUpperCase());
                int interval = EConfig.MOBS.getConfig().getInt(path.getString("type") + ".interval");
                new Spawner(loc, type, interval).update();
            }
            Vars.sendSystemMessage(TypeMessage.SUCCESS, Spawner.spawners.size() + " mob-spawners loaded!");
        }
    }
}
