package com.elitetek.scrambleme;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sambit on 4/24/2015.
 */
public class UserInfo {

    public String id;
    public String birthday;
    public String name;
    public String gender;
    public String link;
    public String location;
    public String locale;
    public String updated_time;


    public UserInfo(JSONObject jsonObject) {
        if (jsonObject != null) {
            try {
                id = jsonObject.getString("id");
                name = jsonObject.getString("name");
                gender = jsonObject.getString("gender");
                link = jsonObject.getString("link");
                birthday = jsonObject.getString("birthday");
                location = jsonObject.getJSONArray("location").getString(1);
                locale = jsonObject.getString("locale");
                updated_time = jsonObject.getString("updated_time");
                Log.d("id " + id + "name " + name + "gender " + gender, "link " + link + "birthday " + birthday + "location " + location + "locale " + locale);
            } catch (JSONException e) {
                e.getMessage();
            }
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
