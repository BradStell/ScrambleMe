package com.elitetek.scrambleme.database;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class ImageTable {
	
	public static final String TABLE_NAME = "ImagePairs";
	public static final String IMAGE_ID = "id";
	public static final String IMAGE_NORMAL = "imageNormal";
    public static final String IMAGE_SCRAMBLED = "imageScrambled";


	static public void onCreate(SQLiteDatabase db) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE " + TABLE_NAME + " (");
		sb.append(IMAGE_ID + " text primary key, ");
		sb.append(IMAGE_NORMAL + " text not null, ");
		sb.append(IMAGE_SCRAMBLED + " text not null");
		sb.append(");");
		try {
			db.execSQL(sb.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	static public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		ImageTable.onCreate(db);
	}
}
