package com.dalilu.commandCenter.utils;


import com.dalilu.commandCenter.R;

public final class AppConstants {
    public static final int VIDEO_TYPE = 0;
    public static final int IMAGE_TYPE = 1;
    public static final int AUDIO_TYPE = 2;
    public static final int TEXT_TYPE = 1;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final String UID = "uid";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String USER_NAME = "userName";
    public static final String STATE = "state";
    public static final String COUNTRY = "country";
    public static final String KNOWN_LOCATION = "known location";
    public static final String USER_PHOTO_URL = "userPhotoUrl";
    public static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    public static final String CACHE_UTILS = "CacheUtils";
    public static final String LATITUDE = "lat";
    public static final String LONGITUDE = "lng";

    /**
     * Constant used in the location settings dialog.
     */
    public static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    public final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    public final static String KEY_LOCATION = "location";
    public final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";


    // Gallery directory name to store the images or videos
    public static final String GALLERY_DIRECTORY_NAME = String.valueOf(R.string.app_name);

    // Image and Video file extensions
    public static final String IMAGE_EXTENSION = "jpg";
    public static final String VIDEO_EXTENSION = "mp4";

    public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

}
