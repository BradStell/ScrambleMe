package com.elitetek.scrambleme.database;

import com.elitetek.scrambleme.ImagePairs;
import com.parse.ParseUser;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class ImageDAO {

	private SQLiteDatabase db;
	
	public ImageDAO(SQLiteDatabase db) {
		this.db = db;
	}
	
	public long save(ImagePairs imagePairs){
	 	ContentValues values = new ContentValues();

        //TODO remove blob and turn into path to .jpg

        String pathToNormalFile = DbBitmapUtility.saveBitmapToFile(imagePairs.getNormalImage());
        String pathToScrambledFile = DbBitmapUtility.saveBitmapToFile(imagePairs.getScrambledImage());

        values.put(ImageTable.IMAGE_ID, imagePairs.getId());
        values.put(ImageTable.IMAGE_OWNER, imagePairs.getOwnerName());
	 	values.put(ImageTable.IMAGE_NORMAL, pathToNormalFile);
	 	values.put(ImageTable.IMAGE_SCRAMBLED, pathToScrambledFile);
	 	 
	 	return db.insert(ImageTable.TABLE_NAME, null, values);
	}
	
	public boolean delete(ImagePairs imagePairs){
		return db.delete(ImageTable.TABLE_NAME, ImageTable.IMAGE_ID + "=" + imagePairs.getId(), null) > 0;
	}
	
	public ImagePairs get(String id){

         ImagePairs imagePairs = null;
	 	 Cursor c = db.query(true, ImageTable.TABLE_NAME, new String[] {ImageTable.IMAGE_ID, ImageTable.IMAGE_OWNER, ImageTable.IMAGE_NORMAL, ImageTable.IMAGE_SCRAMBLED}, ImageTable.IMAGE_ID + "=" + id, null, null, null, null, null);
	 	 
	 	 if(c != null){
			 c.moveToFirst();			 
			 if (!c.isAfterLast()) {
				 imagePairs = this.buildImagePairFromCursor(c);
			 }			 	
		 	 if(!c.isClosed()){
				 c.close();
		 	 }			 	 	 	 	
	 	 }	 	
	 	 return imagePairs;
	}

    public ArrayList<ImagePairs> getAllSavedImages() {
        ArrayList<ImagePairs> imageList = new ArrayList<ImagePairs>();

        Cursor c = db.rawQuery("SELECT * FROM " + ImageTable.TABLE_NAME + " WHERE username = " + ParseUser.getCurrentUser().getString("username"), null);

        if (c != null) {
            c.moveToFirst();
            if (! c.isAfterLast()) {
                imageList = this.buildArrayListFromCursor(c);
            }
            if (! c.isClosed()) {
                c.close();
            }
        }
        return imageList;
    }

    private ArrayList<ImagePairs> buildArrayListFromCursor(Cursor c){
        ArrayList<ImagePairs> imageList = new ArrayList<ImagePairs>();
        ImagePairs imagePairs;

        while(! c.isAfterLast()) {
            imagePairs = new ImagePairs();
            imagePairs.setId(Integer.parseInt(c.getString(0)));
            imagePairs.setOwnerName(c.getString(1));
            imagePairs.setNormalImage( DbBitmapUtility.getBitmapFromFile(c.getString(2)));
            imagePairs.setScrambledImage( DbBitmapUtility.getBitmapFromFile(c.getString(3)));
            imageList.add(imagePairs);
            c.moveToNext();
        }
        return imageList;
    }

	private ImagePairs buildImagePairFromCursor(Cursor c){
		ImagePairs imagePairs = null;
	 	 if(c != null) {
	 		 imagePairs = new ImagePairs();
	 		 imagePairs.setId(Integer.parseInt(c.getString(0)));
             imagePairs.setOwnerName(c.getString(1));
	 		 imagePairs.setNormalImage(DbBitmapUtility.getImage(c.getBlob(2)));
	 		 imagePairs.setScrambledImage(DbBitmapUtility.getImage(c.getBlob(3)));
	 	 }
	 	 return imagePairs;
	}

    public static class DbBitmapUtility {

        // convert from bitmap to byte array
        public static byte[] getBytes(Bitmap bitmap) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return stream.toByteArray();
        }

        // convert from byte array to bitmap
        public static Bitmap getImage(byte[] image) {
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        }

        //TODO save bitmap to file & retrieve from database back to bitmap
        public static String saveBitmapToFile(Bitmap bitmap) {

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String relativePicturePath = "picture_" + System.nanoTime() + ".jpg";
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + relativePicturePath);

            try {
                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());

            } catch (IOException e) {
                e.printStackTrace();
            }

            return relativePicturePath;
        }

        public static Bitmap getBitmapFromFile(String path) {

            Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + File.separator + path);

           /* ByteArrayOutputStream blob = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 *//*ignored for PNG*//*, blob);
            byte[] bitmapdata = blob.toByteArray();



            byte[] imageBytes = new byte[(int) f.length()];
            ByteArrayInputStream imageStream = new ByteArrayInputStream(imageBytes);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);*/

            return bitmap;
        }
    }
}
