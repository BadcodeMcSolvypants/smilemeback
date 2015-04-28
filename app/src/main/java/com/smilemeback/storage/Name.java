/*
 This file is part of SmileMeBack.

 SmileMeBack is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 SmileMeBack is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with SmileMeBack.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.smilemeback.storage;

import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Category and Image Name class.
 */
public class Name {
    public static final String ILLEGAL_CHARACTERS = "|\\?*<\":>+[]/'";
    public static final Pattern VALID_REGEX = Pattern.compile("[^" + Pattern.quote(ILLEGAL_CHARACTERS) + "]+");
    protected final String name;

    /**
     * Create a new {@link com.smilemeback.storage.Name} instance.
     * In case of an illegal name containing any of the characters {@literal ILLEGAL_CHARACTERS},
     * throws {@link java.lang.IllegalArgumentException} .
     * @param name The name as a {@link java.lang.String} .
     */
    public Name(String name) throws IllegalArgumentException {
        Matcher m = VALID_REGEX.matcher(name);
        if (!m.matches()) {
            throw new IllegalArgumentException("Invalid characters in name <" + name + ">");
        }
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
