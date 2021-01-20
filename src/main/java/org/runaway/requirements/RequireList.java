package org.runaway.requirements;

import lombok.Getter;
import org.runaway.Gamer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class RequireList {
    List<Require> requireList;


    public RequireList() {
        this.requireList = new ArrayList();
    }

    public RequireList(Require... requires) {
        this.requireList = Arrays.stream(requires).collect(Collectors.toList());
    }

    public RequireList(List<Require> requireList) {
        this.requireList = requireList;
    }

    public RequireList addRequire(Require require) {
        if(getRequireList() == null) {
            this.requireList = new ArrayList();
        }
        getRequireList().add(require);
        return this;
    }

    public boolean canPass(Gamer gamer, boolean sendMessage) {
        if(requireList == null) return true;
        for (Require require : requireList) {
            RequireResult requireResult = require.canAccess(gamer, sendMessage);
            if(!requireResult.isAccess()) {
                return false;
            }
        }
        return true;
    }

    public List<String> getLore(Gamer gamer) {
        List<String> loreList = new ArrayList();
        for (Require require : getRequireList()) {
            loreList.add(" &7• " + require.getLoreString(gamer));
            loreList.add("&r");
        }
        loreList.remove(loreList.size() - 1);
        return loreList;
    }

    public void doAfter(Gamer gamer) {
        getRequireList().forEach(require -> require.doAfter(gamer));
    }
}
