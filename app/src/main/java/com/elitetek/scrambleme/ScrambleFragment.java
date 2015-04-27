package com.elitetek.scrambleme;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link ScrambleFragment.OnFragmentInteractionListener}
 * interface to handle interaction events.
 *
 */
public class ScrambleFragment extends Fragment implements View.OnClickListener {

	private OnFragmentInteractionListener mListener;
	Button scrambleMe;
    Button DeScrambleMe;
	ImageView pictureToScramble;
	Bitmap img;
	Bitmap pictureBeingViewed;
    Bitmap[] scrambledImageArray;
	String pathToFile;
	LinearLayout root;
    int[] used;
    int[] key;
	private int COUNT = 0;
	private static final int FROM_SHARE = 13;
    Bitmap normalPictureToBeSaved = null;
    Bitmap scrambledPictureToBeSaved = null;

    public ScrambleFragment() {
        // Required empty public constructor
    }

	public ScrambleFragment(String path) {
		// Required empty public constructor
		pathToFile = path;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_scramble, container, false);
	}	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		/***** UI SETUP ******************************************************************************************/
		Typeface textFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/CaviarDreams.ttf");
		
		root = (LinearLayout) getActivity().findViewById(R.id.LinearLayoutScrambleRoot);		
		scrambleMe = (Button) getActivity().findViewById(R.id.buttonScramble);
        DeScrambleMe = (Button) getActivity().findViewById(R.id.buttonDescramble);
		pictureToScramble = (ImageView) getActivity().findViewById(R.id.imageViewPic);
		
		scrambleMe.setOnClickListener(this);
		scrambleMe.setTypeface(textFont);
		scrambleMe.setTextSize(getResources().getDimension(R.dimen.button_text_size));

        DeScrambleMe.setOnClickListener(this);
        DeScrambleMe.setTypeface(textFont);
        DeScrambleMe.setTextSize(getResources().getDimension(R.dimen.button_text_size));
		/***** END  UI SETUP *************************************************************************************/
			
		
		img = setPic(pathToFile);

        // Back button pressed
		ScrambleFragment fragment = this;
		fragment.getView().setFocusableInTouchMode(true);
		fragment.getView().setOnKeyListener( new OnKeyListener() { 
			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent arg2) {
				if( keyCode == KeyEvent.KEYCODE_BACK ) {
		            mListener.fromScrambleFragment();
		        }
		        return true;
			}
		});
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentInteractionListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}
	
	@Override
	public void onClick(View v) {		
		
		switch (v.getId()) {
			case R.id.buttonScramble:
                if (COUNT++ < 1)
                    scrambleImage(img);

                break;
            case R.id.buttonDescramble:

                if (scrambledImageArray != null) {
                    Bitmap[] descrambledArray = new Bitmap[scrambledImageArray.length];

                    for (int i = 0; i < scrambledImageArray.length; i++)
                        descrambledArray[key[i]] = scrambledImageArray[i];

                    Bitmap scaled = putBitmapsTogether(descrambledArray, pictureBeingViewed);
                    pictureBeingViewed = scaled;
                    pictureToScramble.setImageBitmap(scaled);
                    COUNT--;
                } else {
                    Toast.makeText(getActivity(), "Picture is not scrambled", Toast.LENGTH_SHORT).show();
                }
        }
	}

    public void handleMenuPress(int id) {

        switch (id) {
            case R.id.share: /***********************************************************************************************/

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("image/jpeg");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                pictureBeingViewed.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
                try {
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
                startActivityForResult(Intent.createChooser(share, "Share Image"), FROM_SHARE);

                break;

            case R.id.save: /***********************************************************************************************/

                if (scrambledPictureToBeSaved == null) {
                    Toast.makeText(getActivity(), "You must scramble the image first", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Pictures saved", Toast.LENGTH_SHORT).show();
                    // TODO code to save to database
                    mListener.fromScramFragSaveToDatabase(normalPictureToBeSaved, scrambledPictureToBeSaved);
                }
        }

    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == FROM_SHARE)
            if (resultCode == Activity.RESULT_OK)
			    mListener.fromScrambleFragment();

	}

    private Bitmap putBitmapsTogether(Bitmap[] bitmapArray, Bitmap image) {

        int rows = 64;
        int cols = 64;
        int chunkWidth = image.getWidth() / cols;
        int chunkHeight = image.getHeight() / rows;

        Bitmap bm = Bitmap.createBitmap(chunkWidth * rows, chunkHeight * cols, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bm);
        int num = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                canvas.drawBitmap(bitmapArray[num], chunkWidth * i,	chunkHeight * j, null);
                num++;
            }
        }

        //Bitmap scaled = Bitmap.createScaledBitmap(bm, chunkWidth * rows, chunkHeight * cols, true);
        Bitmap scaled = Bitmap.createBitmap(bm);

        return scaled;
    }

    private Bitmap[] getBitmapArray(Bitmap image) {

        int rows = 64;
        int cols = 64;
        int chunkWidth = image.getWidth() / cols;
        int chunkHeight = image.getHeight() / rows;

        int count = 0;
        Bitmap[] imgs = new Bitmap[rows * cols];
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                imgs[count] = Bitmap.createBitmap(image, x * chunkWidth, y * chunkHeight, chunkWidth, chunkHeight);
                count++;
            }
        }

        return imgs;
    }

    /**
     *
     * @param image
     */
	private void scrambleImage(Bitmap image) {

        Bitmap[] imgs = getBitmapArray(image);

        Random rand = new Random(System.nanoTime());
        used = new int [imgs.length];
        key = new int[imgs.length];
        Bitmap[] scrambledArray = new Bitmap[imgs.length];
        int number;

        // Scramble Array and generate key for unscrambling
        for (int i = 0; i < imgs.length; i++) {
            number = rand.nextInt(imgs.length);
            while (used[number] == 1)
                number = rand.nextInt(imgs.length);
            key[i] = number;
            used[number] = 1;
            scrambledArray[i] = imgs[number];
        }

        scrambledImageArray = scrambledArray;

        // Assemble scrambled bitmap array back into single bitmap
		Bitmap scaled = putBitmapsTogether(scrambledArray, image);
        scrambledPictureToBeSaved = scaled;
		pictureBeingViewed = scaled;
		pictureToScramble.setImageBitmap(scaled);
	}

    /**
     *
     * @param mCurrentPhotoPath
     * @return
     */
	private Bitmap setPic(String mCurrentPhotoPath) {
		int targetW = pictureToScramble.getWidth();
		int targetH = pictureToScramble.getHeight();

		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW / targetW, photoH / targetH);
		}

		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        normalPictureToBeSaved = bitmap;
		pictureToScramble.setImageBitmap(bitmap);
        pictureBeingViewed = bitmap;
		return bitmap;
	}

    public interface OnFragmentInteractionListener {
        public void fromScrambleFragment();
        public void fromScramFragSaveToDatabase(Bitmap normal, Bitmap scrambled);
    }
}
