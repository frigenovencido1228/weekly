package com.example.weekly;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegistrationActivity extends AppCompatActivity {

    TextInputEditText etEmail, etPassword, etConfirm;
    MaterialButton btnCreate, btnLogin;

    TextInputLayout tvPassword, tvConfirm, tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirm = findViewById(R.id.etConfirm);

        btnCreate = findViewById(R.id.btnCreate);
        btnLogin = findViewById(R.id.btnLogin);

        tvEmail = findViewById(R.id.tvEmail);
        tvPassword = findViewById(R.id.tvPassword);
        tvConfirm = findViewById(R.id.tvConfirm);

        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if ((etEmail.getText().toString().trim().matches(emailPattern)) && etEmail.getText().toString().trim().length() > 0) {
                    btnCreate.setEnabled(true);
                    tvEmail.setHelperText("");
                } else {
                    tvEmail.setHelperText("Email is not valid.");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (etPassword.getText().toString().length() < 6) {
                    tvPassword.setHelperText("Passwords should be more than 6 characters");
                    btnCreate.setEnabled(false);
                    return;
                }
                if (!etConfirm.getText().toString().toString().equals(etPassword.getText().toString())) {
                    btnCreate.setEnabled(false);
                    tvPassword.setHelperText("");
                    tvConfirm.setHelperText("Passwords do not match.");
                    return;
                }
                btnCreate.setEnabled(true);
                tvPassword.setHelperText("");
                tvConfirm.setHelperText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etPassword.getText().toString().length() < 6) {
                    tvPassword.setHelperText("Passwords should be more than 6 characters");
                    btnCreate.setEnabled(false);
                    return;
                }
                if (!etConfirm.getText().toString().toString().equals(etPassword.getText().toString())) {
                    tvConfirm.setHelperText("Passwords do not match.");
                    btnCreate.setEnabled(false);
                    return;
                }
                if (etEmail.getText().toString().isEmpty()) {
                    btnCreate.setEnabled(false);
                    tvEmail.setHelperText("Enter your email.");
                    tvConfirm.setHelperText("");
                    return;

                }
                btnCreate.setEnabled(true);
                tvPassword.setHelperText("");
                tvConfirm.setHelperText("");

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegistrationActivity.this, "Ok", Toast.LENGTH_SHORT).show();
            }
        });
    }
}