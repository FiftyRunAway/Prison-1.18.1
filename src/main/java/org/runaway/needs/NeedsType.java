package org.runaway.needs;

import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public enum NeedsType {

    WASH, TOILET, SLEEP;

    public static ArrayList<String> getProperties(NeedsType type) {
        ArrayList<String> list = new ArrayList<>();
        switch (type) {
            case WASH:
                list.add("Душ");
                list.add("Срочно идите в душ!%Вы воянете");
                list.add("&eВы сильно завонялись! \n&cСрочно помойтесь.");
                break;
            case TOILET:
                list.add("Туалет");
                list.add("Срочно сходите в туалет!%Вы уже не можете держать");
                list.add("&aУже сильно давит на клапан! \n&cБегите в туалет.");
                break;
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
            case TOILET:
                return PotionEffectType.SLOW;
            case SLEEP:
                return PotionEffectType.SLOW_DIGGING;
        }
        return null;
    }

    public static int getType(NeedsType type) {
        switch (type) {
            case WASH:
                return 1;
            case TOILET:
                return 2;
            case SLEEP:
                return 3;
        }
        return 0;
    }
}
