package com.elitetek.scrambleme;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;

public class ParseApplication extends Application {

  @Override
  public void onCreate() {
        super.onCreate();

        // Add your initialization code here
        Parse.initialize(this, "5jH2cY9mSveUq96g4a7YRZF7i9XILuY9pd7sSKG9", "sI8KkWUCNrjWuU4H2MtJwAfNCRD3i9qMcZpCruKR");

        ParseFacebookUtils.initialize(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());

        ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);
  }
}
