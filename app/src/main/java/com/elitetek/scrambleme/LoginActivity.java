package com.elitetek.scrambleme;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements View.OnClickListener {

	EditText email;
	EditText password;
	Button login;
	Button create;
	Button facebook;
	Button twitter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		/* UI element setup ****************************************************************************************/
		login = (Button) findViewById(R.id.buttonLogin);
		login.setOnClickListener(this);
		create = (Button) findViewById(R.id.buttonCreateAccount);
		create.setOnClickListener(this);
		facebook = (Button) findViewById(R.id.buttonGallery);
		facebook.setOnClickListener(this);
		twitter = (Button) findViewById(R.id.buttonCamera);
		twitter.setOnClickListener(this);

		TextView title = (TextView) findViewById(R.id.textViewTitle);
		
		email = (EditText) findViewById(R.id.editTextEmailSignIn);
		password = (EditText) findViewById(R.id.editTextPasswordSignIn);		
		
		Typeface titleFont = Typeface.createFromAsset(getAssets(), "fonts/FFF_Tusj.ttf");
		Typeface textFont = Typeface.createFromAsset(getAssets(), "fonts/CaviarDreams.ttf");
		Typeface textFontBold = Typeface.createFromAsset(getAssets(), "fonts/Caviar_Dreams_Bold.ttf");
		
		login.setTypeface(textFontBold);
		login.setTextColor(Color.BLACK);
		login.setTextSize(getResources().getDimension(R.dimen.button_text_size_small));
		
		create.setTypeface(textFontBold);
		create.setTextColor(Color.BLACK);
		create.setTextSize(getResources().getDimension(R.dimen.button_text_size_small));
		
		facebook.setTypeface(textFont);
		facebook.setTextColor(Color.BLACK);
		facebook.setTextSize(getResources().getDimension(R.dimen.button_text_size));
		
		twitter.setTypeface(textFont);
		twitter.setTextColor(Color.BLACK);
		twitter.setTextSize(getResources().getDimension(R.dimen.button_text_size));

		title.setTypeface(titleFont);
		title.setTextSize(getResources().getDimension(R.dimen.title_text_size));

		/** ACTION BAR  ***********************************
		ActionBar mActionBar = getActionBar();
		mActionBar.setDisplayShowHomeEnabled(false);
		mActionBar.setDisplayShowTitleEnabled(false);
		LayoutInflater mInflater = LayoutInflater.from(this);

		View mCustomView = mInflater.inflate(R.layout.custum_action_bar, null);
		TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.textViewActionBarTitle);
		mTitleTextView.setText("Scramble Me");
		mTitleTextView.setTypeface(titleFont);

		mActionBar.setCustomView(mCustomView);
		mActionBar.setDisplayShowCustomEnabled(true);
		/****   /UI element setup ********************************************************************************************/

		
		
		
		// Check to see if the user is already logged in
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser != null) {
			// go to main activity
			Toast.makeText(LoginActivity.this, "Welcome " + currentUser.getString("nameWithCase"), Toast.LENGTH_LONG).show();
			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
	    	finish();
	    	startActivity(intent);
		}	
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
			case R.id.buttonLogin:
				
				if (email.getText().length() == 0 && password.getText().length() > 0) 
					Toast.makeText(LoginActivity.this, "Email Blank!", Toast.LENGTH_LONG).show();
				else if (password.getText().length() == 0 && email.getText().length() > 0)
					Toast.makeText(LoginActivity.this, "Password Blank!", Toast.LENGTH_LONG).show();
				else if (email.getText().length() == 0 && password.getText().length() == 0)
					Toast.makeText(LoginActivity.this, "Email and Password Field Are Blank!", Toast.LENGTH_LONG).show();
				else {
				
					ParseUser.logInInBackground(email.getText().toString().toLowerCase(), password.getText().toString(), new LogInCallback() {
						  public void done(ParseUser user, ParseException e) {
						    if (user != null) {
						    	// User Logged In
						    	Intent intent = new Intent(LoginActivity.this, MainActivity.class);
						    	finish();
						    	startActivity(intent);				    	
						    } else {					      
						    	Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
						    	email.setText("");
						    	password.setText("");
						    }
						  }
					});	
				}
				
				break;
			case R.id.buttonCreateAccount:
				
				Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
				finish();
				startActivity(intent);
				
				break;
			case R.id.buttonGallery:
				
				break;
			case R.id.buttonCamera:
				
				break;
		}
		
	}
}
