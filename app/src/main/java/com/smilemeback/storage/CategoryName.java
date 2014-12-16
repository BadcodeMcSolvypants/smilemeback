package com.smilemeback.storage;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Category name class.
 */
public class CategoryName {
    protected final String name;

    public CategoryName(String name) {
        this.name = name;
    }

    public CategoryName(CategoryName catname) {
        this.name = catname.toString();
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
