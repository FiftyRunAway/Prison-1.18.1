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

public enum EStat implements Saveable {
    MODE("mode", "default", StatType.STRING),
    UUID("uuid", "", StatType.STRING),
    FULL_NAME("full_name", "", StatType.STRING),
    LEVEL("level", 1, StatType.INTEGER),
    STREAMS("streams", 0, StatType.DOUBLE),
    MONEY("balance", 0.0, StatType.DOUBLE),
    BLOCKS("blocks", 0.0, StatType.DOUBLE),
    FACTION("faction", "default", StatType.STRING),
    KILLS("kills", 0, StatType.INTEGER),
    KEYS("keys", 0, StatType.INTEGER),
    BOW_KILL("bow_kills", 0, StatType.INTEGER),
    DEATHES("deathes", 0, StatType.INTEGER),
    RATS("rats", 0, StatType.INTEGER),
    ZOMBIES("zombies", 0, StatType.INTEGER),
    DONATEMONEY("donatemoney", 0, StatType.INTEGER),
    ZBT("zbt", false, StatType.BOOLEAN),
    AUTOSELLDONATE("autosell_donate", false, StatType.BOOLEAN),
    BOOSTERMONEY("boost_money", 1.0, StatType.DOUBLE),
    BOOSTERBLOCKS("boost_blocks", 1.0, StatType.DOUBLE),
    PLAYEDTIME("played_time", 0, StatType.INTEGER),
    BOSSES("bosses", 0, StatType.INTEGER),
    AUTOSELL("autosell", false, StatType.BOOLEAN),
    REBIRTH("rebirth_level", 0, StatType.INTEGER),
    HELPER("helper", 0, StatType.INTEGER),
    SCROLLS("scrolls", 0, StatType.INTEGER),
    CASHBACK_TRAINER("trainer_cashback", 0, StatType.INTEGER),
    UPGRADE_TRAINER("trainer_upgrade", 0, StatType.INTEGER),
    LUCK_TRAINER("trainer_luck", 0, StatType.INTEGER),
    GYM_TRAINER("trainer_gym", 0, StatType.INTEGER),
    REBIRTH_SCORE("rebirth_score", 0, StatType.INTEGER),
    BATTLEPASS_SCORE("battlepass_score", 0, StatType.INTEGER),
    BATTLEPASS_LEVEL("battlepass_level", 0, StatType.INTEGER),
    LOCATION_GLAD("locglad", false, StatType.BOOLEAN),
    LOCATION_VAULT("locvault", false, StatType.BOOLEAN),
    LOCATION_ICE("locice", false, StatType.BOOLEAN);

    private String title;
    private Object defualt;
    private StatType type;

    EStat(String title, Object defualts, StatType type) {
        this.title = title;
        this.defualt = defualts;
        this.type = type;
    }

    public boolean containsInConfig(String player) {
        return EConfig.STATISTICS.getConfig().contains(player + "." + this.title);
    }

    public Object getFromConfig(String player) {
        return Main.getInstance().getPreparedRequests().returnRequest(DoReturn.SELECT, player, this.title);
    }

    public void setInConfig(String player, Object value) {
        if (Main.getInstance().getSaveType().equals(SaveType.SQLITE)) {
            Main.getInstance().getPreparedRequests().voidRequest(DoVoid.UPDATE, player, this.title, value);
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

    @Override
    public Object getDefaultValue() {
        return getDefualt();
    }

    @Override
    public String getColumnName() {
        return getStatName();
    }
}
