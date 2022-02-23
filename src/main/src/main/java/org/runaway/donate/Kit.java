package org.runaway.donate;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.runaway.Gamer;
import org.runaway.enums.MoneyType;
import org.runaway.items.Item;
import org.runaway.items.ItemManager;
import org.runaway.menu.button.IMenuButton;
import org.runaway.rewards.IReward;
import org.runaway.rewards.MoneyReward;
import org.runaway.utils.ItemBuilder;
import org.runaway.utils.Items;
import org.runaway.utils.TimeUtils;
import org.runaway.utils.Utils;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class Kit {
    public List<IReward> rewards;
    public long cooldown;
    public Privs priv;

    public ItemStack getIcon(Gamer gamer, Privs privs) {
        if (privs.getKit() == null) return new ItemStack(Material.AIR);
        Privs gamerPriv = gamer.getPrivilege();
        return new ItemBuilder(privs.getMaterial())
                .name(getPriv().getGuiName())
                .addGlow(gamerPriv.equals(privs))
                .setLore(getLore(gamer, privs))
                .build();
    }

    public static List<String> getLore(Gamer gamer, Privs privs) {
        if (privs.getKit() == null) return new ArrayList<>();
        Kit kit = privs.getKit();
        List<String> lore = new ArrayList<>();
        lore.add(Utils.colored("&7Содержание"));
        for (IReward reward : kit.getRewards()) {
            if (reward instanceof MoneyReward moneyReward) {
                lore.add(Utils.colored("  &eДеньги &7• &b" + (moneyReward.getAmount() * gamer.getLevel()) + " " + MoneyType.RUBLES.getShortName() + " &7[зависит от уровня]"));
            } else {
                lore.add(Utils.colored("  &eПредмет &7• &b" + reward.getName()));
            }
        }
        lore.add(Utils.colored("&r"));
        Privs gamerPriv = gamer.getPrivilege();
        if (gamerPriv.equals(privs)) {
            if (gamer.getKitLastDate() != null &&
                    gamer.getKitLastDate().getTime() + (kit.getCooldown() * 1000) >= System.currentTimeMillis()) {
                lore.add(Utils.colored("&cПодождите " + TimeUtils.getDuration((gamer.getKitLastDate().getTime() + (kit.getCooldown() * 1000) - System.currentTimeMillis()) / 1000)));
            } else {
                lore.add(Utils.colored("&aНажмите, чтобы получить!"));
            }
        } else {
            lore.add(Utils.colored("&cЭтот набор вам недоступен!"));
        }
        return lore;
    }

    public static boolean canHave(Gamer gamer, Kit kit) {
        Kit gamerPriv = gamer.getPrivilege().getKit();
        if (gamerPriv == null) return false;
        if (gamer.getPrivilege().equals(kit.getPriv())) {
            if (gamer.getKitLastDate() == null) return true;
            return gamer.getKitLastDate().getTime() + (kit.getCooldown() * 1000) < System.currentTimeMillis();
        }
        return false;
    }
}
