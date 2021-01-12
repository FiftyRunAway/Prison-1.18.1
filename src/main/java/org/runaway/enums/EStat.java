package org.runaway.enums;

import org.bukkit.Bukkit;
import org.runaway.Main;
import org.runaway.sqlite.DoReturn;
import org.runaway.sqlite.DoVoid;
import org.runaway.sqlite.PreparedRequests;
import org.runaway.utils.Utils;

import java.util.HashMap;

/*
 * Created by _RunAway_ on 16.1.2019
 */

public enum EStat {
    MODE("mode", "default", StatType.STRING, Utils.getMode()),
    LEVEL("level", 1, StatType.INTEGER, Utils.getLevel()),
    MONEY("balance", 0.0, StatType.DOUBLE, Utils.getMoney()),
    BLOCKS("blocks", 0.0, StatType.DOUBLE, Utils.getBlocks()),
    FACTION("faction", "default", StatType.STRING, Utils.getFactionMap()),
    KILLS("kills", 0, StatType.INTEGER, Utils.getKills()),
    KEYS("keys", 0, StatType.INTEGER, Utils.getKeys()),
    BOW_KILL("bow_kills", 0, StatType.INTEGER, Utils.getBow_kills()),
    DEATHES("deathes", 0, StatType.INTEGER, Utils.getDeathes()),
    RATS("rats", 0, StatType.INTEGER, Utils.getRats()),
    ZOMBIES("zombies", 0, StatType.INTEGER, Utils.getZombies()),
    DONATEMONEY("donatemoney", 0, StatType.INTEGER, Utils.getDonatemoney()),
    ZBT("zbt", false, StatType.BOOLEAN, Utils.getZbt()),
    AUTOSELLDONATE("autosell_donate", false, StatType.BOOLEAN, Utils.getAutoselldonate()),
    BOOSTERMONEY("boost_money", 1.0, StatType.DOUBLE, Utils.getBoostermoney()),
    BOOSTERBLOCKS("boost_blocks", 1.0, StatType.DOUBLE, Utils.getBoosterblocks()),
    PLAYEDTIME("played_time", 0, StatType.INTEGER, Utils.getPlayedtime()),
    BOSSES("bosses", 0, StatType.INTEGER, Utils.getBosses()),
    AUTOSELL("autosell", false, StatType.BOOLEAN, Utils.getAutosell()),
    REBIRTH("rebirth_level", 0, StatType.INTEGER, Utils.getRebirth()),
    HELPER("helper", 0, StatType.INTEGER, Utils.getHelper()),
    SCROLLS("scrolls", 0, StatType.INTEGER, Utils.getScrolls()),
    CASHBACK_TRAINER("trainer_cashback", 0, StatType.INTEGER, Utils.getCashback()),
    UPGRADE_TRAINER("trainer_upgrade", 0, StatType.INTEGER, Utils.getUpgrade()),
    LUCK_TRAINER("trainer_luck", 0, StatType.INTEGER, Utils.getLuck()),
    GYM_TRAINER("trainer_gym", 0, StatType.INTEGER, Utils.getGym()),
    REBIRTH_SCORE("rebirth_score", 0, StatType.INTEGER, Utils.getRebirthScores()),
    BATTLEPASS_SCORE("battlepass_score", 0, StatType.INTEGER, Utils.getBattlePassScores()),
    BATTLEPASS_LEVEL("battlepass_level", 0, StatType.INTEGER, Utils.getBattlePassLevel()),
    LOCATION_GLAD("locglad", false, StatType.BOOLEAN, Utils.getGladiator()),
    LOCATION_VAULT("locvault", false, StatType.BOOLEAN, Utils.getVault()),
    LOCATION_ICE("locice", false, StatType.BOOLEAN, Utils.getIce());

    private String title;
    private Object defualt;
    private StatType type;
    private HashMap map;

    EStat(String title, Object defualts, StatType type, HashMap map) {
        this.title = title;
        this.defualt = defualts;
        this.type = type;
        this.map = map;
    }

    public boolean containsInConfig(String player) {
        return EConfig.STATISTICS.getConfig().contains(player + "." + this.title);
    }

    public Object getFromConfig(String player) {
        if (Main.getInstance().getSaveType().equals(SaveType.SQLITE)) {
            return PreparedRequests.returnRequest(DoReturn.SELECT, Main.getMainDatabase(), player,
                    Main.getInstance().stat_table, this.title);
        }
        if (!containsInConfig(player)) {
            return defualt;
        }
        return EConfig.STATISTICS.getConfig().get(player + "." + this.title);
    }

    public void setInConfig(String player, Object value) {
        if (Main.getInstance().getSaveType().equals(SaveType.SQLITE)) {
            PreparedRequests.voidRequest(DoVoid.UPDATE, Main.getMainDatabase(), player,
                    Main.getInstance().stat_table, value, this.title);
            return;
        }
        EConfig.STATISTICS.getConfig().set(player + "." + this.title, value);
        EConfig.STATISTICS.saveConfig();
    }

    public void addConfig(String player, int value) {
        setInConfig(player, (int)getFromConfig(player) + value);
    }

    public void removeConfig(String player, int value) {
        setInConfig(player, (int)getFromConfig(player) - value);
    }

    public String getStatName() {
        return this.title;
    }

    public StatType getStatType() {
        return this.type;
    }

    public String getSQLiteType() {
        if (this.type.equals(StatType.STRING)) return "TEXT";
        return this.type.name();
    }

    public Object getDefualt() {
        return this.defualt;
    }

    public HashMap getMap() {
        return this.map;
    }
}
