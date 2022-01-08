package org.runaway.mines;

import org.bukkit.*;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.Prison;
import org.runaway.achievements.Achievement;
import org.runaway.entity.IMobController;
import org.runaway.entity.MobManager;
import org.runaway.enums.EConfig;
import org.runaway.enums.EMessage;
import org.runaway.enums.ServerStatus;
import org.runaway.enums.TypeMessage;
import org.runaway.items.ItemManager;
import org.runaway.items.PrisonItem;
import org.runaway.items.parameters.ParameterManager;
import org.runaway.requirements.BlocksRequire;
import org.runaway.requirements.LocalizedBlock;
import org.runaway.requirements.MoneyRequire;
import org.runaway.requirements.RequireList;
import org.runaway.utils.ItemBuilder;
import org.runaway.utils.Lore;
import org.runaway.utils.Utils;
import org.runaway.utils.Vars;

import java.util.*;

/*
 * Created by _RunAway_ on 4.5.2019
 */

public class Mines {

    public static List<Mines> mines = new ArrayList<>();
    public static Map<Mines, MineIcon> icons = new HashMap<>();
    String id;
    String name;
    boolean needPerm;
    String perm;
    int minLevel;
    Location spawn;
    Material icon;
    short subId;
    String uidBoss;
    IMobController boss;
    MineIcon mineIcon;

    public static void loadMinesMenu() {
        try {
            if (!EConfig.MINES.getConfig().getKeys(false).isEmpty()) {
                for (String cRegionString : EConfig.MINES.getConfig().getKeys(false)) {
                    ConfigurationSection cRegion = EConfig.MINES.getConfig().getConfigurationSection(cRegionString);
                    if (!cRegion.contains("name")) continue;
                    short subid = 0;
                    Material material = Material.getMaterial(cRegion.getString("icon").toUpperCase().split(":")[0]);
                    if (cRegion.getString("icon").split(":").length > 1) {
                        subid = Short.parseShort(cRegion.getString("icon").split(":")[1]);
                    }
                    String boss = null;
                    if (cRegion.contains("boss"))
                        boss = cRegion.getString("boss");
                    ConfigurationSection cLoc = cRegion.getConfigurationSection("location");
                    Location loc = new Location(Bukkit.getWorld(cLoc.getString("world")), cLoc.getDouble("x"), cLoc.getDouble("y"), cLoc.getDouble("z"));
                    new Mines(cRegionString, Utils.colored(cRegion.getString("name")), cRegion.getInt("min-level"), loc, material, subid, cRegion.getBoolean("needperm", false), cRegion.getString("permission"), boss);
                }
            }
        } catch (Exception ex) {
            Vars.sendSystemMessage(TypeMessage.ERROR, "Error in loading mines menu!");
            Prison.getInstance().setStatus(ServerStatus.ERROR);
            ex.printStackTrace();
        }
    }

    private Mines(String id, String name, int minLevel, Location spawn, Material icon, short subId, boolean needPerm, String loc_name, String uidBoss) {
        this.id = id;
        this.name = name;
        this.needPerm = needPerm;
        this.perm = loc_name;
        this.minLevel = minLevel;
        this.spawn = spawn;
        this.icon = icon;
        this.subId = subId;
        this.uidBoss = uidBoss;
        this.boss = null;
        if (this.uidBoss != null)
            this.boss = MobManager.uidMobControllerMap.getOrDefault(this.uidBoss, null);
        this.mineIcon = new MineIcon.Builder(this).build();
        if (needPerm) {
            //org.runaway.mines.Location.locations.add(new org.runaway.mines.Location(ChatColor.stripColor(this.name), loc_name));
            PrisonItem prisonItem = PrisonItem.builder()
                    .vanillaName(this.id.toLowerCase() + "Pass") //тех. название предмета
                    .vanillaItem(new ItemBuilder(Material.BOOK)
                            .name(this.name)
                            .addLoreLine("&7>> ПКМ для открытия доступа")
                            .build()) //билд предмета
                    .consumerOnClick(gamer -> {
                        if(gamer.getIntQuestValue(this.id + "Pass") == 1) {
                            gamer.sendMessage("&aУ вас уже есть доступ к этой локации!");
                            return;
                        }
                        gamer.setQuestValue(this.id + "Pass", 1);
                        gamer.sendMessage(EMessage.ACTIVATELOCATION);
                        gamer.getPlayer().playSound(gamer.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                        Achievement.FIRST_LOCATION.get(gamer.getPlayer());
                        ItemStack mainItem = gamer.getPlayer().getInventory().getItemInMainHand();
                        if(mainItem.getAmount() == 1) {
                            gamer.getPlayer().getInventory().setItemInMainHand(null);
                        } else {
                            mainItem.setAmount(mainItem.getAmount() - 1);
                        }
                    })
                    .parameters(Arrays.asList( //параметры
                            ParameterManager.getNodropParameter(), //предмет не выпадает
                            ParameterManager.getOwnerParameter(), //предмет с владельцем
                            ParameterManager.getMinLevelParameter(getMinLevel()), //мин лвл для использования предмета
                            ParameterManager.getRareParameter(PrisonItem.Rare.VERY_RARE), //редкость предмета
                            ParameterManager.getCategoryParameter(PrisonItem.Category.ACCESS)))
                    .build(); //предмет можно улучшить
            ItemManager.addPrisonItem(prisonItem); //инициализация предмета
        }
        mines.add(this);
    }

    public boolean canTeleport(Gamer gamer, boolean sendMsg) {
        if (gamer.hasPermission("*")) return true;
        if(gamer.getLevel() < getMinLevel()) {
            if(sendMsg) {
                gamer.sendMessage(Utils.colored(EMessage.MINELEVEL.getMessage().replace("%level%", getMinLevel() + "")));
            }
            return false;
        }
        if(needPerm && gamer.getIntQuestValue(this.id + "Pass") != 1) {
            if(sendMsg) {
                gamer.sendMessage(EMessage.MINENEEDPERM);
            }
            return false;
        }
        return true;
    }

    public boolean canTeleport(Gamer gamer) {
        return canTeleport(gamer, false);
    }

    public static List<Mines> getMines() {
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
        return this.mineIcon.acessButton(gamer);
    }

    public Lore getLoreIcon(Gamer gamer) {
        return new MineIcon.Builder(this).build().lore(gamer);
    }

    public boolean hasBoss() {
        return boss != null;
    }

    public IMobController getBoss() {
        return boss;
    }
}
