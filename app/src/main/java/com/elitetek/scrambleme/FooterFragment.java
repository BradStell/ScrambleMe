package com.elitetek.scrambleme;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link FooterFragment.OnFragmentInteractionListener}
 * interface to handle interaction events.
 *
 */
public class FooterFragment extends Fragment implements View.OnClickListener {

	Button gallery;
	Button camera;
	private OnFragmentInteractionListener mListener;
	
	private ImageView mImageView;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_FILE = 2;
	private static final int SCRAMBLE_IMAGE = 3;
	private String mCurrentPhotoPath;
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;	
	

	public FooterFragment() {
		// Required empty public constructor
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		/***** UI SETUP ***************************************************************************************************/
		Typeface textFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/CaviarDreams.ttf");
		
		gallery = (Button) getActivity().findViewById(R.id.buttonGallery);
		camera = (Button) getActivity().findViewById(R.id.buttonCamera);
		
		gallery.setTypeface(textFont);
		gallery.setTextColor(Color.BLACK);
		gallery.setTextSize(getResources().getDimension(R.dimen.button_text_size));
		gallery.setOnClickListener(this);
		
		camera.setTypeface(textFont);
		camera.setTextColor(Color.BLACK);
		camera.setTextSize(getResources().getDimension(R.dimen.button_text_size));
		camera.setOnClickListener(this);
		/***** END UI SETUP ***********************************************************************************************/
		
		mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_footer, container, false);	
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
		
			case R.id.buttonCamera:
				
				Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
					Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File f = null;
					try {
						f = setUpPhotoFile();
						mCurrentPhotoPath = f.getAbsolutePath();
						takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,	Uri.fromFile(f));
					} catch (IOException e) {
						e.printStackTrace();
						f = null;
						mCurrentPhotoPath = null;
					}
					startActivityForResult(takePictureIntent, PICK_FROM_CAMERA);
				}				
				break;
				
			case R.id.buttonGallery:
				
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, PICK_FROM_FILE);						
		}
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_FROM_FILE) {
			if (resultCode == getActivity().RESULT_OK) {
				Uri selectedImage = data.getData();
				try {
					String path = getRealPathFromURI(getActivity().getBaseContext(),selectedImage);
					mListener.fromFooterFragment(path);					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (requestCode == PICK_FROM_CAMERA) {			
			if (resultCode == getActivity().RESULT_OK) {
				if (mCurrentPhotoPath != null) {
					galleryAddPic();
					//Intent intent = new Intent(ImagePickerActivity.this, ScrambleImageActivity.class);
					//intent.putExtra("filename", mCurrentPhotoPath);
					//startActivityForResult(intent, SCRAMBLE_IMAGE);
					mListener.fromFooterFragment(mCurrentPhotoPath);
				}
			}			
		}
	}
	
	private void galleryAddPic() {
		Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(mCurrentPhotoPath);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		getActivity().sendBroadcast(mediaScanIntent);
	}
	
	private File setUpPhotoFile() throws IOException {
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();
		return f;
	}
	
	private File createImageFile() throws IOException {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
				.format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = getAlbumDir();
		File image = File.createTempFile(imageFileName, /* prefix */
				".jpg", /* suffix */
				storageDir /* directory */
		);

		mCurrentPhotoPath = "file:" + image.getAbsolutePath();
		return image;
	}
	
	private File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {

			storageDir = mAlbumStorageDirFactory
					.getAlbumStorageDir(getAlbumName());

			if (storageDir != null) {
				if (!storageDir.mkdirs()) {
					if (!storageDir.exists()) {
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}
		} else {
			Log.v(getString(R.string.app_name),
					"External storage is not mounted READ/WRITE.");
		}
		return storageDir;
	}
	
	private String getAlbumName() {
		return "Scrambled Images";
	}
	
	public String getRealPathFromURI(Context context, Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = context.getContentResolver().query(contentUri, proj, null,
					null, null);
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public interface OnFragmentInteractionListener {
		public void fromFooterFragment(String path);
	}
	
}
