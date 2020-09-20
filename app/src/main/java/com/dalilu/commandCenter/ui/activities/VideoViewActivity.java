package com.dalilu.commandCenter.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.dalilu.commandCenter.Dalilu;
import com.dalilu.commandCenter.R;
import com.dalilu.commandCenter.databinding.ActivityVideoViewBinding;
import com.dalilu.commandCenter.utils.AppConstants;
import com.dalilu.commandCenter.utils.DisplayViewUI;
import com.dalilu.commandCenter.utils.GetTimeAgo;
import com.danikula.videocache.HttpProxyCacheServer;

import java.text.MessageFormat;
import java.util.Objects;

public class VideoViewActivity extends AppCompatActivity {
    String objectId;
    String proxyUrl;
    VideoView videoView;
    MediaController mediaController;
    FrameLayout.LayoutParams lp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityVideoViewBinding activityVideoViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_video_view);
        Intent getVideoIntent = getIntent();
        videoView = activityVideoViewBinding.videoContentPreview;

        runOnUiThread(() -> {
            if (getVideoIntent != null) {
                //load video
                HttpProxyCacheServer proxy = Dalilu.getProxy(this);
                proxyUrl = proxy.getProxyUrl(getVideoIntent.getStringExtra(AppConstants.OBJECT_URL));
                videoView.setVideoPath(proxyUrl);

                mediaController = new MediaController(this);
                videoView.setMediaController(mediaController);
                mediaController.setAnchorView(videoView);

                lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.BOTTOM;
                mediaController.setLayoutParams(lp);
                ((ViewGroup) mediaController.getParent()).removeView(mediaController);

                activityVideoViewBinding.controllerAnchor.addView(mediaController);

                if (Objects.equals(getVideoIntent.getBooleanExtra(AppConstants.IS_SOLVED, false), true)) {
                    activityVideoViewBinding.txtCheckBy.setText(R.string.chkkdd);

                } else {
                    activityVideoViewBinding.txtCheckBy.setText(R.string.notChkkdd);

                }
                activityVideoViewBinding.txtDate.setText(GetTimeAgo.getTimeAgo(getVideoIntent.getLongExtra(AppConstants.TIME_STAMP, 0)));
                activityVideoViewBinding.txtLocation.setText(getVideoIntent.getStringExtra(AppConstants.KNOWN_LOCATION));
                activityVideoViewBinding.txtPostedBy.setText(MessageFormat.format(getString(R.string.pstBBY), getVideoIntent.getStringExtra(AppConstants.USER_NAME)));

                objectId = getVideoIntent.getStringExtra(AppConstants.UID);

            }
        });

        activityVideoViewBinding.btnDelete.setOnClickListener(view -> DisplayViewUI.displayAlertDialogDelete(
                this,
                getString(R.string.rmRpt),
                getString(R.string.thisRpt),
                getString(R.string.no),
                getString(R.string.yes),
                (dialogInterface, i) -> {
                    switch (i) {
                        case -2:
                            dialogInterface.dismiss();
                            break;
                        case -1:
                            //delete item
                            ProgressDialog loading = DisplayViewUI.displayProgress(VideoViewActivity.this, getString(R.string.plsRpt));
                            loading.show();

                            ReportActivity.alertCollectionReference.document(objectId)
                                    .delete()
                                    .addOnCompleteListener(VideoViewActivity.this, task -> {
                                        if (task.isSuccessful()) {
                                            loading.dismiss();
                                            DisplayViewUI.displayToast(VideoViewActivity.this, getString(R.string.rptDel));

                                            gotoMain();
                                        } else {
                                            loading.dismiss();
                                            DisplayViewUI.displayToast(VideoViewActivity.this, Objects.requireNonNull(task.getException()).getMessage());
                                        }


                                    });

                            break;
                    }
                }
        ));


    }


    void gotoMain() {
        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}