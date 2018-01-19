package com.example.sj.erpadmin.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.sj.erpadmin.R;
import com.example.sj.erpadmin.utils.CommonUtils;
import com.example.sj.erpadmin.utils.DebugLog;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button signUpButton;
    private View.OnClickListener onSignUpClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String email = emailEditText.getText().toString().trim();
            if (CommonUtils.isEmpty(email)) {
                DebugLog.e("Email can't be empty!");
                emailEditText.setError(getString(R.string.msg_email_empty));
                emailEditText.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                DebugLog.e("Enter valid email!");
                emailEditText.setError(getString(R.string.msg_valid_email));
                emailEditText.requestFocus();
                return;
            }

            String password = passwordEditText.getText().toString().trim();
            if (CommonUtils.isEmpty(password)) {
                DebugLog.e("Password can't be empty!");
                passwordEditText.setError(getString(R.string.msg_passwod_empty));
                passwordEditText.requestFocus();
                return;
            }

            if (password.length() > 6 && password.length() < 15) {
                DebugLog.e("Password should be grater than 6 and less then 15 characters!");
                passwordEditText.setError(getString(R.string.msg_password_valid));
                passwordEditText.requestFocus();
                return;
            }

            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            if (CommonUtils.isEmpty(confirmPassword)) {
                DebugLog.e("Confirm password can't be empty!");
                confirmPasswordEditText.setError(getString(R.string.msg_confirm_password_empty));
                confirmPasswordEditText.requestFocus();
                return;
            }

            if (confirmPassword.equals(password)) {
                DebugLog.e("Not matched with password, check and re-enter!");
                confirmPasswordEditText.setError(getString(R.string.msg_mismatched_confirm_password));
                confirmPasswordEditText.requestFocus();
                return;
            }

            registerUser(email, password);
        }
    };

    private void registerUser(String email, String password) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        emailEditText = findViewById(R.id.edit_text_email);
        passwordEditText = findViewById(R.id.edit_text_password);
        confirmPasswordEditText = findViewById(R.id.edit_text_confirm_password);
        signUpButton = findViewById(R.id.button_sign_up);
        signUpButton.setOnClickListener(onSignUpClickListener);
    }
}
