package com.elitetek.scrambleme;

import android.graphics.Bitmap;

import java.util.Random;

/**
 * The pair of images, both normal and scrambled
 * Created by B-rad on 4/26/2015.
 */
public class ImagePairs {

    private int id;
    private String ownerName;
    private Bitmap normalImage, scrambledImage;

    public ImagePairs () {
        Random rand = new Random(System.nanoTime());
        id = rand.nextInt(Integer.MAX_VALUE);
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Bitmap getNormalImage() {
        return normalImage;
    }

    public void setNormalImage(Bitmap normalImage) {
        this.normalImage = normalImage;
    }

    public Bitmap getScrambledImage() {
        return scrambledImage;
    }

    public void setScrambledImage(Bitmap scrambledImage) {
        this.scrambledImage = scrambledImage;
    }
}
