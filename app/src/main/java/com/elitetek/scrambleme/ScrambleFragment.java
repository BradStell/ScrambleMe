package com.elitetek.scrambleme;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

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
import android.widget.Toast;

import com.elitetek.scrambleme.database.ImageDAO;
import com.facebook.AccessToken;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.parse.ParseUser;

/**
 * A simple {@link Fragment} subclass. Activities that contain this fragment
 * must implement the {@link ScrambleFragment.OnFragmentInteractionListener}
 * interface to handle interaction events.
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
	private int COUNT = 0;
    private int SAVE_COUNT = 0;
    private static final int FROM_SHARE = 13;
    Bitmap normalPictureToBeSaved = null;
    Bitmap scrambledPictureToBeSaved = null;
    Bitmap normalPictureFromListView;
    Bitmap scrambledPictureFromListView;
    int picturePairFromListViewId = -1;
    String imagePath = null;

    public ScrambleFragment() {
        // Required empty public constructor
    }

    public ScrambleFragment(String path) {
        // Required empty public constructor
        pathToFile = path;
    }

    public ScrambleFragment(int id, String imagePath) {
        picturePairFromListViewId = id;
        this.imagePath = imagePath;
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

        if (picturePairFromListViewId < 0)
            img = setPic(pathToFile);
        else if (pathToFile != null) {
            img = setPic(pathToFile);
        } else {
            img = setScaledPic(imagePath);
            pictureToScramble.setImageBitmap(img);
        }

        // Back button pressed
        ScrambleFragment fragment = this;
        fragment.getView().setFocusableInTouchMode(true);
        fragment.getView().setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View arg0, int keyCode, KeyEvent arg2) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mListener.fromScrambleFragment();
                    Log.d("click", "in scramble fragment back pressed");
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

                if (COUNT++ < 1) {
                    if (img != null)
                        scrambleImage(img);
                    else
                        Toast.makeText(getActivity(), "Picture is already scrambled", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.buttonDescramble:

                if (scrambledImageArray != null) {
                    Bitmap[] descrambledArray = new Bitmap[scrambledImageArray.length];

                    for (int i = 0; i < scrambledImageArray.length; i++)
                        descrambledArray[MainActivity.KEY[i]] = scrambledImageArray[i];

                    Bitmap scaled = putBitmapsTogether(descrambledArray, pictureBeingViewed);
                    pictureBeingViewed = scaled;
                    pictureToScramble.setImageBitmap(scaled);
                    COUNT--;
                } else {

                    Bitmap[] imageIntoArray = getBitmapArray(img);
                    Bitmap[] descrambledArray = new Bitmap[imageIntoArray.length];

                    for (int i = 0; i < imageIntoArray.length; i++)
                        descrambledArray[MainActivity.KEY[i]] = imageIntoArray[i];

                    Bitmap scaled = putBitmapsTogether(descrambledArray, img);
                    pictureBeingViewed = scaled;
                    scrambledPictureToBeSaved = scaled;
                    pictureToScramble.setImageBitmap(scaled);
                    COUNT--;
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

                if (SAVE_COUNT > 0)
                    Toast.makeText(getActivity(), "The image is already saved", Toast.LENGTH_SHORT).show();
                else {
                    if (scrambledPictureToBeSaved != null && normalPictureToBeSaved != null) {
                        Toast.makeText(getActivity(), "Pictures saved", Toast.LENGTH_SHORT).show();
                        SAVE_COUNT++;
                        ImagePairs newImagePair = new ImagePairs();
                        newImagePair.setOwnerName(ParseUser.getCurrentUser().getString("username"));
                        newImagePair.setNormalImage(normalPictureToBeSaved);
                        newImagePair.setScrambledImage(scrambledPictureToBeSaved);
                        MainActivity.picturesList.add(newImagePair);

                        normalPictureFromListView = normalPictureToBeSaved;
                        scrambledPictureFromListView = scrambledPictureToBeSaved;

                        mListener.fromScramFragSaveToDatabase(normalPictureToBeSaved, scrambledPictureToBeSaved);
                    } else {
                        Toast.makeText(getActivity(), "Scramble/Descramble picture first", Toast.LENGTH_SHORT).show();
                    }

                }

                break;

            case R.id.delete: /*********************************************************************************************/

                if (picturePairFromListViewId < 0)
                    Toast.makeText(getActivity(), "Images are not saved", Toast.LENGTH_SHORT).show();
                else {

                    // MainFragment mainFragment = (MainFragment) getFragmentManager().findFragmentByTag("main");
                    // mainFragment.removeItemFromArrayLists(picturePairFromListViewId);

                    mListener.fromScramFragDeleteFromDatabase(picturePairFromListViewId);
                }
                break;

            case R.id.fbshare:
                //Bitmap image = null;
                if (AccessToken.getCurrentAccessToken() != null) {
                    if (ShareDialog.canShow(SharePhotoContent.class)) {
                        SharePhoto photo = new SharePhoto.Builder()
                                .setBitmap(scrambledPictureToBeSaved)
                                .build();
                        SharePhotoContent content = new SharePhotoContent.Builder()
                                .addPhoto(photo)
                                .build();
                        ShareDialog.show(getActivity(), content);
                    }
                } else {
                    Toast.makeText(getActivity().getBaseContext(), "You must be logged in through facebook inorder to share", Toast.LENGTH_LONG).show();
                }

                break;
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
                canvas.drawBitmap(bitmapArray[num], chunkWidth * i, chunkHeight * j, null);
                num++;
            }
        }

        Bitmap scaled = Bitmap.createBitmap(bm);

        return scaled;
    }

    private Bitmap[] getBitmapArray(Bitmap image) {

        int rows = 64;
        int cols = 64;
        int chunkWidth = image.getWidth() / cols;
        int chunkHeight = image.getHeight() / rows;

        Log.d("size", image.getWidth() + " divide: " + (float) image.getWidth() / cols);

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
     * @param image
     */
    private void scrambleImage(Bitmap image) {

        Bitmap[] imgs = getBitmapArray(image);

        Bitmap[] scrambledArray = new Bitmap[imgs.length];

        for (int i = 0; i < MainActivity.KEY.length; i++)
            scrambledArray[i] = imgs[MainActivity.KEY[i]];

        scrambledImageArray = scrambledArray;

        // Assemble scrambled bitmap array back into single bitmap
        Bitmap scaled = putBitmapsTogether(scrambledArray, image);
        scrambledPictureToBeSaved = scaled;
        pictureBeingViewed = scaled;
        pictureToScramble.setImageBitmap(scaled);
    }

    public Bitmap setScaledPic(String path) {

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(MainActivity.PICTURE_PATH + path, bmOptions);

        bmOptions.inSampleSize = calculateInSampleSize(bmOptions, 400, 400);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(MainActivity.PICTURE_PATH + path, bmOptions);

        //int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
       // Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);

        normalPictureToBeSaved = bitmap;
        pictureToScramble.setImageBitmap(bitmap);
        pictureBeingViewed = bitmap;
        return bitmap;
    }

    /**
     * @param mCurrentPhotoPath
     * @return
     */
    private Bitmap setPic(String mCurrentPhotoPath) {

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        bmOptions.inSampleSize = calculateInSampleSize(bmOptions, 400, 400);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        //int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
        //Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);

        normalPictureToBeSaved = bitmap;
        pictureToScramble.setImageBitmap(bitmap);
        pictureBeingViewed = bitmap;
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

    public interface OnFragmentInteractionListener {
        public void fromScrambleFragment();

        public void fromScramFragSaveToDatabase(Bitmap normal, Bitmap scrambled);

        public void fromScramFragDeleteFromDatabase(int id);
    }
}
