package org.runaway.events;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/*
 * Created by _RunAway_ on 23.1.2019
 */

public class PlayerInventoryClick implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        try {
            if (event.getInventory().getName().equals(ChatColor.YELLOW + "Ваши достижения") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Вскопанные блоки") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Меню доната") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Выбор фракции") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Профиль") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Список шахт") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Тренер") ||
                    event.getInventory().getName().contains(ChatColor.YELLOW + "Выберите предмет прокачки") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Активные ускорители") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Активация ускорителей") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Аукцион") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Уведомления") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Перерождение") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Привилегии") ||
                    event.getInventory().getName().contains("Просмотр") ||
                    event.getInventory().getName().contains("Боевой пропуск") ||
                    event.getInventory().getName().contains("Вы уверены?") ||
                    event.getInventory().getName().contains("Работа") ||
                    event.getInventory().getName().contains("Задания шахты:") ||
                    event.getInventory().getName().contains("Магазин") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Продажа рыбы") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Повышение уровня") ||
                    event.getInventory().getName().equals(ChatColor.YELLOW + "Прокачка предмета")) {
                event.setCancelled(true);
            }
        } catch (Exception ignored) {  }
    }
}
