package com.smilemeback.storage;


import java.io.File;

public class Image {

    protected Name name;
    protected File image;
    protected File sound;

    public Image(Name imageName, File image, File sound) {
        this.name = imageName;
        this.image = image;
        this.sound = sound;
    }
}
