package com.smilemeback.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Category contains an category thumbnail image
 * and a list of pictures, their labels and audio recordings.
 */
public class Category implements Comparable<Category> {

    /**
     * The index of the category.
     * All categories must have unique indices that define their display order.
     */
    private int index;

    /**
     * The name of the category.
     */
    private Name name;

    /**
     * The actual folder file of the category.
     */
    private File folder;

    /**
     * The thumbnail file of the category.
     * It must reside inside the category with name "_thumbnail.jpg".
     */
    private File thumbnail;


    /**
     * Initialize a new category.
     *
     * The constructor automatically retrieves the {@link #index}, {@link #name} and {@link #thumbnail}
     * of the category.
     *
     * @param categoryFile The folder file of the category.
     * @throws StorageException
     */
    public Category(File categoryFile) throws StorageException {
        this.folder = categoryFile;
        this.index = Storage.getIndex(categoryFile);
        this.name = Storage.getName(categoryFile);
        this.thumbnail = new File(this.folder, Storage.CATEGORY_THUMBNAIL);
        if (!thumbnail.exists()) {
            throw new StorageException("Category" + name + " thumbnail <" + thumbnail.getAbsolutePath() + "> does not exist!");
        }
    }

    public int getIndex() { return this.index; }

    public Name getName() { return this.name; }

    public File getFolder() { return this.folder; }

    public File getThumbnail() {
        return thumbnail;
    }

    public List<Image> getImages() {
        File[] catFiles = folder.listFiles();

        // map icon indices to image and audio files
        Map<Integer, File> imageFiles = scanImages(catFiles);
        Map<Integer, File> audioFiles = scanAudio(catFiles);

        // zip them together and initialize icons
        List<Image> images = new ArrayList<>();
        for (int idx : imageFiles.keySet()) {
            File imageFile = imageFiles.get(idx);
            File audioFile = audioFiles.get(idx);
            String imageName = imageFile.getName();
            imageName = imageName.substring(0, imageName.lastIndexOf('.'));
            if (audioFile == null) {
                System.out.println("Warning: no audio found for " + imageFile.getAbsolutePath());
            } else {
                images.add(new Image(new Name(imageName), imageFile, audioFile));
            }
        }

        return images;
    }

    /**
     * Filter files with given suffix and return them as indexed map.
     * It assumes that the filenames are encoded as following:
     * {icon_index}_{name}{suffix}
     *
     * @param files
     * @param suffix The suffix the desired filenames have to end with.
     * @return The mapping from icon index to files with given suffix.
     */
    protected Map<Integer, File> scanFiles(File[] files, String suffix) {
        Map<Integer, File> images = new HashMap<>();
        for (File f : files) {
            if (f != getThumbnail()) {
                String fnm = f.getName();
                if (fnm.endsWith(suffix)) {
                    int idx = getIndex(fnm);
                    if (idx != -1) {
                        images.put(idx, f);
                    }
                }
            }
        }
        return images;
    }

    protected int getIndex(String fnm) {
        int idx = fnm.indexOf('_');
        if (idx != -1) {
            idx = Integer.parseInt(fnm.substring(0, idx));
        } else {
            idx = fnm.indexOf(".");
            idx = Integer.parseInt(fnm.substring(0, idx));
        }
        return idx;
    }

    protected Map<Integer, File> scanImages(File[] files) {
        return scanFiles(files, ".jpg");
    }

    protected Map<Integer, File> scanAudio(File[] files) {
        return scanFiles(files, ".3gpp");
    }

    @Override
    public String toString() {
        return getFolder().getAbsolutePath();
    }

    @Override
    public int compareTo(Category another) {
        return getIndex() - another.getIndex();
    }
}
