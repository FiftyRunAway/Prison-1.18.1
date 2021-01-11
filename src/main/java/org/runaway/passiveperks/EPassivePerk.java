package org.runaway.passiveperks;

import org.runaway.passiveperks.perks.*;

public enum EPassivePerk {
    VACCINE(new Vaccine()),
    BBLOCKFIRST(new BBlocksFirst()),
    BBLOCKSSECOND(new BBlocksSecond()),
    BBMONEYSECOND(new BBMoneySecond()),
    BMONEYFIRST(new BMoneyFirst()),
    FIREPROOF(new Fireproof()),
    JUMPER(new Jumper()),
    KEYFIRST(new KeyFirst()),
    KEYSECOND(new KeySecond()),
    SPEED(new Speed()),
    NIGHTVISION(new NightVision()),
    KILLER(new Killer());

    private PassivePerks perk;

    EPassivePerk(PassivePerks c) {
        this.perk = c;
    }

    public PassivePerks getPerk() {
        return perk;
    }
}
