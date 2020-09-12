package com.dalilu.commandCenter.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import java.util.Locale;

public class LanguageManager {

    /**
     * For english locale
     */
    public static final String LANGUAGE_KEY_ENGLISH = "en";

    /**
     * for french locale
     */
    public static final String LANGUAGE_KEY_FRENCH = "fr";


    /**
     * SharedPreferences Key
     */
    private static final String LANGUAGE_KEY = "language_key";


    public static Context setLocale(Context mContext) {
        return updateResources(mContext, getLanguagePref(mContext));
    }


    public static void setNewLocale(Context mContext, String mLocaleKey) {
        setLanguagePref(mContext, mLocaleKey);
        updateResources(mContext, mLocaleKey);
    }

    /**
     * Get saved Locale from SharedPreferences
     *
     * @param mContext current context
     * @return current locale key by default return english locale
     */
    public static String getLanguagePref(Context mContext) {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        return mPreferences.getString(LANGUAGE_KEY, "");
    }


    private static void setLanguagePref(Context mContext, String localeKey) {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mPreferences.edit().putString(LANGUAGE_KEY, localeKey).apply();
    }

    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        context = context.createConfigurationContext(config);
        return context;
    }

}
