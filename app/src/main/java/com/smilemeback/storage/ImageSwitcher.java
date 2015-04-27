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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class for helping image reordering.
 */
public class ImageSwitcher {
    protected Storage storage;
    protected List<Integer> srcPositions = new ArrayList<>();
    protected List<Integer> destPositions = new ArrayList<>();
    protected Set<Integer> selectedPositions = new HashSet<>();
    protected int lowIdx;
    protected int highIdx;

    private boolean switched = false;

    public ImageSwitcher(Storage storage, List<Image> images, Image switchImage) {
        this.storage = storage;
        computeDirtyRange(images, switchImage);
        computeMovePositions(images, switchImage);
    }

    public List<Integer> getSourcePositions() {
        return srcPositions;
    }

    public List<Integer> getDestinationPositions() {
        return destPositions;
    }

    protected void computeDirtyRange(final List<Image> images, final Image switchImage) {
        lowIdx = switchImage.getPosition();
        highIdx = switchImage.getPosition();
        for (Image image : images) {
            if (image.getPosition() < lowIdx) {
                lowIdx = image.getPosition();
            }
            if (image.getPosition() > highIdx) {
                highIdx = image.getPosition();
            }
            selectedPositions.add(image.getPosition());
        }
    }

    protected void computeMovePositions(final List<Image> images, final Image switchImage) {
        // get the first selection image
        Image selectedImage = images.get(0);
        if (selectedImage.getPosition() < switchImage.getPosition()) {
            // if selection images are left of switch image, then move selection images
            // after the switch image.
            int destIndex = lowIdx;
            // copy everything that is not selection, but is before the switch index
            for (int idx=0 ; idx<switchImage.getPosition() ; ++idx) {
                if (!selectedPositions.contains(idx)) {
                    srcPositions.add(idx);
                    destPositions.add(destIndex);
                    destIndex += 1;
                }
            }
            // now copy switch index
            srcPositions.add(switchImage.getPosition());
            destPositions.add(destIndex);
            destIndex += 1;
            // now copy selection images
            for (int idx=0 ; idx<=highIdx ; ++idx) {
                if (selectedPositions.contains(idx)) {
                    srcPositions.add(idx);
                    destPositions.add(destIndex);
                    destIndex += 1;
                }
            }
            // now copy all not selection images that are after switch image
            for (int idx=switchImage.getPosition()+1 ; idx<=highIdx ; ++idx) {
                if (!selectedPositions.contains(idx)) {
                    srcPositions.add(idx);
                    destPositions.add(destIndex);
                    destIndex += 1;
                }
            }
        } else {
            int destIndex = lowIdx;
            // first, move selection image
            for (int idx=0 ; idx<=highIdx ; ++idx) {
                if (selectedPositions.contains(idx)) {
                    srcPositions.add(idx);
                    destPositions.add(destIndex);
                    destIndex += 1;
                }
            }
            // move switch image
            srcPositions.add(switchImage.getPosition());
            destPositions.add(destIndex);
            destIndex += 1;
            // move all unselected inside dirty region
            for (int idx=lowIdx ; idx<=highIdx ; ++idx) {
                if (!selectedPositions.contains(idx) && idx != switchImage.getPosition()) {
                    srcPositions.add(idx);
                    destPositions.add(destIndex);
                    destIndex += 1;
                }
            }
        }
    }
}
