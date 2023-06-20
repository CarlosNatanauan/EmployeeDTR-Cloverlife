package com.mobiledev.employeedtr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import ir.waspar.persianedittext.PersianEditText;
import com.example.myloadingbutton.MyLoadingButton;



import java.util.concurrent.Executor;

public class Login extends AppCompatActivity {

    PersianEditText username, password;

    MyLoadingButton myLoadingButton, myLoadingButtonFingerprint;

    private CheckBox checkBoxKeepMeLoggedIn;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);





        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        checkBoxKeepMeLoggedIn = findViewById(R.id.checkBoxKeepMeLoggedIn);

        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Check if the checkbox was previously selected
        boolean isChecked = sharedPreferences.getBoolean("KEEP_ME_LOGGED_IN", false);
        checkBoxKeepMeLoggedIn.setChecked(isChecked);

        // If checkbox was previously selected, autofill the EditTexts
        if (isChecked) {
            String savedUsername = sharedPreferences.getString("USERNAME", "");
            String savedPassword = sharedPreferences.getString("PASSWORD", "");
            username.setText(savedUsername);
            password.setText(savedPassword);
        }

        checkBoxKeepMeLoggedIn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (isChecked) {
                    editor.putString("USERNAME", username.getText().toString());
                    editor.putString("PASSWORD", password.getText().toString());
                } else {
                    // Clear the saved credentials when the checkbox is unchecked
                    editor.remove("USERNAME");
                    editor.remove("PASSWORD");
                    username.setText("");
                    password.setText("");
                }
                editor.putBoolean("KEEP_ME_LOGGED_IN", isChecked);
                editor.apply();
            }
        });

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        myLoadingButton = findViewById(R.id.my_loading_button_login);
        myLoadingButton.setMyButtonClickListener(new MyLoadingButton.MyLoadingButtonClick() {
            @Override
            public void onMyLoadingButtonClick() {
                // Call the authentication method when myLoadingButton is clicked
                authenticateWithCredentials();
            }
        });

        myLoadingButtonFingerprint = findViewById(R.id.my_loading_button_fingerprint);
        myLoadingButtonFingerprint.setMyButtonClickListener(new MyLoadingButton.MyLoadingButtonClick() {
            @Override
            public void onMyLoadingButtonClick() {
                // Call the authentication method when myLoadingButtonFingerprint is clicked
                authenticateWithFingerprint();
            }
        });

        setLoadingButtonStyle(myLoadingButton);
        setLoadingButtonStyle2(myLoadingButtonFingerprint);
    }

    private void setLoadingButtonStyle(MyLoadingButton button){
        button.setAnimationDuration(1000)
                .setButtonColor(R.color.colorAccent)
                .setButtonLabel("Login")
                .setButtonLabelSize(20)
                .setProgressLoaderColor(Color.WHITE)
                .setButtonLabelColor(R.color.white);
    }
    private void setLoadingButtonStyle2(MyLoadingButton button){
        button.setAnimationDuration(1000)
                .setButtonColor(R.color.colorAccent)
                .setButtonLabel("Biometrics")
                .setButtonLabelSize(20)
                .setProgressLoaderColor(Color.WHITE)
                .setButtonLabelColor(R.color.white);
    }

    private void authenticateWithCredentials() {
        // Show the loading animation
        myLoadingButton.showLoadingButton();

        // Validate the user credentials
        if (validateUser(username.getText().toString(), password.getText().toString())) {
            // If validation is successful, show the done animation
            myLoadingButton.showDoneButton();

            // If the "keep me logged in" checkbox is checked, save the credentials
            if (checkBoxKeepMeLoggedIn.isChecked()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("USERNAME", username.getText().toString());
                editor.putString("PASSWORD", password.getText().toString());
                editor.apply();
            }

            // Delay the start of the new activity to show the done animation
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(Login.this, Homepage.class);
                    startActivity(intent);
                }
            }, 1000); // 1000ms delay
        } else {
            // If validation fails, show the error animation
            myLoadingButton.showErrorButton();

            // Display the error toast
            Toast.makeText(Login.this, "Invalid username or password", Toast.LENGTH_SHORT).show();

            // Reset the button to original state
            resetButtonToOriginal(myLoadingButton);
        }
    }

    private void resetButtonToOriginal(MyLoadingButton button) {
        button.showNormalButton();
    }

    private boolean validateUser(String username, String password) {
        return "carlos".equals(username) && "idol123".equals(password);
    }

    private void authenticateWithFingerprint() {
        Executor executor = ContextCompat.getMainExecutor(this);

        myLoadingButtonFingerprint.showLoadingButton();

        BiometricPrompt biometricPrompt = new BiometricPrompt(Login.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                myLoadingButtonFingerprint.showErrorButton();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resetButtonToOriginal(myLoadingButtonFingerprint);
                    }
                }, 1000); // 1000ms delay
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                myLoadingButtonFingerprint.showDoneButton();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(Login.this, Homepage.class);
                        startActivity(intent);
                    }
                }, 1000); // 1000ms delay
            }
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                myLoadingButtonFingerprint.showErrorButton();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resetButtonToOriginal(myLoadingButtonFingerprint);
                    }
                }, 1000); // 1000ms delay
                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login with your Biometrics")
                .setNegativeButtonText("USE FORM")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    public void onBackPressed() {
    }
}
