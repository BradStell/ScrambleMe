package com.elitetek.scrambleme.database;
import com.elitetek.scrambleme.ImagePairs;

import com.elitetek.scrambleme.MainActivity;
import com.parse.ParseUser;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ImageDAO {

	private SQLiteDatabase db;
	
	public ImageDAO(SQLiteDatabase db) {
		this.db = db;
	}
	
	public long save(ImagePairs imagePairs){
	 	ContentValues values = new ContentValues();

        String pathToNormalFile = DbBitmapUtility.saveBitmapToFile(imagePairs.getNormalImage());
        String pathToScrambledFile = DbBitmapUtility.saveBitmapToFile(imagePairs.getScrambledImage());

        values.put(ImageTable.IMAGE_ID, imagePairs.getId());
        values.put(ImageTable.IMAGE_OWNER, imagePairs.getOwnerName());
	 	values.put(ImageTable.IMAGE_NORMAL, pathToNormalFile);
	 	values.put(ImageTable.IMAGE_SCRAMBLED, pathToScrambledFile);
	 	 
	 	return db.insert(ImageTable.TABLE_NAME, null, values);
	}
	
	public boolean delete(int id){
		return db.delete(ImageTable.TABLE_NAME, ImageTable.IMAGE_ID + "=" + id, null) > 0;
	}

    public String getScrambledImagePath(int id) {

        String imagePath = null;

        Cursor c = db.query(true,
                ImageTable.TABLE_NAME,
                new String[] {ImageTable.IMAGE_ID, ImageTable.IMAGE_OWNER, ImageTable.IMAGE_NORMAL,
                        ImageTable.IMAGE_SCRAMBLED}, ImageTable.IMAGE_ID + "=" + id,
                null, null, null, null, null);

        if(c != null){
            c.moveToFirst();
            if (!c.isAfterLast()) {
                imagePath = getImagePathFromCursor(c);
            }
            if(!c.isClosed()){
                c.close();
            }
        }
        return imagePath;

    }
	
	public ImagePairs get(int id){

         ImagePairs imagePairs = null;
	 	 Cursor c = db.query(true,
                 ImageTable.TABLE_NAME,
                 new String[] {ImageTable.IMAGE_ID, ImageTable.IMAGE_OWNER, ImageTable.IMAGE_NORMAL,
                 ImageTable.IMAGE_SCRAMBLED}, ImageTable.IMAGE_ID + "=" + id,
                 null, null, null, null, null);
	 	 
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

        Cursor c = db.rawQuery("SELECT * FROM " + ImageTable.TABLE_NAME + " WHERE " + ImageTable.IMAGE_OWNER + "='" + ParseUser.getCurrentUser().getString("username") + "'", null);

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
	 		 imagePairs.setNormalImage(DbBitmapUtility.getBitmapFromFile(c.getString(2)));
	 		 imagePairs.setScrambledImage(DbBitmapUtility.getBitmapFromFile(c.getString(3)));
	 	 }
	 	 return imagePairs;
	}

    private String getImagePathFromCursor(Cursor c){

        return c.getString(3);

    }

    /**
     * Inner class for Bitmap/File manipulation
     */
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

        public static String saveBitmapToFile(Bitmap bitmap) {

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String relativePicturePath = "picture_" + System.nanoTime() + ".jpg";
            File f = new File(MainActivity.PICTURE_PATH + relativePicturePath);

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

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(MainActivity.PICTURE_PATH + path, options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            String imageType = options.outMimeType;

            options.inSampleSize = calculateInSampleSize(options, 150, 150);

            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(MainActivity.PICTURE_PATH + path, options);

            return bitmap;
        }

        public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) > reqHeight
                        && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }
    }
}
