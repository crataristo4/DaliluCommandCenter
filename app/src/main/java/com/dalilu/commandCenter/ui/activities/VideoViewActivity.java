package com.dalilu.commandCenter.ui.activities;

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
import com.danikula.videocache.HttpProxyCacheServer;

import java.util.Objects;

public class VideoViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityVideoViewBinding activityVideoViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_video_view);
        Intent getVideoIntent = getIntent();
        VideoView videoView = activityVideoViewBinding.videoContentPreview;

        runOnUiThread(() -> {
            if (getVideoIntent != null) {
                //load video
                HttpProxyCacheServer proxy = Dalilu.getProxy(this);
                String proxyUrl = proxy.getProxyUrl(getVideoIntent.getStringExtra(AppConstants.OBJECT_URL));
                videoView.setVideoPath(proxyUrl);

                MediaController mediaController = new MediaController(this);
                videoView.setMediaController(mediaController);
                mediaController.setAnchorView(videoView);

                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.BOTTOM;
                mediaController.setLayoutParams(lp);
                ((ViewGroup) mediaController.getParent()).removeView(mediaController);

                activityVideoViewBinding.controllerAnchor.addView(mediaController);

                if (Objects.equals(getVideoIntent.getBooleanExtra(AppConstants.IS_SOLVED, false), true)) {
                    activityVideoViewBinding.txtCheckBy.setText(R.string.chkkdd);

                } else {
                    activityVideoViewBinding.txtCheckBy.setText(R.string.notChkkdd);

                }
                activityVideoViewBinding.txtDate.setText(getVideoIntent.getStringExtra(AppConstants.TIME_STAMP));
                activityVideoViewBinding.txtLocation.setText(getVideoIntent.getStringExtra(AppConstants.KNOWN_LOCATION));
                activityVideoViewBinding.txtPostedBy.setText(java.text.MessageFormat.format("Posted by: {0}", getVideoIntent.getStringExtra(AppConstants.USER_NAME)));


            }
        });


    }
}