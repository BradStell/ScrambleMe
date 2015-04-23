package com.elitetek.scrambleme;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link ScrambleFragment.OnFragmentInteractionListener}
 * interface to handle interaction events.
 *
 */
public class ScrambleFragment extends Fragment implements View.OnClickListener {

	private OnFragmentInteractionListener mListener;
	Button scrambleMe;
	ImageView pictureToScramble;
	Bitmap img;
	Bitmap scrambledImg;
	String pathToFile;
	LinearLayout root;
	private int COUNT = 0;
	private static final int FROM_SHARE = 13;

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
		pictureToScramble = (ImageView) getActivity().findViewById(R.id.imageViewPic);
		
		scrambleMe.setOnClickListener(this);
		scrambleMe.setTypeface(textFont);
		scrambleMe.setTextSize(getResources().getDimension(R.dimen.button_text_size));
		/***** END  UI SETUP *************************************************************************************/
			
		
		img = setPic(pathToFile);
		
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
				if (COUNT++ == 0) {
					scrambleMe.setText("Share");
					scrambleImage(img);
				}
				else {	
					
					Intent share = new Intent(Intent.ACTION_SEND);
					share.setType("image/jpeg");
					ByteArrayOutputStream bytes = new ByteArrayOutputStream();
					scrambledImg.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
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
				}
					
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == FROM_SHARE)
			mListener.fromScrambleFragment();
	}

	public interface OnFragmentInteractionListener {
		public void fromScrambleFragment();
	}
	
	private void scrambleImage(Bitmap image) {
		int rows = 64;
		int cols = 64;
		int chunkWidth = image.getWidth() / cols;
		int chunkHeight = image.getHeight() / rows;
		Log.d("demo", image.getWidth() + " " + image.getHeight());
		int count = 0;
		Bitmap[] imgs = new Bitmap[rows * cols];
		for (int x = 0; x < rows; x++) {
			for (int y = 0; y < cols; y++) {				
				imgs[count] = Bitmap.createBitmap(image, x * chunkWidth, y * chunkHeight, chunkWidth, chunkHeight);
				count++;
			}
		}
		ArrayList<Bitmap> imgList = new ArrayList<Bitmap>();
		for (int i = 0; i < imgs.length; i++) {
			if(imgs[i] != null){
				imgList.add(imgs[i]);
			}
		}
		Collections.shuffle(imgList);
		
		Bitmap bm = Bitmap.createBitmap(chunkWidth * rows, chunkHeight * cols, Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas(bm);
		int num = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				canvas.drawBitmap(imgList.get(num), chunkWidth * i,	chunkHeight * j, null);
				num++;
			}
		}
		int nh = (int) ( bm.getHeight() * (512.0 / bm.getWidth()) );
		Bitmap scaled = Bitmap.createScaledBitmap(bm, 512, nh, true);
		scrambledImg = scaled;
		pictureToScramble.setImageBitmap(scaled);
	}

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
		
		int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
		Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);

		pictureToScramble.setImageBitmap(scaled);
		return bitmap;
	}	
}
