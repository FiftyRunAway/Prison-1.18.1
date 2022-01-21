package org.runaway.configs;

import org.runaway.battlepass.BattlePass;
import org.runaway.battlepass.IMission;
import org.runaway.battlepass.IReward;
import org.runaway.battlepass.missions.EMissions;
import org.runaway.battlepass.rewards.ERewards;
import org.runaway.enums.ServerStatus;
import org.runaway.items.PrisonItem;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ConfigHeaders {

    public static String configHeader() {
        return "Server status: " + Arrays.toString(ServerStatus.values()).replace("[", "").replace("]", "") +
                "\n----------------------------------------------------------" +
                "\nLocations:" +
                "\n- Forest has format 'x1 y1 z1 x2 y2 z2 WORLD'" +
                "\n- Case has format 'x y z WORLD'" +
                "\n- Spawn has format 'x y z WORLD'";
    }

    public static String bpHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("BattlePass Missions:");
        sb.append("\n--------------------------------");
        Arrays.stream(EMissions.values()).forEach(mission -> {
            try {
                IMission c = mission.getMissionClass().getDeclaredConstructor().newInstance();
                sb.append("\n- ").append(c.getClass().getSimpleName().toLowerCase()).append(":\"").append(c.getArgumentsString()).append(" name\"");
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        sb.append("\n=================================");
        sb.append("\nmine_name you can take from Config.yml mines-material");
        sb.append("\n=================================");

        sb.append("\nBattlePass Rewards: \n- free|paid:");
        sb.append("\n--------------------------------");
        Arrays.stream(ERewards.values()).forEach(reward -> {
            try {
                IReward r = reward.getRewardClass().getDeclaredConstructor().newInstance();
                sb.append("\n- ").append(reward.toString().toLowerCase()).append(" \"").append(r.getArgumentsString()).append("\"");
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        sb.append("\n=================================");
        sb.append("\nblock_pattern has appearance: block-sub-id#name");
        sb.append("\n=================================");
        sb.append("\nDate format: " + BattlePass.data_format);
        sb.append("\n=================================");

        return sb.toString();
    }

    public static String itemsHeader() {
        StringBuilder sb = new StringBuilder("Categories: ");
        Arrays.stream(PrisonItem.Category.values()).forEach(category -> sb.append(category.name().toLowerCase()).append(" "));
        sb.append("\nRarity: ");
        Arrays.stream(PrisonItem.Rare.values()).forEach(rare -> sb.append(rare.name()).append(" "));
        return sb.toString();
    }
}
