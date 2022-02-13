package org.runaway.donate;

import lombok.Getter;
import org.runaway.Prison;
import org.runaway.enums.SaveType;
import org.runaway.enums.Saveable;
import org.runaway.enums.StatType;
import org.runaway.sqlite.DoReturn;
import org.runaway.sqlite.DoVoid;

import java.util.Locale;

public enum DonateStat implements Saveable {
    UUID("uuid", "", StatType.STRING),
    FULL_NAME("full_name", "", StatType.STRING),

    BALANCE("balance", 0, StatType.INTEGER),
    TOTAL_DONATED("totalDonated", 0, StatType.INTEGER),
    SPENT("spent", 0, StatType.INTEGER),
    BUYINGS("amountPurchases", 0, StatType.INTEGER),
    BPOINT("bpPoints", 0, StatType.INTEGER),

    OFFLINE_VALUES("offline_values", "", StatType.STRING);

    @Getter
    private String title;
    @Getter
    private Object defualt;
    @Getter
    private StatType type;

    DonateStat(String title, Object defualt, StatType type) {
        this.title = title;
        this.defualt = defualt;
        this.type = type;
    }

    public Object getFromFile(String player) {
        return Prison.getInstance().getPreparedRequests().returnRequest(DoReturn.SELECT, player.toLowerCase(Locale.ROOT), this.title);
    }

    public void setInFile(String player, Object value) {
        if (Prison.getInstance().getSaveType().equals(SaveType.SQLITE)) {
            Prison.getInstance().getPreparedRequests().voidRequest(DoVoid.UPDATE, player.toLowerCase(Locale.ROOT), this.title, value);
        }
    }

    public String getSQLiteType() {
        if (this.type.equals(StatType.STRING)) return "TEXT";
        return this.type.name();
    }

    @Override
    public Object getDefaultValue() {
        return getDefualt();
    }

    @Override
    public String getColumnName() {
        return getTitle();
    }

    @Override
    public String getColumnType() {
        return getSQLiteType();
    }
}
