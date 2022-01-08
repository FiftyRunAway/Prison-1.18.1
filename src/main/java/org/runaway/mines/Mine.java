package org.runaway.mines;

import com.boydti.fawe.util.EditSessionBuilder;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.function.pattern.BlockPattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.patterns.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.runaway.items.Item;
import org.runaway.Prison;
import org.runaway.enums.EMessage;
import org.runaway.events.BlockBreak;
import org.runaway.managers.GamerManager;
import org.runaway.menu.button.DefaultButtons;
import org.runaway.menu.button.IMenuButton;
import org.runaway.menu.type.StandardMenu;
import org.runaway.utils.Utils;

/*
 * Created by _RunAway_ on 24.5.2019
 */

public class Mine {

    private String techName, name;
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

    private int left;
    private long reseted;
    private long will_reset;
    private StandardMenu adminMenu;

    public Mine(String techName, String name, int mlevel, boolean pvp, int height, int diametr, World world, int x, int y, int z, int delay, String types, Material material, int tpx, int tpy, int tpz, Location holo, boolean tpSpawn, Material surface) {
        this.techName = techName;
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
        this.hologram = HologramsAPI.createHologram(Prison.getInstance(), holoLoc);
        this.tpSpawn = tpSpawn;
        this.surface = surface;

        this.reseted = System.currentTimeMillis();
        this.will_reset = this.reseted + this.delay * 1000L;
        setupHolo(this);

        this.adminMenu = StandardMenu.create(1, "&eАдмин-панель шахты &7• &e" + this.name);

        IMenuButton reset = DefaultButtons.FILLER.getButtonOfItemStack(new Item.Builder(Material.DIAMOND).name("&aОбновить шахту").build().item()).setSlot(0);
        reset.setClickEvent(event -> {
            forceReset(this);
            event.getWhoClicked().closeInventory();
        });
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
                this.adminMenu.open(GamerManager.getGamer(player));
            }
        });
        int left = (int) Math.round((double)((mine.will_reset - System.currentTimeMillis()) / 1000));
        this.hologram.appendTextLine(Utils.colored("&fОбновится через &7• &a" + Math.round(Math.ceil((double) left / 60)) + " мин."));
        this.hologram.appendTextLine(Utils.colored("&fБлоков осталось &7• &a" + percent() + "%"));
        this.hologram.appendTextLine("");
        this.hologram.appendTextLine(Utils.colored("&fPVP &7• &a" + (this.pvp ? "Есть" : "&cНет")));

        updateHolo();
    }

    private void updateHolo() {
        holoTask = new BukkitRunnable() {
            @Override
            public void run() {
                forceUpdateHolo();
            }
        }.runTaskTimer(Prison.getInstance(), 0L, 1200L);
    }

    private void forceUpdateHolo() {
        int left = (int) Math.round((double)((will_reset - System.currentTimeMillis()) / 1000));
        hologram.removeLine(2);
        hologram.insertTextLine(2, Utils.colored("&fОбновится через &7• &a" + Math.round(Math.ceil((double) left / 60)) + " мин."));
        long p = percent();
        hologram.removeLine(3);
        hologram.insertTextLine(3, Utils.colored("&fБлоков осталось &7• &a" + p + "%"));
    }

    private long percent() {
        double broken = 0, blocks = 0;
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
        if (broken == 0) return 100;
        double s = blocks - broken;
        float nm = (float) (s / blocks);

        return Math.round(nm * 100);
    }

    private void forceReset(Mine mine) {
        mineTask.cancel();
        holoTask.cancel();
        reseted = System.currentTimeMillis();
        will_reset = reseted + delay * 1000L;
        setupHolo(mine);
        updateMine(mine);
    }

    public static void updateMine(Mine mine) {
        mineTask = new BukkitRunnable() {
            public void run() {
                Location loc = new Location(mine.world, mine.getX(), mine.getY(), mine.getZ());
                int diametr = mine.getDiametr();
                Location endLoc = new Location(mine.world, (mine.getX() - diametr), (mine.getY() - mine.getHeight()), (mine.getZ() - diametr));
                Utils.getPlayers().forEach(s -> {
                    Player p = Bukkit.getPlayer(s);
                    Location playerLoc = p.getLocation();
                    if (BlockBreak.isInRegion(loc, endLoc, playerLoc)) {
                        if (mine.tpSpawn) {
                            p.teleport(new Location(mine.world, mine.x, mine.y, mine.z));
                            GamerManager.getGamer(p).sendMessage(EMessage.MINERESET);
                            return;
                        }
                        playerLoc.setY(loc.getY() + 1.0);
                        p.teleport(playerLoc);
                    }
                });
                mine.reseted = System.currentTimeMillis();
                mine.will_reset = mine.reseted + mine.delay * 1000L;
                mine.forceUpdateHolo();
                boolean isSurface = false;
                if (!mine.surface.equals(Material.AIR)) {
                    isSurface = true;
                    try {
                        CuboidSelection cuboidSelection = new CuboidSelection(loc.getWorld(),
                                loc,
                                loc.clone().subtract(mine.getDiametr() - 1, 0, mine.getDiametr() - 1));
                        EditSession editSession = new EditSessionBuilder(BukkitUtil.getLocalWorld(loc.getWorld())).build();
                        BlockPattern blockPattern = new BlockPattern(new BaseBlock(mine.surface.getId()));
                        editSession.setBlocks(cuboidSelection.getRegionSelector().getRegion(), (Pattern) blockPattern);
                        editSession.setFastMode(true);
                        editSession.flushQueue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                try {
                    CuboidSelection cuboidSelection = new CuboidSelection(loc.getWorld(),
                            loc.clone().subtract(0, isSurface ? 1 : 0, 0),
                            loc.clone().subtract(mine.getDiametr() - 1, mine.getHeight() - 1, mine.getDiametr() - 1));
                    EditSession editSession = new EditSessionBuilder(BukkitUtil.getLocalWorld(loc.getWorld())).build();
                    RandomPattern randomPattern = new RandomPattern();
                    for (String str : mine.getTypes().split(" ")) {
                        Material mat;
                        int subid = 0;
                        if (str.contains(":")) {
                            String[] splitter = str.split(":");
                            mat = Material.valueOf(splitter[0].toUpperCase());
                            subid = Integer.parseInt(splitter[1]);
                        } else {
                            mat = Material.valueOf(str);
                        }
                        randomPattern.add(new BlockPattern(new BaseBlock(mat.getId(), subid)), 1);
                    }
                    editSession.setBlocks(cuboidSelection.getRegionSelector().getRegion(), (Pattern) randomPattern);
                    editSession.setFastMode(true);
                    editSession.flushQueue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(Prison.getInstance(), 0L, mine.getDelay() * 20L);
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

    public int getDiametr() {
        return this.diametr;
    }

    public int getHeight() {
        return this.height;
    }

    private int getZ() {
        return this.z;
    }

    private String getTypes() {
        return this.types;
    }

    public Location getMineLocation() {
        return new Location(world, this.x, this.y, this.z);
    }
}
