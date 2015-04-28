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
package com.smilemeback.storage.datamover;


import com.smilemeback.storage.Image;

import java.util.List;

public class ImageDataMover extends DataMover<Image> {

    public ImageDataMover(List<? extends Image> collection, List<? extends Image> selection, Image target) throws IllegalArgumentException {
        super(collection, selection, target);
    }

    public void moveImages() {

    }
}
