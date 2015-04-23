package com.elitetek.scrambleme;

import android.graphics.Bitmap;

public class Image {
	
	int id;
	String owner;
	Bitmap originalPic, scrambledPic;
	boolean fromOutside;
	
	public Image() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Bitmap getOriginalPic() {
		return originalPic;
	}

	public void setOriginalPic(Bitmap originalPic) {
		this.originalPic = originalPic;
	}

	public Bitmap getScrambledPic() {
		return scrambledPic;
	}

	public void setScrambledPic(Bitmap scrambledPic) {
		this.scrambledPic = scrambledPic;
	}

	
}
