package org.runaway.mines;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.Main;
import org.runaway.enums.EConfig;
import org.runaway.enums.EStat;
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
    EStat perm;
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
                    Mines mine = new Mines(cRegionString, Utils.colored(cRegion.getString("name")), cRegion.getInt("min-level"), loc, Material.getMaterial(cRegion.getString("icon").toUpperCase()), cRegion.getBoolean("needperm", false), getStat(cRegion.getString("permission")));
                    MineIcon icon = new MineIcon.Builder(mine).build();
                    Mines.icons.put(mine, icon);
                }
            }
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error in loading mines menu!");
            //Bukkit.getPluginManager().disablePlugin(Main.getInstance());
            Main.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    public static EStat getStat(String cfg) {
        switch (cfg) {
            case "prison.glad": {
                return EStat.LOCATION_GLAD;
            }
            case "prison.vault": {
                return EStat.LOCATION_VAULT;
            }
            case "prison.ice": {
                return EStat.LOCATION_ICE;
            }
            default: {
                return null;
            }
        }
    }

    private Mines(String id, String name, int minLevel, Location spawn, Material icon, boolean needPerm, EStat perm) {
        this.id = id;
        this.name = name;
        this.needPerm = needPerm;
        this.perm = perm;
        this.minLevel = minLevel;
        this.spawn = spawn;
        this.icon = icon;
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

    public EStat getPerm() {
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
