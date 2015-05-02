package com.elitetek.scrambleme;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ParseApplication extends Application {

  @Override
  public void onCreate() {
        super.onCreate();
      printHashKey();
        // Add your initialization code here
        Parse.initialize(this, "5jH2cY9mSveUq96g4a7YRZF7i9XILuY9pd7sSKG9", "sI8KkWUCNrjWuU4H2MtJwAfNCRD3i9qMcZpCruKR");

        ParseFacebookUtils.initialize(getApplicationContext());
        FacebookSdk.sdkInitialize(getApplicationContext());
      ParseTwitterUtils.initialize("FPEfH4Aq0FqrAljaYKFhsckFd", "13XrsthkgsmeeSytLmYTDqBvo1SMhqvCy5Bmq5dS1jKpcNxI2U");

      ParseACL defaultACL = new ParseACL();
        ParseACL.setDefaultACL(defaultACL, true);
  }


    public void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.elitetek.scrambleme",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
}
