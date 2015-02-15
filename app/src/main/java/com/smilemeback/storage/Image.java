/**
 * This file is part of SmileMeBack.

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


import java.io.File;

public class Image {

    protected Name name;
    protected File image;
    protected File audio;

    public Image(Name imageName, File image, File audio) {
        this.name = imageName;
        this.image = image;
        this.audio = audio;
    }

    public Name getName() {
        return name;
    }

    public File getImage() {
        return image;
    }

    public File getAudio() {
        return audio;
    }
}
