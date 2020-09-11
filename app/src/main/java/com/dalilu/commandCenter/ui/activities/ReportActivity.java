package com.dalilu.commandCenter.ui.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.dalilu.commandCenter.R;
import com.dalilu.commandCenter.databinding.ActivityReportBinding;
import com.dalilu.commandCenter.utils.AppConstants;
import com.dalilu.commandCenter.utils.CameraUtils;
import com.dalilu.commandCenter.utils.DisplayViewUI;
import com.dalilu.commandCenter.utils.GetTimeAgo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ReportActivity extends AppCompatActivity {

    private static String imageStoragePath;
    ActivityReportBinding activityReportBinding;
    private StorageReference filePath;
    private Uri uri = null;
    private ProgressDialog pd;
    private CollectionReference alertCollectionReference;
    private String knownName, state, country, phoneNumber, userId, userName, userPhotoUrl;
    private double latitude, longitude;
    private ImageView imgPhoto;
    private VideoView videoView;
    private Button btnUpload;
    // key to store image path in savedInstance state
    public static final String KEY_IMAGE_STORAGE_PATH = "image_path";
    // Bitmap sampling size
    public static final int BITMAP_SAMPLE_SIZE = 8;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityReportBinding = DataBindingUtil.setContentView(this, R.layout.activity_report);
        setSupportActionBar(activityReportBinding.toolBarReport);


        StorageReference imageStorageRef = FirebaseStorage.getInstance().getReference().child("alerts");
        filePath = imageStorageRef.child(UUID.randomUUID().toString());
        alertCollectionReference = FirebaseFirestore.getInstance().collection("Alerts");
        imgPhoto = activityReportBinding.imgAlertPhoto;
        videoView = activityReportBinding.videoView;
        btnUpload = activityReportBinding.btnUpload;

        Intent getUserDetailsIntent = getIntent();
        if (getUserDetailsIntent != null) {
            userName = getUserDetailsIntent.getStringExtra(AppConstants.USER_NAME);
            userPhotoUrl = getUserDetailsIntent.getStringExtra(AppConstants.USER_PHOTO_URL);
            userId = getUserDetailsIntent.getStringExtra(AppConstants.UID);
            phoneNumber = getUserDetailsIntent.getStringExtra(AppConstants.PHONE_NUMBER);
            state = getUserDetailsIntent.getStringExtra(AppConstants.STATE);
            country = getUserDetailsIntent.getStringExtra(AppConstants.COUNTRY);
            knownName = getUserDetailsIntent.getStringExtra(AppConstants.KNOWN_LOCATION);
            latitude = getUserDetailsIntent.getDoubleExtra(AppConstants.LATITUDE, 0);
            longitude = getUserDetailsIntent.getDoubleExtra(AppConstants.LONGITUDE, 0);

        }

        // Checking availability of the camera
        if (!CameraUtils.isDeviceSupportCamera(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device doesn't have camera
            finish();
        }

        activityReportBinding.fabCamera.setOnClickListener(v -> {
            if (CameraUtils.checkPermissions(v.getContext())) {
                captureImage();
            } else {
                requestCameraPermission(AppConstants.MEDIA_TYPE_IMAGE);
            }
        });


        activityReportBinding.fabVideo.setOnClickListener(v -> {
            if (CameraUtils.checkPermissions(v.getContext())) {
                captureVideo();
            } else {
                requestCameraPermission(AppConstants.MEDIA_TYPE_VIDEO);
            }
        });


        // restoring storage image path from saved instance state
        // otherwise the path will be null on device rotation
        restoreFromBundle(savedInstanceState);


    }


    /**
     * Capturing Camera Image will launch camera app requested image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File file = CameraUtils.getOutputMediaFile(AppConstants.MEDIA_TYPE_IMAGE);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }

        Uri fileUri = CameraUtils.getOutputMediaFileUri(ReportActivity.this, file);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, AppConstants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Launching camera app to record video
     */
    private void captureVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        File file = CameraUtils.getOutputMediaFile(AppConstants.MEDIA_TYPE_VIDEO);
        if (file != null) {
            imageStoragePath = file.getAbsolutePath();
        }

        Uri fileUri = CameraUtils.getOutputMediaFileUri(ReportActivity.this, file);

        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
        intent.putExtra(String.valueOf(MediaRecorder.VideoEncoder.MPEG_4_SP), 1);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file

        // start the video capture Intent
        startActivityForResult(intent, AppConstants.CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    /**
     * Requesting permissions using Dexter library
     */
    private void requestCameraPermission(final int type) {

        if (type == AppConstants.MEDIA_TYPE_IMAGE) {
            Dexter.withContext(ReportActivity.this)
                    .withPermissions(Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {

                                captureImage();
                               /* if (type == AppConstants.MEDIA_TYPE_IMAGE) {
                                    // capture picture
                                    captureImage();
                                } else {
                                    captureVideo();
                                }*/

                            } else if (report.isAnyPermissionPermanentlyDenied()) {
                                showPermissionsAlert();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
        } else if (type == AppConstants.MEDIA_TYPE_VIDEO) {

            Dexter.withContext(ReportActivity.this)
                    .withPermissions(Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {

                                captureVideo();

                               /* if (type == AppConstants.MEDIA_TYPE_IMAGE) {
                                    // capture picture
                                    captureImage();
                                } else {
                                }*/

                            } else if (report.isAnyPermissionPermanentlyDenied()) {
                                showPermissionsAlert();
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();

        }


    }

    /**
     * Alert dialog to navigate to app settings
     * to enable necessary permissions
     */
    private void showPermissionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
        builder.setTitle(R.string.perRequired)
                .setMessage(R.string.camPerm)
                .setPositiveButton(R.string.goToSettings, (dialog, which) -> CameraUtils.openSettings(ReportActivity.this))
                .setNegativeButton(R.string.cancel, (dialog, which) -> {

                }).show();
    }

    private void uploadToServer(Uri imageUri, String type) {
        pd.show();
        StringBuilder address = new StringBuilder();
        address.append(knownName).append(",").append(state).append(",").append(country);
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:MM a");
        String dateReported = dateFormat.format(Calendar.getInstance().getTime());

        //upload photo to server
        filePath.putFile(imageUri).continueWithTask(task -> {
            if (!task.isSuccessful()) {
                pd.dismiss();

            }

            Log.i("TAG", "fromTask: " + filePath.getDownloadUrl());
            return filePath.getDownloadUrl();

        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                Uri downLoadUri = task.getResult();
                assert downLoadUri != null;
                String url = downLoadUri.toString();
                Log.i("TAG", "onComplete: " + url);


                Map<String, Object> alertItems = new HashMap<>();
                alertItems.put("userName", userName);
                alertItems.put("userPhotoUrl", userPhotoUrl);
                alertItems.put("url", url);
                alertItems.put(type, type);
                alertItems.put("coordinates", new GeoPoint(latitude, longitude));
                alertItems.put("address", address.toString());
                alertItems.put("userId", userId);
                alertItems.put("phoneNumber", phoneNumber);
                alertItems.put("timeStamp", GetTimeAgo.getTimeInMillis());
                alertItems.put("dateReported", dateReported);
                alertItems.put("isSolved", false);


                //fire store cloud store
                alertCollectionReference.add(alertItems).addOnCompleteListener(task2 -> {

                    if (task2.isSuccessful()) {
                        String id = MainActivity.userId;

                        pd.dismiss();
                        DisplayViewUI.displayToast(ReportActivity.this, getString(R.string.reportSuccess));

                        startActivity(new Intent(ReportActivity.this, MainActivity.class)
                              /*  .putExtra(AppConstants.UID, id)
                                .putExtra(AppConstants.USER_NAME, userName)
                                .putExtra(AppConstants.USER_PHOTO_URL, userPhotoUrl)
                                .putExtra(AppConstants.PHONE_NUMBER, phoneNumber)*/


                        );
                        finish();


                    } else {
                        pd.dismiss();
                        DisplayViewUI.displayToast(this, Objects.requireNonNull(task2.getException()).getMessage());

                    }

                });

            } else {
                pd.dismiss();
                DisplayViewUI.displayToast(ReportActivity.this, Objects.requireNonNull(task.getException()).getMessage());

            }

        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppConstants.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

//todo change the name of the images uploaded to the server

                // Refreshing the gallery
                CameraUtils.refreshGallery(ReportActivity.this, imageStoragePath);
                previewCapturedImage();
                //CameraUtils.optimizeBitmap(10,imageStoragePath);
                uri = CameraUtils.getOutputMediaFileUri(ReportActivity.this, new File(imageStoragePath));
                imgPhoto.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.GONE);
                Glide.with(ReportActivity.this).load(uri).into(activityReportBinding.imgAlertPhoto);


                btnUpload.setOnClickListener(view -> {
                    //display loading
                    pd = DisplayViewUI.displayProgress(ReportActivity.this, getString(R.string.uploadingImage));

//upload to server
                    uploadToServer(uri, "image");
                });


            } else if (resultCode == Activity.RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(ReportActivity.this,
                        R.string.captureCanceled, Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(ReportActivity.this,
                        R.string.failedToCapture, Toast.LENGTH_SHORT)
                        .show();
            }
        } else if (requestCode == AppConstants.CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(ReportActivity.this, imageStoragePath);

                uri = CameraUtils.getOutputMediaFileUri(ReportActivity.this, new File(imageStoragePath));
                previewVideo();

                videoView.setVisibility(View.VISIBLE);
                imgPhoto.setVisibility(View.GONE);
                videoView.setVideoPath(imageStoragePath);
                videoView.start();

                btnUpload.setOnClickListener(view -> {
                    //display loading
                    pd = DisplayViewUI.displayProgress(ReportActivity.this, getString(R.string.uploadingVideo));

                    //upload to server
                    uploadToServer(uri, "video");

                });


            } else if (resultCode == Activity.RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(ReportActivity.this,
                        R.string.vidRecCanceled, Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(ReportActivity.this,
                        R.string.sorryVidFailed, Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /**
     * Display image from gallery
     */
    private void previewCapturedImage() {
        try {
            // hide video preview
            imgPhoto.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);

            Bitmap bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, imageStoragePath);

            imgPhoto.setImageBitmap(bitmap);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displaying video in VideoView
     */
    private void previewVideo() {
        try {
            videoView.setVisibility(View.VISIBLE);
            imgPhoto.setVisibility(View.GONE);
            videoView.setVideoPath(imageStoragePath);
            videoView.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Restoring store image path from saved instance state
     */
    private void restoreFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(KEY_IMAGE_STORAGE_PATH)) {
                imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH);
                if (!TextUtils.isEmpty(imageStoragePath)) {
                    if (imageStoragePath.substring(imageStoragePath.lastIndexOf(".")).equals("." + AppConstants.IMAGE_EXTENSION)) {
                        previewCapturedImage();
                    } else if (imageStoragePath.substring(imageStoragePath.lastIndexOf(".")).equals("." + AppConstants.VIDEO_EXTENSION)) {
                        previewVideo();
                    }
                }
            }
        }
    }


    /**
     * Saving stored image path to saved instance state
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putString(KEY_IMAGE_STORAGE_PATH, imageStoragePath);
    }

    /**
     * Restoring image path from saved instance state
     */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        imageStoragePath = savedInstanceState.getString(KEY_IMAGE_STORAGE_PATH);
    }

}