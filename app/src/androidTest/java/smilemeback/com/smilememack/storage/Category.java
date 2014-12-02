package smilemeback.com.smilememack.storage;

import java.io.File;

/**
 * Category contains an category thumbnail image
 * and a list of pictures, their labels and audio recordings.
 */
public interface Category {
    String getName();
    File getImage();
}
