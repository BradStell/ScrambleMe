package com.elitetek.scrambleme;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends Activity implements View.OnClickListener {

	EditText name;
	EditText email;
	EditText password;
	EditText passwordConfirm;
	Button create;
	Button cancel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		
		/* Setup UI Components *************************************************************/
		create = (Button) findViewById(R.id.buttonCreateAccount);
		create.setOnClickListener(this);
		cancel = (Button) findViewById(R.id.buttonCancel);
		cancel.setOnClickListener(this);
		
		name = (EditText) findViewById(R.id.editTextName);
		email = (EditText) findViewById(R.id.editTextEmailCreate);
		password = (EditText) findViewById(R.id.editTextPassCreate);
		passwordConfirm = (EditText) findViewById(R.id.editTextPassConf);
		
		Typeface font = Typeface.createFromAsset(getAssets(), "fonts/CaviarDreams.ttf");
		create.setTypeface(font);
		cancel.setTypeface(font);
		/***********************************************************************************/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.signup, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int id = item.getItemId();
		
		if (id == R.id.action_settings) {
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.buttonCreateAccount:
			
			if (name.getText().length() == 0)
				Toast.makeText(SignupActivity.this, "Name is left Blank", Toast.LENGTH_SHORT).show();
			else if (email.getText().length() == 0) 
				Toast.makeText(SignupActivity.this, "Email is left Blank", Toast.LENGTH_SHORT).show();
			else if (password.getText().length() == 0)
				Toast.makeText(SignupActivity.this, "Password is left Blank", Toast.LENGTH_SHORT).show();
			else if (passwordConfirm.getText().length() == 0)
				Toast.makeText(SignupActivity.this, "Confirm Password is left Blank", Toast.LENGTH_SHORT).show();
			else if (password.getText().toString().compareTo(passwordConfirm.getText().toString()) != 0) {
				Toast.makeText(SignupActivity.this, "Passwords Do Not Match", Toast.LENGTH_SHORT).show();
				password.setText("");
				passwordConfirm.setText("");
			}
			else {
				ParseUser user = new ParseUser();
				user.setUsername(email.getText().toString().toLowerCase());
				user.setPassword(password.getText().toString());
				user.setEmail(email.getText().toString().toLowerCase());			
				user.put("name", name.getText().toString().toLowerCase());
				user.put("nameWithCase", name.getText().toString());
				 
				user.signUpInBackground(new SignUpCallback() {
					public void done(ParseException e) {
					    if (e == null) {
					    	Toast.makeText(SignupActivity.this, "Account Created", Toast.LENGTH_LONG).show();			    	
					    } else {					    	
					    	Toast.makeText(SignupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();					    	
					    	email.setText("");
					    }
					}
				});
			}
			
			break;
		case R.id.buttonCancel:
			
			ParseUser currentUser = ParseUser.getCurrentUser();
			if (currentUser != null)
				ParseUser.logOut();	
			
			Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
			finish();
			startActivity(intent);
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
		finish();
		startActivity(intent);
	}
	
	
}
