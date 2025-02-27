package com.example.trainhero.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.Navigation;

import com.example.trainhero.R;
import com.example.trainhero.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
    }

    public void login(View view) {

        EditText emailEditText = findViewById(R.id.email_login);
        EditText passwordEditText = findViewById(R.id.password_login);

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Email cannot be empty");
            emailEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password cannot be empty");
            passwordEditText.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                            Navigation.findNavController(view).navigate(R.id.action_fragmentLogin_to_homePageFragment);

                        } else {
                            Toast.makeText(MainActivity.this, "Login FAILED", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    public boolean register() {
        EditText emailEditText = findViewById(R.id.email_register);
        EditText passwordEditText = findViewById(R.id.password_register);
        EditText phoneEditText = findViewById(R.id.phone_register);
        EditText nameEditText = findViewById(R.id.name_register);

        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();

        if (name.isEmpty()) {
            nameEditText.setError("Name is required");
            showToast("Name is required");
            return false;
        }

        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            showToast("Email is required");
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email");
            showToast("Enter a valid email");
            return false;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            showToast("Password is required");
            return false;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            showToast("Password must be at least 6 characters");
            return false;
        }

        if (phone.isEmpty()) {
            phoneEditText.setError("Phone number is required");
            showToast("Phone number is required");
            return false;
        }

        if (!phone.matches("\\d{10}")) {
            phoneEditText.setError("Enter a valid phone number");
            showToast("Enter a valid phone number");
            return false;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();
                            addData(uid, email, phone, name);
                            showToast("Registration Successful");
                        } else {
                            showToast("Registration FAILED");
                        }
                    }
                });

        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    public void addData(String uid, String email, String phone, String name) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(uid);

        User user = new User(phone, email, name);
        myRef.setValue(user);
    }
}