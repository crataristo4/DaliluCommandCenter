package com.dalilu.commandCenter.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.dalilu.commandCenter.R;
import com.dalilu.commandCenter.clickhandler.ItemClickHandler;
import com.dalilu.commandCenter.databinding.ActivityRegisterPhoneNumberBinding;
import com.dalilu.commandCenter.utils.LanguageManager;


public class RegisterPhoneNumberActivity extends AppCompatActivity {
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


    }

}