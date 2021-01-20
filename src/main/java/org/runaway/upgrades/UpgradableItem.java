package org.runaway.upgrades;

import lombok.Builder;
import lombok.Getter;
import org.runaway.items.PrisonItem;
import org.runaway.requirements.RequireList;

@Builder @Getter
public class UpgradableItem {
    PrisonItem prisonItem1;
    PrisonItem prisonItem2;
    RequireList requireList;


}
