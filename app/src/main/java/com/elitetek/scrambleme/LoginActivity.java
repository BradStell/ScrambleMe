package com.elitetek.scrambleme;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

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

        /** UI element setup ****************************************************************************************/
        login = (Button) findViewById(R.id.buttonLogin);
        login.setOnClickListener(this);
        create = (Button) findViewById(R.id.buttonCreateAccount);
        create.setOnClickListener(this);
        facebook = (Button) findViewById(R.id.buttonFbLogin);
        facebook.setOnClickListener(this);
        twitter = (Button) findViewById(R.id.buttonTweetLogin);
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
        /** END UI element setup ****************************************************************************************/

        // Check to see if the user is already logged in
        ParseUser currentUser = ParseUser.getCurrentUser();
        Profile profile = Profile.getCurrentProfile();
        if (currentUser != null) {
            if (profile != null) {
                Toast.makeText(LoginActivity.this, "Welcome " + profile.getFirstName(), Toast.LENGTH_LONG).show();
            } else {
                if(currentUser.getString("nameWithCase") != null){
                    Toast.makeText(LoginActivity.this, "Welcome " + currentUser.getString("nameWithCase"), Toast.LENGTH_LONG).show();
                }else{
                    String[] name = currentUser.getString("authData").split(":");
                    Toast.makeText(LoginActivity.this, "Welcome " + name[1], Toast.LENGTH_LONG).show();
                }
            }
            startMainActivity();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
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
                                startMainActivity();
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
                startActivity(intent);
                break;

            case R.id.buttonFbLogin:
                List<String> permissions = Arrays.asList("public_profile", "email", "read_custom_friendlists", "user_photos");
                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            Toast.makeText(getBaseContext(), "Failed Login. You may not have facebook app on your device.", Toast.LENGTH_LONG).show();
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else if (user.isNew()) {
                            Toast.makeText(getBaseContext(), "Welcome " + Profile.getCurrentProfile().getFirstName(), Toast.LENGTH_LONG).show();
                            startMainActivity();
                        } else {
                            Toast.makeText(getBaseContext(), "Welcome " + Profile.getCurrentProfile().getFirstName(), Toast.LENGTH_LONG).show();
                            startMainActivity();
                        }
                    }
                });

                break;

            case R.id.buttonTweetLogin:
                ParseTwitterUtils.logIn(LoginActivity.this, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Twitter login.");
                        } else if (user.isNew()) {
                            Log.d("MyApp", "User signed up and logged in through Twitter!");
                            String[] name = user.getString("authData").split(":");
                            //Toast.makeText(getBaseContext(), "Welcome ", Toast.LENGTH_LONG).show();
                            startMainActivity();
                        } else {
                            Log.d("MyApp", "User logged in through Twitter!");
                            //String[] name = user.getString("authData").split(":");
                            Toast.makeText(getBaseContext(), "Welcome ", Toast.LENGTH_LONG).show();
                            startMainActivity();
                        }
                    }
                });
                //Toast.makeText(this, "Will be implemented in version 2", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}
