package com.sumod.pokenav.utils;

/**
 * Created by sumodkulkarni on 16/7/16.
 */
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;


public class PrefManager {

    public static String SHARED_PREFS = "com.sumod.pokenav";

    /**
     * Preferences
     * Add your preferences here
     */

    public static String PREF_USER_NAME = "pref_username";
    public static String PREF_EMAIL = "pref_email";
    public static String PREF_USER_PROFILE_PICTURE = "pref_user_profile_pic";
    public static String PREF_REGISTRATION_DONE = "pref_registration_done";
    public static String PREF_MAIN_ACT_LAUNCH = "pref_main_activity_launch";
    public static String PREF_FIRST_RUN = "pref_first_run";
    public static String PREF_APP_VERSION = "pref_app_version";

    /**
     * This function returns the Shared_Prefs for particular key
     * @param context Context of the android application running (getApplicationContext()) will help
     * @param key The key for the desired preference
     * @param object The class of the desired preference value (e.g: String.class)
     * @return Object of the type of preference
     */

    public static Object getPrefs(Context context, String key, Class object) {
        if (object.equals(String.class)) {
            return context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString(key, null);
        } else if (object.equals(Integer.class)) {
            return context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getInt(key, 0);
        } else if (object.equals(Boolean.class)) {
            return context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getBoolean(key, false);
        } else if (object.equals(Float.class)) {
            return context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getFloat(key, 0.0f);
        }
        return null;
    }

    /**
     * Use this function to store the preference's value
     * @param context Context of the android application running (getApplicationContext()) will help
     * @param key The key for the desired preference
     * @param value The value of the desired preference
     */

    public static void putPrefs(Context context, String key, Object value) {
        if (value.getClass().equals(String.class)) {
            context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).edit().putString(key, (String) value).commit();
        } else if (value.getClass().equals(Boolean.class)) {
            context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).edit().putBoolean(key, (Boolean) value).apply();
        } else if (value.getClass().equals(Integer.class)) {
            context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).edit().putInt(key, (Integer) value).apply();
        } else if (value.getClass().equals(Float.class)) {
            context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).edit().putFloat(key, (Float) value).apply();
        }

    }

}

