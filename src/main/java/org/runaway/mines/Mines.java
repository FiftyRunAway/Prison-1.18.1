package org.runaway.mines;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.enums.EConfig;
import org.runaway.enums.ServerStatus;
import org.runaway.enums.TypeMessage;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/*
 * Created by _RunAway_ on 4.5.2019
 */

public class Mines {

    public static Map<String, Mines> mines = new LinkedHashMap<>();
    public static HashMap<Mines, MineIcon> icons = new HashMap<>();
    String id;
    String name;
    boolean needPerm;
    String perm;
    int minLevel;
    Location spawn;
    Material icon;

    public static void loadMinesMenu() {
        try {
            if (EConfig.MINES.getConfig().getKeys(false).size() > 0) {
                for (String cRegionString : EConfig.MINES.getConfig().getKeys(false)) {
                    ConfigurationSection cRegion = EConfig.MINES.getConfig().getConfigurationSection(cRegionString);
                    if (!cRegion.contains("name")) continue;
                    ConfigurationSection cLoc = cRegion.getConfigurationSection("location");
                    Location loc = new Location(Bukkit.getWorld(cLoc.getString("world")), cLoc.getDouble("x"), cLoc.getDouble("y"), cLoc.getDouble("z"));
                    Mines mine = new Mines(cRegionString, Utils.colored(cRegion.getString("name")), cRegion.getInt("min-level"), loc, Material.getMaterial(cRegion.getString("icon").toUpperCase()), cRegion.getBoolean("needperm", false), cRegion.getString("permission"));
                    MineIcon icon = new MineIcon.Builder(mine).build();
                    Mines.icons.put(mine, icon);
                }
            }
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error in loading mines menu!");
            Prison.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    private Mines(String id, String name, int minLevel, Location spawn, Material icon, boolean needPerm, String loc_name) {
        this.id = id;
        this.name = name;
        this.needPerm = needPerm;
        this.perm = loc_name;
        this.minLevel = minLevel;
        this.spawn = spawn;
        this.icon = icon;
        if (needPerm) {
            org.runaway.mines.Location.locations.add(new org.runaway.mines.Location(ChatColor.stripColor(this.name), loc_name));
        }
        Mines.mines.put(id, this);
    }

    public static Map<String, Mines> getMines() {
        return Mines.mines;
    }

    public String getName() {
        return this.name;
    }

    public boolean needPerm() {
        return this.needPerm;
    }

    public String getLocName() {
        return this.perm;
    }

    public int getMinLevel() {
        return this.minLevel;
    }

    public Location getSpawn() {
        return this.spawn;
    }

    public ItemStack getPrisonIcon(Gamer gamer) {
        return icons.get(this).acessButton(gamer);
    }

    public static Mines getPrisonMine(String id) {
        if (Mines.mines.containsKey(id)) {
            return Mines.mines.get(id);
        }
        return null;
    }

    public static boolean isPrisonRegion(String id) {
        return Mines.mines.containsKey(id);
    }
}
