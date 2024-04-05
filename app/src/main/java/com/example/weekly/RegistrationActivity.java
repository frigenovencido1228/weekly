package com.example.weekly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.weekly.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    TextInputEditText etEmail, etPassword, etConfirm, etName;
    MaterialButton btnCreate, btnLogin, btnDemo;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
    TextInputLayout tvPassword, tvConfirm, tvEmail, tvName;

    FirebaseAuth firebaseAuth;
Dialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        loadingDialog=Utils.createDialog(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirm = findViewById(R.id.etConfirm);
        etName = findViewById(R.id.etName);

        btnCreate = findViewById(R.id.btnCreate);
        btnLogin = findViewById(R.id.btnLogin);

        tvEmail = findViewById(R.id.tvEmail);
        tvPassword = findViewById(R.id.tvPassword);
        tvConfirm = findViewById(R.id.tvConfirm);
        tvName = findViewById(R.id.tvName);

        firebaseAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                finish();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirm = etConfirm.getText().toString().trim();
                String name = etName.getText().toString().trim();

                if (name.isEmpty()) {
                    tvName.setHelperText("Enter your name.");
                    return;
                }
                if (email.isEmpty() || !email.matches(emailPattern)) {
                    tvEmail.setHelperText("Email is not valid.");
                    return;
                }
                tvEmail.setHelperText("");
                if (password.isEmpty() || password.length() < 6) {
                    tvPassword.setHelperText("Passwords should be at least 6 characters.");
                    return;
                }
                tvPassword.setHelperText("");
                if (!confirm.equalsIgnoreCase(password)) {
                    tvConfirm.setHelperText("Passwords do not match.");
                    return;
                }
                tvConfirm.setHelperText("");
                loadingDialog.show();

                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String id = firebaseAuth.getCurrentUser().getUid();
                            User user = new User(name, id, email);

                            databaseReference.child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        loadingDialog.dismiss();
                                        Toast.makeText(RegistrationActivity.this, "Added", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));

                                        if (firebaseAuth != null) {
                                            firebaseAuth.signOut();
                                        }
                                        finish();
                                    } else {
                                        loadingDialog.dismiss();
                                        Toast.makeText(RegistrationActivity.this, "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loadingDialog.dismiss();
                                    Toast.makeText(RegistrationActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            loadingDialog.dismiss();
                            Toast.makeText(RegistrationActivity.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}