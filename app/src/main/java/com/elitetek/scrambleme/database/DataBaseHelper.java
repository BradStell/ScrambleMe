package com.elitetek.scrambleme.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
	
	static final String DATABASE_NAME = "ScrambleMe.db";
	static final int DATABASE_VERSION = 1;

	public DataBaseHelper(Context mContext) {
		super(mContext, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		ImageTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		ImageTable.onUpgrade(db, oldVersion, newVersion);
	}
}
