package com.elitetek.scrambleme.database;
import com.elitetek.scrambleme.ImagePairs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DataManager {
	
	Context mContext;
	DataBaseHelper dbOpenHelper;
	SQLiteDatabase db;
	ImageDAO imageDAO;

	public DataManager(Context mContext) {
		this.mContext = mContext;
		dbOpenHelper = new DataBaseHelper(mContext);
		db = dbOpenHelper.getWritableDatabase();
		imageDAO = new ImageDAO(db);
	}

	public void close() {
		db.close();
	}	

    public void saveImagePair(ImagePairs imagePairs) {
        imageDAO.save(imagePairs);
    }

	public void removeImagePair(int id) {
		imageDAO.delete(id);
	}

    public ImagePairs getImagePair(int id) {
        return imageDAO.get(id);
    }

    public String getScrambledImagePath(int id) {
        return imageDAO.getScrambledImagePath(id);
    }

    public ArrayList<ImagePairs> getAllSavedImages() {
        return imageDAO.getAllSavedImages();
    }
}
