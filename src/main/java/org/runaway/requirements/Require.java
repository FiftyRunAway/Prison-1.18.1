package org.runaway.requirements;

import org.runaway.Gamer;

import java.util.function.Predicate;

public interface Require {
    RequireResult canAccess(Gamer gamer);

    String getName();

    Object getValue();

    String getLoreString(Gamer gamer);

    default void doAfter(Gamer gamer, RequireResult requireResult) {

    }
}
