package org.runaway;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.*;

/*
 * Created by _RunAway_ on 5.5.2019
 */

public class TopPlayers {

    private Location location;
    private HashMap<String, Long> topValues;
    private String start;
    private Hologram hologram;
    private int max;
    private String description;

    TopPlayers(Location location, HashMap<String, Long> values, String start, int max, String description) {
        this.location = location;
        this.topValues = values;
        this.start = start;
        this.max = max;
        this.description = description;
        this.create();
    }

    void setTopValues(HashMap<String, Long> map) {
        this.topValues = map;
    }

    void recreate() {
        this.hologram.delete();
        this.create();
    }

    public void create() {
        Hologram hologram = HologramsAPI.createHologram(Main.getInstance(), this.location);
        if (hologram == null) {
            System.out.println("hologram is null. but Why?...");
            return;
        }
        hologram.insertTextLine(0, ChatColor.translateAlternateColorCodes('&', this.start));
        hologram.insertTextLine(1, "   ");
        Map<String, Long> values = sortByValue(this.topValues);
        int h;
        int h2 = this.max - 1;
        int h3 = values.size() - 1;
        if (values.size() < this.max) {
            h = h3;
        } else {
            h = h2;
        }
        for (int f = h; f >= 0; --f) {
            try {
                hologram.appendTextLine(ChatColor.translateAlternateColorCodes('&',  values.keySet().toArray()[h3 - h + f] + " &7&l• &a" + values.get(values.keySet().toArray()[h3 - h + f])));
            }
            catch (Exception e) { e.printStackTrace(); }
        }
        this.hologram = hologram;
    }

    public HashMap<String, Long> getTopValues() {
        Main.forceUpdateTop();

        return (HashMap<String, Long>) sortByValue(this.topValues);
    }

    private static <K, V> HashMap<K, V> invert(Map<K, V> map) {
        HashMap<K, V> inv = new HashMap<>();

        ArrayList<K> keys = new ArrayList<>();
        ArrayList<V> values = new ArrayList<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            keys.add(entry.getKey());
            values.add(entry.getValue());
        }
        for (int i = keys.size(); i > 0; i--) {
            int pos = keys.size() - i;
            inv.put(keys.get(pos), values.get(pos));
        }
        Bukkit.getConsoleSender().sendMessage(inv.toString());
        return inv;
    }

    private static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        LinkedList<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        list.sort(Comparator.comparing(Map.Entry::getValue));
        LinkedHashMap<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            if (!entry.getKey().equals("_RunAway_") &&
                    !entry.getKey().equals("AttempGame")) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public String getDescription() {
        return description;
    }
}
