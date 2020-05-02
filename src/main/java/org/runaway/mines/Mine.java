package org.runaway.mines;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.runaway.Item;
import org.runaway.Main;
import org.runaway.enums.EMessage;
import org.runaway.events.BlockBreak;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.utils.Items;
import org.runaway.utils.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/*
 * Created by _RunAway_ on 24.5.2019
 */

public class Mine {

    private String name;
    private int mlevel;
    private boolean pvp;
    private int height;
    private int diametr;
    private World world;
    private int x;
    private int y;
    private int z;
    private int tpx;
    private int tpy;
    private int tpz;
    private int delay;
    private String types;
    private Material material;
    private Location holoLoc;
    private Hologram hologram;
    private boolean tpSpawn;

    private Material surface;

    private static BukkitTask mineTask;
    private static BukkitTask holoTask;

    private int mins_left;
    private StandardMenu adminMenu;

    public Mine(String name, int mlevel, boolean pvp, int height, int diametr, World world, int x, int y, int z, int delay, String types, Material material, int tpx, int tpy, int tpz, Location holo, boolean tpSpawn, Material surface) {
        this.name = name;
        this.material = material;
        this.mlevel = mlevel;
        this.pvp = pvp;
        this.height = height;
        this.diametr = diametr;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.types = types;
        this.delay = delay;
        this.tpx = tpx;
        this.tpy = tpy;
        this.tpz = tpz;
        this.holoLoc = holo;
        this.hologram = HologramsAPI.createHologram(Main.getInstance(), holoLoc);
        this.tpSpawn = tpSpawn;

        this.surface = surface;

        this.mins_left = delay / 60;
        setupHolo(this);

        this.adminMenu = StandardMenu.create(1, "&eАдмин-панель шахты &7• &e" + this.name);

        IMenuButton reset = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.DIAMOND).name("&aОбновить шахту").build().item()).setSlot(0);
        reset.setClickEvent(event -> forceReset(this));
        this.adminMenu.addButton(reset);
    }

    private void setupHolo(Mine mine) {
        if (this.hologram == null) {
            return;
        }
        this.hologram.clearLines();

        this.hologram.appendTextLine(Utils.colored("&fШахта &7• &e" + this.name));
        ItemLine line = this.hologram.appendItemLine(new ItemStack(this.material));
        line.setTouchHandler(player -> {
            if (player.isOp()) {
                player.openInventory(this.adminMenu.build());
            }
        });
        this.hologram.appendTextLine(Utils.colored("&fОбновится через &7• &a" + mins_left + " мин."));
        this.hologram.appendTextLine(Utils.colored("&fPVP &7• &a" + (this.pvp ? "Есть" : "&cНет")));

        updateHolo(mine);
    }

    private void updateHolo(Mine mine) {
        mine.holoTask = new BukkitRunnable() {
            @Override
            public void run() {
                if ((mine.mins_left - 1) == 0) {
                    mine.mins_left = mine.delay / 60;
                } else {
                    mine.mins_left--;
                }
                mine.hologram.removeLine(2);
                mine.hologram.insertTextLine(2, Utils.colored("&fОбновится через &7• &a" + mins_left + " мин."));
            }
        }.runTaskTimer(Main.getInstance(), 0L, 1200L);
    }

    private long percent() {
        int broken = 0, blocks = 0;
        Location loc = new Location(this.world, this.x, this.y, this.z);
        for (int i = 0; i < this.height; i++) {
            for (int q = 0; q < this.diametr; q++) {
                for (int l = 0; l < this.diametr; l++) {
                    blocks++;
                    if (loc.getBlock().getType().equals(Material.AIR)) broken++;
                    loc.subtract(1.0, 0.0, 0.0);
                    if (l == diametr - 1) {
                        loc.subtract((-l - 1), 0.0, 0.0);
                    }
                }
                loc.subtract(0.0, 0.0, 1.0);
                if (q == diametr - 1) {
                    loc.subtract(0.0, 0.0, (-q - 1));
                }
            }
            loc.subtract(0.0, 1.0, 0.0);
        }
        int s = blocks - broken;

        return (long) (new BigDecimal(s / blocks).setScale(2, RoundingMode.UP).doubleValue() * 100);
    }

    private void forceReset(Mine mine) {
        mineTask.cancel();
        holoTask.cancel();
        mins_left = getDelay() / 60;
        setupHolo(mine);
        updateMine(mine);
    }

    public static void updateMine(Mine mine) {
        mineTask = new BukkitRunnable() {
            public void run() {
                Random random = new Random();
                Location loc = new Location(mine.world, mine.getX(), mine.getY(), mine.getZ());
                int diametr = mine.getDiametr();
                Location endLoc = new Location(mine.world, (mine.getX() - diametr), (mine.getY() - mine.getHeight()), (mine.getZ() - diametr));
                Utils.getPlayers().forEach(s -> {
                    Player p = Bukkit.getPlayer(s);
                    Location playerLoc = p.getLocation();
                    if (BlockBreak.isInRegion(loc, endLoc, playerLoc)) {
                        if (mine.tpSpawn) {
                            p.teleport(new Location(mine.world, mine.x, mine.y, mine.z));
                            Main.gamers.get(p.getUniqueId()).sendMessage(EMessage.MINERESET);
                            return;
                        }
                        playerLoc.setY(loc.getY() + 1.0);
                        p.teleport(playerLoc);
                    }
                });
                int highFloor = loc.getBlockY();
                boolean isSurface = false;
                if (!mine.surface.equals(Material.AIR)) isSurface = true;
                for (int i = 0; i < mine.getHeight(); i++) {
                    for (int q = 0; q < diametr; q++) {
                        for (int l = 0; l < diametr; l++) {
                            String str = mine.getTypes().split(" ")[random.nextInt(mine.getTypes().split(" ").length)];
                            Material mat;
                            int subid = 0;
                            if (str.contains(":")) {
                                String[] splitter = str.split(":");
                                mat = Material.valueOf(splitter[0].toUpperCase());
                                subid = Integer.parseInt(splitter[1]);
                            } else {
                                mat = Material.valueOf(str);
                            }
                            if (loc.getBlockY() == highFloor && isSurface) {
                                loc.getBlock().setType(mine.surface);
                            } else {
                                loc.getBlock().setTypeIdAndData(mat.getId(), (byte) subid, true);
                            }
                            loc.subtract(1.0, 0.0, 0.0);
                            if (l == diametr - 1) {
                                loc.subtract((-l - 1), 0.0, 0.0);
                            }
                        }
                        loc.subtract(0.0, 0.0, 1.0);
                        if (q == diametr - 1) {
                            loc.subtract(0.0, 0.0, (-q - 1));
                        }
                    }
                    loc.subtract(0.0, 1.0, 0.0);
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, mine.getDelay() * 20L);
    }

    public boolean getPvp() {
        return this.pvp;
    }

    public String getName() {
        return this.name;
    }

    public Material getMaterial() {
        return this.material;
    }

    public int getMlevel() {
        return this.mlevel;
    }

    public int getX() {
        return this.x;
    }

    private int getDelay() {
        return this.delay;
    }

    private int getY() {
        return this.y;
    }

    private int getDiametr() {
        return this.diametr;
    }

    private int getHeight() {
        return this.height;
    }

    private int getZ() {
        return this.z;
    }

    private String getTypes() {
        return this.types;
    }

    public Location getMineLocation() {
        return new Location(world, this.tpx, this.tpy, this.tpz);
    }
}
