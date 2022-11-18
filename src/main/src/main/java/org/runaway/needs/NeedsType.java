package org.runaway.needs;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import org.runaway.donate.features.IFeature;
import org.runaway.donate.features.NeedSleep;
import org.runaway.donate.features.NeedWash;

import java.util.ArrayList;

public enum NeedsType {

    WASH,
    //TOILED,
    SLEEP;

    public static IFeature getFeature(NeedsType type) {
        switch (type) {
            case WASH -> {
                return new NeedWash();
            }
            case SLEEP -> {
                return new NeedSleep();
            }
            default -> {
                return null;
            }
        }
    }

    public static int getCooldown(NeedsType type) {
        switch (type) {
            case WASH -> {
                return 15;
            }
            case SLEEP -> {
                return 60;
            }
            default -> {
                return 0;
            }
        }
    }

    public static String getHud(NeedsType type) {
        switch (type) {
            case WASH -> {
                return "realcraft:thirst_bar";
            }
            case SLEEP -> {
                return "magiccraft:mana_bar";
            }
            default -> {
                return null;
            }
        }
    }

    public static int getHudMaxAmount(NeedsType type) {
        switch (type) {
            case WASH -> {
                return 10;
            }
            case SLEEP -> {
                return 6;
            }
            default -> {
                return 0;
            }
        }
    }

    public static ArrayList<String> getProperties(NeedsType type) {
        ArrayList<String> list = new ArrayList<>();
        switch (type) {
            case WASH:
                list.add("Душ");
                list.add("Срочно идите в душ!%/spawn и назад");
                list.add("&cСрочно помойтесь.");
                break;
            /*case TOILET:
                list.add("Туалет");
                list.add("Срочно сходите в туалет!%/spawn и налево");
                list.add("&cБегите в туалет.");
                break; */
            case SLEEP:
                list.add("Сон");
                list.add("Закрываются глаза!%Вы хотите спать");
                list.add("&aВы уже заработались! \n&cПора бы и поспать.");
                break;
            default:
                list.add("null");
        }
        return list;
    }

    public static PotionEffectType getEffect(NeedsType type) {
        switch (type) {
            case WASH:
                return PotionEffectType.CONFUSION;
            /*case TOILET:
                return PotionEffectType.SLOW; */
            case SLEEP:
                return PotionEffectType.SLOW_DIGGING;
        }
        return null;
    }

    public static int getType(NeedsType type) {
        switch (type) {
            case WASH:
                return 1;
            /*case TOILET:
                return 2; */
            case SLEEP:
                return 3;
        }
        return 0;
    }
}
