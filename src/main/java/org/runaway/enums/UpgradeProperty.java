package org.runaway.enums;

/*
 * Created by _RunAway_ on 4.5.2019
 */

public enum UpgradeProperty {

    RATS("Крысы", false, "крыс"),
    COST("Цена", true, "денег"),
    KILLS("Убийства", false, "убийств"),
    SHOVEL_BLOCKS("Сыпучие блоки", false, "сыпучих блоков"),
    WOOD("Дерево", false, "дерева"),
    MINE_BLOCKS("Блоки в шахте", false, "выкопанных в шахте блоков"),
    BLOCKS("Блоки", false, "блоков"),
    LEVEL("Уровень", false, "уровень"),
    STONE("Камень", false, "камня"),
    NETHERRACK("Адский камень", false, "адского камня"),
    IRON_ORE("Железная руда", false, "железной руды"),
    COAL_ORE("Угольная руда", false, "угольной руды"),
    GOLD_ORE("Золотая руда", false, "золотой руды"),
    WEB("Паутина", false, "паутины"),
    BOW_KILL("Убийства из лука", false, "убийств из лука"),
    DIRT("Земля", false, "земли"),
    SAND("Песок", false, "песка"),
    GRAVEL("Гравий", false, "гравия"),
    WOOL("Шерсть", false, "шерсти");

    private String name;
    private boolean isTaken;
    private String forMessage;

    UpgradeProperty(String name, boolean isTaken, String forMessage) {
        this.name = name;
        this.isTaken = isTaken;
        this.forMessage = forMessage;
    }

    public String getName() {
        return this.name;
    }

    public boolean isTaken() {
        return this.isTaken;
    }

    public String getForMessage() {
        return this.forMessage;
    }
}
