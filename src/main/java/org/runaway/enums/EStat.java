package org.runaway.enums;

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
    DONATEMONEY("donate.money", 0, StatType.INTEGER, Utils.getDonatemoney()),
    ZBT("donate.zbt", false, StatType.BOOLEAN, Utils.getZbt()),
    AUTOSELLDONATE("donate.autosell", false, StatType.BOOLEAN, Utils.getAutoselldonate()),
    BOOSTERMONEY("donate.boost.money", 1.0, StatType.DOUBLE, Utils.getBoostermoney()),
    BOOSTERBLOCKS("donate.boost.blocks", 1.0, StatType.DOUBLE, Utils.getBoosterblocks()),
    PLAYEDTIME("played-time", 0, StatType.INTEGER, Utils.getPlayedtime()),
    BOSSES("bosses", 0, StatType.INTEGER, Utils.getBosses()),
    AUTOSELL("autosell", false, StatType.BOOLEAN, Utils.getAutosell()),
    REBIRTH("rebirth.level", 0, StatType.INTEGER, Utils.getRebirth()),
    HELPER("helper", 0, StatType.INTEGER, Utils.getHelper()),
    SCROLLS("scrolls", 0, StatType.INTEGER, Utils.getScrolls()),
    CASHBACK_TRAINER("trainer.cashback", 0, StatType.INTEGER, Utils.getCashback()),
    UPGRADE_TRAINER("trainer.upgrade", 0, StatType.INTEGER, Utils.getUpgrade()),
    LUCK_TRAINER("trainer.luck", 0, StatType.INTEGER, Utils.getLuck()),
    GYM_TRAINER("trainer.gym", 0, StatType.INTEGER, Utils.getGym()),
    DAILYSTREAK("quests.daily", "default", StatType.STRING, Utils.getDailyQuests()),
    DAILYSTART("quests.dailystart", 0, StatType.INTEGER, Utils.getDailyStart()),
    TWOFA_CODE("auth.code", "default", StatType.STRING, Utils.getAuthCode()),
    REBIRTH_SCORE("rebirth.score", 0, StatType.INTEGER, Utils.getRebirthScores()),
    BATTLEPASS_SCORE("battlepass.score", 0, StatType.INTEGER, Utils.getBattlePassScores()),
    BATTLEPASS_LEVEL("battlepass.level", 0, StatType.INTEGER, Utils.getBattlePassLevel());

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
        if (!containsInConfig(player)) {
            return defualt;
        }
        return EConfig.STATISTICS.getConfig().get(player + "." + this.title);
    }

    public void setInConfig(String player, Object value) {
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

    public Object getDefualt() {
        return this.defualt;
    }

    public HashMap getMap() {
        return this.map;
    }
}
