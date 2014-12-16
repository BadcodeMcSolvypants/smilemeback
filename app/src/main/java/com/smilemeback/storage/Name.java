package com.smilemeback.storage;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Category and Image Name class.
 */
public class Name {
    protected final String name;

    public Name(String name) {
        this.name = name;
    }

    public Name(Name name) {
        this.name = name.toString();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }
}
