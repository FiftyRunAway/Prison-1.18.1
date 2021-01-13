package org.runaway.enums;

import org.runaway.Main;
import org.runaway.sqlite.DoReturn;
import org.runaway.sqlite.DoVoid;

import java.util.HashMap;

/*
 * Created by _RunAway_ on 16.1.2019
 */

public enum EStat implements Saveable {
    MODE("mode", "default", StatType.STRING),
    UUID("uuid", "", StatType.STRING),
    FULL_NAME("full_name", "", StatType.STRING),
    LEVEL("level", 1, StatType.INTEGER),
    STREAMS("streams", 0.0, StatType.DOUBLE),
    MONEY("balance", 0.0, StatType.DOUBLE),
    BLOCKS("blocks", 0.0, StatType.DOUBLE),
    MOB_KILLS("mob_kills", "", StatType.STRING),
    BLOCKS_AMOUNT("blocks_amount", "", StatType.STRING),
    PERKS("perks", "", StatType.STRING),
    BOOSTERS("boosters", "", StatType.STRING),
    OFFLINE_VALUES("offline_values", "", StatType.STRING),
    FACTION("faction", "default", StatType.STRING),
    KILLS("kills", 0, StatType.INTEGER),
    KEYS("keys", 0, StatType.INTEGER),
    BOW_KILL("bow_kills", 0, StatType.INTEGER),
    DEATHES("deathes", 0, StatType.INTEGER),
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
    LOCATIONS("locations", "", StatType.STRING);

    private String title;
    private Object defualt;
    private StatType type;

    EStat(String title, Object defualts, StatType type) {
        this.title = title;
        this.defualt = defualts;
        this.type = type;
    }

    public Object getFromConfig(String player) {
        return Main.getInstance().getPreparedRequests().returnRequest(DoReturn.SELECT, player, this.title);
    }

    public void setInConfig(String player, Object value) {
        if (Main.getInstance().getSaveType().equals(SaveType.SQLITE)) {
            Main.getInstance().getPreparedRequests().voidRequest(DoVoid.UPDATE, player, this.title, value);
            return;
        }
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
