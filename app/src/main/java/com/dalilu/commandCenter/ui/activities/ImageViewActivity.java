package com.dalilu.commandCenter.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.dalilu.commandCenter.R;
import com.dalilu.commandCenter.databinding.ActivityImageViewBinding;
import com.dalilu.commandCenter.utils.AppConstants;
import com.dalilu.commandCenter.utils.DisplayViewUI;

import java.util.Objects;

public class ImageViewActivity extends AppCompatActivity {
    String objectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityImageViewBinding activityImageViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_view);
        Intent getImageIntent = getIntent();

        if (getImageIntent != null) {
            if (Objects.equals(getImageIntent.getBooleanExtra(AppConstants.IS_SOLVED, false), true)) {
                activityImageViewBinding.txtCheckBy.setText(R.string.chkkdd);

            } else {
                activityImageViewBinding.txtCheckBy.setText(R.string.notChkkdd);

            }
            activityImageViewBinding.txtDate.setText(getImageIntent.getStringExtra(AppConstants.DATE_TIME));
            activityImageViewBinding.txtLocation.setText(getImageIntent.getStringExtra(AppConstants.KNOWN_LOCATION));
            activityImageViewBinding.txtPostedBy.setText(java.text.MessageFormat.format("Posted by: {0}", getImageIntent.getStringExtra(AppConstants.USER_NAME)));
            Glide.with(this)
                    .load(getImageIntent.getStringExtra(AppConstants.OBJECT_URL))
                    .error(R.drawable.error)
                    .into(activityImageViewBinding.imgView);

            objectId = getImageIntent.getStringExtra(AppConstants.UID);

        }


        activityImageViewBinding.btnDelete.setOnClickListener(view -> DisplayViewUI.displayAlertDialogDelete(
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
                            ProgressDialog loading = DisplayViewUI.displayProgress(ImageViewActivity.this, getString(R.string.plsRpt));
                            loading.show();

                            ReportActivity.alertCollectionReference.document(objectId)
                                    .delete()
                                    .addOnCompleteListener(ImageViewActivity.this, task -> {
                                        if (task.isSuccessful()) {
                                            loading.dismiss();
                                            DisplayViewUI.displayToast(ImageViewActivity.this, getString(R.string.rptDel));

                                            gotoMain();
                                        } else {
                                            loading.dismiss();
                                            DisplayViewUI.displayToast(ImageViewActivity.this, Objects.requireNonNull(task.getException()).getMessage());
                                        }


                                    });

                            break;
                    }
                }
        ));
    }


    @Override
    public void onBackPressed() {
        gotoMain();
    }

    void gotoMain() {
        startActivity(new Intent(this, MainActivity.class));
        finishAffinity();
    }
}