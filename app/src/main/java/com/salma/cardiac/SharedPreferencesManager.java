package com.salma.cardiac;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {


    private static final String SHARED_NAME = "myPrefs";


    // properties
    private static final String SOME_STRING_VALUE = "SOME_STRING_VALUE";
    // other properties...


    public static boolean doesContain(Context context , String value){
        return getSharedPreferences(context).contains(value);


    }

    private SharedPreferencesManager() {}

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
    }


    public static String getStringValue(Context context , String fetchedString) {
        return getSharedPreferences(context).getString(fetchedString , "");
    }

    public static void setStringValue(Context context, String key , String value) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(key , value);
        editor.apply();
    }


    public static float getFloatValue(Context context , String fetchedString) {
        return getSharedPreferences(context).getFloat(fetchedString , 0);
    }

    public static void setFloatValue(Context context, String key , float value) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putFloat(key , value);
        editor.apply();
    }

    public static boolean getBooleanValue(Context context , String fetchedString) {
        return getSharedPreferences(context).getBoolean(fetchedString , false);
    }

    public static void setBooleanValue(Context context, String key , boolean value) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(key , value);
        editor.apply();
    }

    public static void setIntValue(Context context, String key  , int value) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(key , value);
        editor.apply();
    }


    public static void removeValue(Context context, String key) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(key);
        editor.apply();
    }


    public static int getIntValue(Context context , String fetchedInt) {
        return getSharedPreferences(context).getInt(fetchedInt , 0);
    }

    public static void clearShared (Context context ) {
        getSharedPreferences(context).edit().clear().apply();

        SharedPreferencesManager.setIntValue(this,"age",15);

    }

    // other getters/setters

}
