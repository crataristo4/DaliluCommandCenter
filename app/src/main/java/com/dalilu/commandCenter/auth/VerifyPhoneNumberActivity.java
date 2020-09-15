package com.dalilu.commandCenter.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.databinding.DataBindingUtil;

import com.dalilu.commandCenter.R;
import com.dalilu.commandCenter.databinding.ActivityVerifyPhoneNumberBinding;
import com.dalilu.commandCenter.ui.activities.BaseActivity;
import com.dalilu.commandCenter.ui.activities.MainActivity;
import com.dalilu.commandCenter.utils.AppConstants;
import com.dalilu.commandCenter.utils.DisplayViewUI;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneNumberActivity extends BaseActivity {
    private static String code = "";
    ActivityVerifyPhoneNumberBinding activityVerifyPhoneNumberBinding;
    private FirebaseAuth mAuth;
    AppCompatEditText edtCode;
    String phoneNumber;
    String imageUrl;
    private CollectionReference commandCenterRef;
    private String uid;
    private static final String TAG = "VerifyPhone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityVerifyPhoneNumberBinding = DataBindingUtil.setContentView(this, R.layout.activity_verify_phone_number);
        mAuth = FirebaseAuth.getInstance();

        commandCenterRef = FirebaseFirestore.getInstance().collection("Command Center");


        Intent intent = getIntent();
        if (intent != null) {

            phoneNumber = intent.getStringExtra(AppConstants.PHONE_NUMBER);
            activityVerifyPhoneNumberBinding.phone.setText(phoneNumber);
        }

        edtCode = activityVerifyPhoneNumberBinding.code;

        edtCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 6) {

                    try {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(code, s.toString());
                        mAuth.signInWithCredential(credential).addOnSuccessListener(authResult -> {

                            uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                            gotoFinishAccount(uid);

                        }).addOnFailureListener(e -> DisplayViewUI.displayToast(VerifyPhoneNumberActivity.this, e.getMessage()));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        });


        try {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this,
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                            mAuth.signInWithCredential(credential).addOnCompleteListener(VerifyPhoneNumberActivity.this,
                                    task -> {
                                        uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                        gotoFinishAccount(uid);
                                    });
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            DisplayViewUI.displayToast(VerifyPhoneNumberActivity.this, e.getMessage());
                        }

                        @Override
                        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            super.onCodeSent(s, forceResendingToken);
                            code = s;
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    void gotoFinishAccount(String id) {
        imageUrl = "https://firebasestorage.googleapis.com/v0/b/dalilu-app.appspot.com/o/command%20center%2Fcc.jpg?alt=media&token=cc2872f4-7423-4198-a8f6-7884ce7c0064";
        String userName = "Command Center";

        Map<String, Object> accountInfo = new HashMap<>();
        accountInfo.put("userPhotoUrl", imageUrl);
        accountInfo.put("userName", userName);
        accountInfo.put("phoneNumber", phoneNumber);
        accountInfo.put("userId", id);

        commandCenterRef.document(uid).set(accountInfo).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {

                Intent intent = new Intent(VerifyPhoneNumberActivity.this, MainActivity.class);
               /* intent.putExtra(AppConstants.PHONE_NUMBER, phoneNumber);
                intent.putExtra(AppConstants.UID, id);
                intent.putExtra(AppConstants.USER_NAME, userName);
                intent.putExtra(AppConstants.USER_PHOTO_URL, imageUrl);*/
                startActivity(intent);
                this.finishAffinity();

                Log.i(TAG, "onCreate: " + userName + userPhotoUrl + uid + phoneNumber);

            } else {
                DisplayViewUI.displayToast(VerifyPhoneNumberActivity.this, Objects.requireNonNull(task.getException()).getMessage());
            }

        });


    }

    public void wrongNumber(View view) {

        this.finishAffinity();
        startActivity(new Intent(this, RegisterPhoneNumberActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));

    }

    @Override
    public void onBackPressed() {

    }
}