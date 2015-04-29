package com.elitetek.scrambleme;

import com.elitetek.scrambleme.database.DataManager;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.ParseUser;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends Activity implements MainFragment.OnFragmentInteractionListener,
													  FooterFragment.OnFragmentInteractionListener,
													  ScrambleFragment.OnFragmentInteractionListener {

	String pathToPhoto;
    DataManager dataManager;
    public static ArrayList<ImagePairs> picturesList;
    public static int[] KEY;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		dataManager = new DataManager(this);

		getFragmentManager().beginTransaction()
        	.add(R.id.container, new MainFragment(), "main")
    		.add(R.id.footer, new FooterFragment(), "footer")
    		.commit();

		setupActionBar();

        generateKey();
	}

    public void graphRequest() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        Log.d("JSON", object.toString());
                        UserInfo userInfo = new UserInfo(object);
                    }
                });
        Bundle params = new Bundle();
        params.putString("name", "ScrambleMe");
        //new FaceBookGraphAPI().execute("https://graph.facebook.com/v2.1/10150220813846431/photos?access_token=" + AccessToken.getCurrentAccessToken().getToken());

        //String response = mFacebook.request("https://graph.facebook.com/me/albums",params,"POST");
        //GraphRequest.nre
        //String albumId=Util.parseJson(response).getString("id");

        GraphRequest graphRequest = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), "me/albums", new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

            }
        });
    }

    private void setupActionBar() {
        /** Setup the custum action bar */
        ActionBar mActionBar = getActionBar();
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        Typeface titleFont = Typeface.createFromAsset(getAssets(), "fonts/FFF_Tusj.ttf");

        View mCustomView = mInflater.inflate(R.layout.custum_action_bar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.textViewActionBarTitle);
        mTitleTextView.setText("Scrambles");
        mTitleTextView.setTextSize(getResources().getDimension(R.dimen.action_bar_text_size));
        mTitleTextView.setPadding(5,10,5,5);
        mTitleTextView.setTypeface(titleFont);

        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int id = item.getItemId();
        ScrambleFragment scramFrag = (ScrambleFragment) getFragmentManager().findFragmentByTag("scramble");
		
		if (id == R.id.logout) {
			
			ParseUser.logOut();
			finish();
			Intent backToLogin = new Intent(MainActivity.this, LoginActivity.class);
			startActivity(backToLogin);
			
			return true;
		}
        else if (id == R.id.share) {

            if (scramFrag != null)
                scramFrag.handleMenuPress(id);
            else
                Toast.makeText(this, "Choose an image to share first", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.save) {

            if (scramFrag != null)
                scramFrag.handleMenuPress(id);
            else
                Toast.makeText(this, "Choose an image to save first", Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.delete) {
            if (scramFrag != null)
                scramFrag.handleMenuPress(id);
            else
                Toast.makeText(this, "Choose an image to delete first", Toast.LENGTH_SHORT).show();
        }
		
		return super.onOptionsItemSelected(item);
	}



	/**
	 * Prevent back button from leaving the app
	 */
	@Override
	public void onBackPressed() {

        MainFragment main = (MainFragment) getFragmentManager().findFragmentByTag("main");

        if (main == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new MainFragment(), "main")
                    .add(R.id.footer, new FooterFragment(), "footer")
                    .commit();
        }
    }

    @Override
    public void fromMainFragment(int id, String imagePath) {
        // code to run from MainFragment
//        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("footer")).commit();
//        getFragmentManager().beginTransaction().replace(R.id.container, new ScrambleFragment(normal, scrambled), "scramble").commit();
        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("footer")).commit();
        getFragmentManager().beginTransaction().replace(R.id.container, new ScrambleFragment(id, imagePath), "scramble").commit();

    }

    @Override
	public void fromFooterFragment(String path) {
		// code to run from FooterFragment
		/*
		Bundle stuffForScrambleFrag = new Bundle();
		stuffForScrambleFrag.putString("pathToPhoto", path);
		ScrambleFragment scrambleReference = new ScrambleFragment();
		scrambleReference.setArguments(stuffForScrambleFrag);
		*/
		getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("footer")).commit();
		getFragmentManager().beginTransaction().replace(R.id.container, new ScrambleFragment(path), "scramble").commit();		
	}

	@Override
	public void fromScrambleFragment() {
		// code to run from ScrambleFragment
		getFragmentManager().beginTransaction()
			.replace(R.id.container, new MainFragment(), "main")
			.add(R.id.footer, new FooterFragment(), "footer")
			.commit();
	}

    @Override
    public void fromScramFragSaveToDatabase(Bitmap normal, Bitmap scrambled) {
        ImagePairs imagePairs = new ImagePairs();
        imagePairs.setOwnerName(ParseUser.getCurrentUser().getString("username"));
        imagePairs.setNormalImage(normal);
        imagePairs.setScrambledImage(scrambled);
        dataManager.saveImagePair(imagePairs);
    }

    @Override
    public void fromScramFragDeleteFromDatabase(int id) {
        dataManager.removeImagePair(id);
    }

    public void generateKey() {

        AssetManager assetManager = getResources().getAssets();
        InputStream is = null;
        BufferedReader reader;
        String line;

        try {
            is = assetManager.open("key.txt");
            reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();

            if (is != null) {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            createIntArrayForKey(sb.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createIntArrayForKey(String stringFromFile) {

        String[] allStrings = stringFromFile.split( "\\s" );
        int[] intArray = new int[allStrings.length];
        for( int i = 0; i < allStrings.length; ++i ){
            try{
                intArray[i] = Integer.parseInt( allStrings[i] );
            }catch( NumberFormatException e ){
                // Do whatever you think is appropriated
                Log.d("key", "number format exception ");
                intArray[i] = -1;
            }
        }
        KEY = intArray;
    }
}
