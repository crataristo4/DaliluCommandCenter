package com.dalilu.commandCenter.auth;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;


import com.dalilu.commandCenter.R;
import com.dalilu.commandCenter.clickhandler.ItemClickHandler;
import com.dalilu.commandCenter.databinding.ActivityRegisterPhoneNumberBinding;
import com.dalilu.commandCenter.utils.LanguageManager;


public class RegisterPhoneNumberActivity extends AppCompatActivity {
    private static final String TAG = "Register Phone";
    ActivityRegisterPhoneNumberBinding activityRegisterPhoneNumberBinding;
    ItemClickHandler itemClickHandler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityRegisterPhoneNumberBinding = DataBindingUtil.setContentView(this, R.layout.activity_register_phone_number);
        itemClickHandler = new ItemClickHandler(this,
                activityRegisterPhoneNumberBinding.textInputLayoutPhone,
                activityRegisterPhoneNumberBinding.ccp,
                activityRegisterPhoneNumberBinding.pbLoading,
                activityRegisterPhoneNumberBinding.btnNext);

        activityRegisterPhoneNumberBinding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // DisplayViewUI.displayToast(RegisterPhoneNumberActivity.this, getString(R.string.selectLanguage));
                    activityRegisterPhoneNumberBinding.btnNext.setEnabled(false);

                } else if (position == 1) {//english is selected
                    activityRegisterPhoneNumberBinding.btnNext.setEnabled(true);
                    LanguageManager.setNewLocale(RegisterPhoneNumberActivity.this, LanguageManager.LANGUAGE_KEY_ENGLISH);
                    // TODO: 7/31/2020  fix app language
                    // recreate();

                } else if (position == 2) {//french is selected
                    activityRegisterPhoneNumberBinding.btnNext.setEnabled(true);
                    LanguageManager.setNewLocale(RegisterPhoneNumberActivity.this, LanguageManager.LANGUAGE_KEY_FRENCH);
                    // recreate();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        activityRegisterPhoneNumberBinding.setValidateNumber(itemClickHandler);



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    1);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: " + " granted");
            } else finish();
        }
    }

   /* private void getPhoneNumber() {

        String getPhoneNumber = Objects.requireNonNull(txtNumber.getEditText()).getText().toString();
        if (!getPhoneNumber.trim().isEmpty()) {
            getPhone = countryCodePicker.getFormattedFullNumber();
            Intent verifyNumberIntent = new Intent(RegisterPhoneNumberActivity.this, VerifyPhoneNumberActivity.class);
            verifyNumberIntent.putExtra(AppConstants.PHONE_NUMBER, getPhone);
            verifyNumberIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(verifyNumberIntent);

        } else if (getPhoneNumber.trim().isEmpty()) {
            txtNumber.setErrorEnabled(true);
            txtNumber.setError(getString(R.string.phoneReq));
        } else {
            txtNumber.setErrorEnabled(false);
        }
    }*/
}