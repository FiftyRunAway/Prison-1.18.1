package org.runaway.fishing.pair;

import java.io.Serializable;
import java.util.Objects;

public class Pair<K, V> implements Serializable {

    private K key;
    private V value;

    public Pair(@NamedArg("key") final K key, @NamedArg("value") final V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

    public void setKey(@NamedArg("key") final K key) {
        this.key = key;
    }

    public void setValue(@NamedArg("key") final V value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.key + "=" + this.value;
    }

    @Override
    public int hashCode() {
        return this.key.hashCode() * 13 + ((this.value == null) ? 0 : this.value.hashCode());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Pair) {
            Pair pair = (Pair)o;
            return Objects.equals(this.key, pair.key) && Objects.equals(this.value, pair.value);
        }
        return false;
    }
}
