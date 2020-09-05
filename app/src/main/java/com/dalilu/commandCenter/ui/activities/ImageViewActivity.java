package com.dalilu.commandCenter.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.dalilu.commandCenter.R;
import com.dalilu.commandCenter.databinding.ActivityImageViewBinding;
import com.dalilu.commandCenter.utils.AppConstants;

public class ImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityImageViewBinding activityImageViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_view);
        Intent getImageIntent = getIntent();

        if (getImageIntent != null) {
            activityImageViewBinding.txtCheckBy.setText(R.string.chkkdd);
            activityImageViewBinding.txtDate.setText(getImageIntent.getStringExtra(AppConstants.DATE_TIME));
            activityImageViewBinding.txtLocation.setText(getImageIntent.getStringExtra(AppConstants.KNOWN_LOCATION));
            activityImageViewBinding.txtPostedBy.setText(java.text.MessageFormat.format("Posted by: {0}", getImageIntent.getStringExtra(AppConstants.USER_NAME)));
            Glide.with(this)
                    .load(getImageIntent.getStringExtra(AppConstants.OBJECT_URL))
                    .error(R.drawable.error)
                    .into(activityImageViewBinding.imgView);
        }
    }
}