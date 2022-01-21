package org.runaway.requirements;

import org.runaway.Gamer;

import java.util.function.Predicate;

public interface Require {
    RequireResult canAccess(Gamer gamer, boolean sendMessage);

    String getName();

    Object getValue();

    String getLoreString(Gamer gamer);

    default void doAfter(Gamer gamer) {

    }
}
