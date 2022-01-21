package org.runaway.battlepass.rewards;

import org.bukkit.Material;
import org.runaway.Gamer;
import org.runaway.battlepass.IReward;
import org.runaway.enums.BoosterType;
import org.runaway.utils.Utils;

public class BoosterReward extends IReward {

    private String value;
    private String name;

    private Material type;

    private BoosterType bt;
    private double mult;
    private long time;
    private boolean global;

    @Override
    protected void init() {
        this.value = this.getStringValue(0);
        this.setStringValue(true);
        this.setValue(this.value);

        String[] spl = this.value.split("-");
        bt = BoosterType.valueOf(spl[0].toUpperCase());
        mult = Double.parseDouble(spl[1]);
        time = Long.parseLong(spl[2]);
        global = Boolean.parseBoolean(spl[3]);

        //set name
        this.name = "&a" +
                (global ? "Глобальный" : "Локальный") +
                " ускоритель " + bt.getName() +
                " " + mult + "x " +
                "на " + Utils.formatTime(time);

        //set type
        this.type = bt.equals(BoosterType.MONEY) ?
                (global ? Material.DIAMOND_BLOCK : Material.DIAMOND) :
                (global ? Material.GOLD_BLOCK : Material.GOLD_INGOT);
    }

    @Override
    protected void getReward(Gamer gamer) {
        gamer.addBooster(bt, mult, time, global);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    protected String getDescription() {
        return "Ускорит ваш процесс развития";
    }

    @Override
    protected Material getType() {
        return this.type;
    }

    @Override
    public String getArgumentsString() {
        return "type-multiplier-time_sec-isGlobal";
    }
}
