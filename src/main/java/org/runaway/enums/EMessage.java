package org.runaway.enums;

import org.runaway.utils.Utils;

import java.util.HashMap;

/*
 * Created by _RunAway_ on 4.2.2019
 */

public enum  EMessage {
    UNAVALIABLEMESSAGE("UnavaliableMessage", "&cЭто сообщение пока что недоступно, обратитесь к администрации!"),
    FRACTIONLEVEL("FractionLevel", "&cВыбор фракции доступен с 5-го уровня"),
    FRACTIONALREADY("FractionAlready", "&cВы уже сделали свой выбор"),
    AUTOSELLENABLE("AutosellEnable", "&aВы включили авто-продажу"),
    AUTOSELLDISABLE("AutosellDisable", "&cВы отключили авто-продажу"),
    BUYDONATE("DonateBuy", "&cКупить эту возможность можно в /donate"),
    MAXLEVEL("MaximumLevel", "&cВы уже достигли максимального уровня"),
    FOUNDKEY("FoundKey", "&aВы нашли ключ"),
    NOTADDEDBLOCK("NotAddedBlock", "&cЭтот блок не добавлен в магазин, напишите администрации об этой ошибке!"),
    DIEDPLAYER("PlayerDied", "&cВы умерли и потеряли %money%"),
    KILLPLAYER("PlayerKill", "&cВы убили %player% и получили %money%"),
    TREASUREOPEN("OpenTreasure", "&aВ этом тайнике вы нашли %reward%"),
    NOBLOCKSFORSALE("NoBlocksForSale", "&cУ вас нет предметов для продажи!"),
    ACTIONBARSELL("ActionBarSell", "&aВы продали &e%amount% &aпредметов на &e%money% руб! &7[&e%booster%x&7]"),
    BOOSTERALREADYACTIVE("BoosterAlreadyActive", "&cЭтот вид бустер уже активен!"),
    NOBOOSTERS("NoBoosters", "&cУ вас нет ни одного бустера"),
    LEVELNEEDBLOCKS("LevelBlocksNeed", "&cПодкопай-ка ещё немного блоков и попробуй снова"),
    MONEYNEEDS("NeedMoney", "&cУ вас недостаточно денег"),
    FIRSTJOINPLAYER("PlayerFirstJoin", "&7[&a+&7] &fИгрок &e%player% &fпопал к нам в тюрьму!"),
    SELECTMODE("OnSelectMode", "&fВы выбрали %mode% &fуровень сложности!"),
    NOLOREUPGRADE("NoLoreOnUpgrade", "&cТо, что у Вас в руке - не поддаётся никаким улучшениям"),
    MAXLEVELUPGRADE("MaxLevelUpgrade", "&cЭтот предмет улучшен по максимуму!"),
    NOTENOUGHPROPERTY("NoProperty", "&cУ вас маловато &4%property%&c!"),
    SUCCESSFULUPGRADE("SuccessfulyUpgrade", "&aВы успешно улучшили предмет в руке"),
    BREAKBYTOOLS("BreakByTools", "&cВы не можете ломать блоки &4рукой&c, возьмите &4инструмент &cв руки!"),
    ALREADYTP("AlreadyTeleporting", "&cВы находитесь в ожидании телепортации"),
    MINELEVEL("MinesLevel", "&cЭта шахта доступна только с &4%level% &cуровня"),
    MINENEEDPERM("MineNeedPerm", "&cДля доступа к этой шахте необходим &4специальный &cдоступ"),
    FRACTIONSUCCESS("FractionSucces", "&aВы успешно вступили во фракцию %fraction%&a. Наши поздравления!"),
    SUCCESSFULYLEAVE("FractionLeave", "&aВы покинули фракцию"),
    NOACTIVEBOOSTERS("NoActiveBoosters", "&cНет активных бустеров или вы уже поблагодарили владельца"),
    NOFACTION("NoFaction", "&cВы ещё не вступили во фракцию!"),
    NOPERM("NoPermissions", "&cУ вас нет доступа к этой функции..."),
    BROADCAST_REBITH("ReBirth", "&aИгрок %player% увеличил свой уровень престижа до %rebirth%"),
    GIFTYOU("GiftYou", "&fИгрок %player% хочет сделать вам подарок. Если хотите принять - напишите /gift"),
    NOPLAYER("NoPlayer", "&cЭтого игрока, к сожалению, нет на нашем сервере в данный момент :("),
    GIFTLEFTTIME("GiftLeftTime", "&cВы не успели принять подарок от %player%"),
    HANDSLEFT("HandsLeft", "&cПоложите уже, наконец-то, что-нибудь в руку)"),
    SENDERALREADYGIFT("SenderAlreadyGifted", "&cВы не можете отправлять несколько подарков сразу"),
    CONSUMERALREADYGIFT("ConsumerAlreadyGifted", "&cЭтот игрок занят, попробуйте примерно через %time% секунд!"),
    CANCELGIFT("GiftCanceled", "&cВы отказались получать подарок от %player%!"),
    ACCEPTGIFT("GiftAccepted", "&aВы приняли подарок от %player%!"),
    ACCEPTEDGIFT("GiftWasAccepted", "&aВаш подарок успешно приняли"),
    CANCELEDGIFT("GiftWasCanceled", "&cВаш подарок был отклонён("),
    TIMELEFT("TimeLeft", "&cВремя вышло... Вы не успели("),
    TIMELEFTOWNER("TimeLeftForOwner", "&cВаш подарок не успели принять, ну или просто не хотят)"),
    RETURNITEM("GiftReturnItem", "&cВаш предмет вернулся к вам в инвентарь!"),
    SELFGIFT("GiftSelf", "&cВы не сможете отправить подарок самому себе)"),
    MINERESET("MineReset", "&eШахта обновилась!"),
    SUCCESSFULBUY("SuccessfulyBought", "&aУспешная покупка!"),
    MINLEVELITEM("ItemMinLevel", "&cМинимальный уровень для использования данного предмета - &4%level%"),
    DISFUNCTION("DisabledFunction", "&cЭта функция отключена на данный момент"),
    DELETECHEST("ChestDelete", "&cВаш предыдущий клад был удалён, так как вы нашли новый)"),
    TIMELEFTCHEST("ChestTimeLeft", "&cВы не успели забрать клад за %time% секунд, его украли"),
    CASEOPENSHIFT("CaseOpenShift", "&eВстаньте с shift, чтобы открыть сундук!"),
    KEYOPENCASE("CaseOpenKey", "&cВозьмите ключ в руку, чтобы открыть этот сундук)"),
    BOSSREWARD("BossReward", "&fЗа убийство босса &e%boss% &fвы получаете &a%money%"),
    SPIDERDEAD("SpiderDead", "&cМатка была повержена окончательно, последний удар нанёс %player%"),
    BLAZEDEAD("BlazeDead", "&cОгненный страж был свержен наповал! Последний удар нанёс %player%"),
    GOLEMDEAD("GolemDead", "&cГолем мёртв! Последний удар нанёс %player%"),
    SLIMEDEAD("SlimeDead", "&cДревний слизняк погиб в ожесточённом бою! Последний удар нанёс %player%"),
    TRAINERNEED("RequireTrainer", "&cУ вас выполнены не все требования"),
    TRAINERSUCCESS("SuccessTrainer", "&aВы успешно прокачали один из навыков!"),
    CASHBACK("TrainerCashback", "&eПоздравляю! Вы получили кэшбэк в размере %cashback% с суммы %money%"),
    TRAINERUPGRADE("TrainerUpgrade", "&eПоздравляю! Вы совершили двойной апгрейд, это было почти нереально)"),
    TRAINERUPGRADEDUPE("TrainerUpgradeDupe", "&cПоздравляю! Вы совершили двойной апгрейд... Но подождите-ка, кажется вы пытались совершить противоправные действия. Мы вынуждены отказать вам в этой радости ("),
    FLEAVECONFIRM("FleaveConfirmation", "&eСтоимость выхода из фракции - &a%money% &7%discount%" +
            "\n&eВведите команду ещё раз для подтверждения операции."),
    ERRORCONFIRMATION("ConfirmationError", "&cВы не успели подтвердить операцию..."),
    TRAINERFULL("FullUpgradeTrainer", "&eВы уже полностью прокачали это умение!"),
    NOSCROLLS("NoScrollsToBuy", "&cНедостаточно свитков для покупки"),
    NOINVENTORY("FullInventory", "&cВаш инвентарь переполнен, продайте блоки!"),
    BROCKENBLOCKS("NoBrockenBlocks", "&cВы, кажется, не вскопали ещё ни одного блока... Пора бы заняться этим!"),
    AUCTIONSTARTING("AuctionStart", "&aНачался аукцион на свалке, скорее беги туда, ты можешь приобрести что-то хорошее совсем недорого! &e/trash"),
    AUCTIONWIN("WinAuction", "&aПоздравляем! Вы выйграли предмет на аукционе и оплатили его уже. Поучаствуйте ещё)"),
    AUCTIONCLOSE("CloseAuction", "&fАукцион закрывается через &eминуту. &fМожете уходить &e/spawn"),
    NOAUCTION("NoAuctions", "&cВсе аукционы завершены! Приходите попозже. Подробнее о времени проведения &4/profile"),
    FULLINVAUCTION("FullInventoryAuction", "&cПрежде, чем участвовать в аукционе - освободите инвентарь!"),
    AUCTIONSELF("SelfAuction", "&cВам не надо перебивать свою ставку)"),
    WAIT("Wait", "&cНе так быстро! Подождите немного..."),
    FRIENDATTACK("FriendNoAttack", "&cНе стоит бить союзников по фрацкции!"),
    NOMESSAGE("NoMessage", "&cВведите само сообщение..."),
    AMPERSAND("NoAmpersand", "&cНе используйте таких символов!"),
    VALUEBAD("BadValue", "&cНеправильное значение..."),
    SENDMONEY("SendMoney", "&aВы отправили деньги."),
    GETMONEY("GetMoney", "&aВы получили %money% от %player%"),
    MANYAUCTION("ManyAuctions", "&cВы можете участвовать только в одном аукционе!"),
    VALUEINT("IntegerVault", "&cТолько целое число"),
    UPGRADEATTENTION("UpgradeAttention", "&cВнимание! Обращайте внимание на то, что у предметов есть &4минимальный уровень&c. Стоит проверить с какого уровня будет ваш следующий предмет! Для подтверждения введите команду &4/upgrade"),
    SENDGIFT("GiftSend", "&aВы успешно отправили подарок!"),
    ALREADYHAVE("AlreadyHave", "&cУ вас уже есть эта локация..."),
    ACTIVATELOCATION("LocationActivate", "&aВы успешно активировали новую &eлокацию"),
    TIP("TipSuccess", "&aВы успешно поблагодарили всех &eвладельцев"),
    ALREADYBOUGHT("AlreadyBought", "&cЭтот аукцион уже выкуплен("),
    ERRORELEMENT("ErrorOfElement", "&cС этим элементом возникла проблема. Пожалуйста, сообщите об этом &4администрации &cсервера!"),
    SECONDLEVEL("SecondLevel", "&cПриходите сюда на втором уровне, а пока что надо копать &4дерево"),
    ONEAUCTION("OneOnAuction", "&cНам не выгодно с вами играть, ведь на сервере совсем пусто..."),
    AUCTIONTIMES("TimesAuction", "&fАукцион проходит в &e%time%"),
    BPMISSION("MissionComplete", "&aВы выполнили миссию &e%name%. &aПродолжайте в том же духе!"),
    BPREWARDGET("RewardGet", "&aВы получили следующие награды за повышение уровня боевого пропуска: %reward%"),
    BPREWARDCAN("RewardCanGet", "&aВы бы могли получить эти награды, имея боевой пропуск: %reward%"),
    REBIRTHBOUGHT("BoughtRebirth", "&aВы приобрели новый навык за очки перерождения. Поздравляем!"),
    BOSSNOTIFY("BossNotify", "&bПоявился босс &c%name%&b. Скорее на него!");

    private String config, defaults;
    public static HashMap<String, String> messages = new HashMap<>();

    EMessage(String config, String defaults) {
        this.config = config;
        this.defaults = defaults;
    }

    public String getConfigName() {
        return config;
    }

    public String getDefaultMessage() { return defaults; }

    public String getMessage() {
        return Utils.colored(messages.get(getConfigName()));
    }

    public String getMessageConfig() {
        return EConfig.MESSAGES.getConfig().getString("messages." + config);
    }
}
