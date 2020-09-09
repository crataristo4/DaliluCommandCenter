package com.dalilu.commandCenter.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.dalilu.commandCenter.ui.activities.BaseActivity;
import com.dalilu.commandCenter.ui.activities.MainActivity;
import com.dalilu.commandCenter.utils.AppConstants;
import com.dalilu.commandCenter.utils.DisplayViewUI;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends BaseActivity {

    final String phoneNumber = "+16505554572";
    final String smsCode = "123456";
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String uid;
    //  ProgressBar loading;
    private CollectionReference commandCenterRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        commandCenterRef = FirebaseFirestore.getInstance().collection("Command Center");

        new Handler().postDelayed(this::configureFakenUMBER, 5000);


    }

    private void configureFakenUMBER() {
        // Configure faking the auto-retrieval with the whitelisted numbers.
        FirebaseAuthSettings firebaseAuthSettings = firebaseAuth.getFirebaseAuthSettings();

        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phoneNumber, smsCode);

        PhoneAuthProvider phoneAuthProvider = PhoneAuthProvider.getInstance();
        phoneAuthProvider.verifyPhoneNumber(
                phoneNumber,
                60L,
                TimeUnit.SECONDS,
                this, //activity *//*
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        // Instant verification is applied and a credential is directly returned.
                        // ...
                        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
//                                loading.setVisibility(View.GONE);

                                user = firebaseAuth.getCurrentUser();
                                assert user != null;
                                uid = user.getUid();

                                String imageUrl = "https://firebasestorage.googleapis.com/v0/b/dalilu-app.appspot.com/o/command%20center%2Fcc.jpg?alt=media&token=cc2872f4-7423-4198-a8f6-7884ce7c0064";
                                String userName = "Command Center";

                                Map<String, Object> accountInfo = new HashMap<>();
                                accountInfo.put("userPhotoUrl", imageUrl);
                                accountInfo.put("userName", userName);
                                accountInfo.put("phoneNumber", phoneNumber);
                                accountInfo.put("userId", uid);

                                commandCenterRef.document(uid).set(accountInfo);

                                Intent intent = new Intent(PhoneAuthActivity.this, MainActivity.class);
                                intent.putExtra(AppConstants.UID, uid);
                                intent.putExtra(AppConstants.PHONE_NUMBER, phoneNumber);
                                intent.putExtra(AppConstants.USER_NAME, userName);
                                intent.putExtra(AppConstants.USER_PHOTO_URL, imageUrl);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();


                            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                //loading.setVisibility(View.GONE);
                                DisplayViewUI.displayToast(PhoneAuthActivity.this, task.getException().getMessage());
                            }

                        });

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                });

    }


}