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
