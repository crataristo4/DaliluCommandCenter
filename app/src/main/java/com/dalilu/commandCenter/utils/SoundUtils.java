package com.dalilu.commandCenter.utils;

import android.app.Activity;

import androidx.annotation.NonNull;

import java.io.File;

public class SoundUtils {

    @NonNull
    public static File getRecordDirectory(Activity activity) {
        File recordDir = new File(activity.getCacheDir(), "record");
        if (!recordDir.exists()) {
            recordDir.mkdir();
        }
        return recordDir;
    }

}
