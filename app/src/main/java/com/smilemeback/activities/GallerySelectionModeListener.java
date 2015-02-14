package com.smilemeback.activities;

/**
 * Interface that defines the listener for various activities of
 * {@link com.smilemeback.activities.GallerySelectionMode}.
 */
public interface GallerySelectionModeListener {

    void gallerySelectionModeFinished();
    void selectAllItems();
    void deselectAllItems();
}
