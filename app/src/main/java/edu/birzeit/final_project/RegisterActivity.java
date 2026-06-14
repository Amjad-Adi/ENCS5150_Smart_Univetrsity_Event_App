package edu.birzeit.final_project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText firstNameEditText = (EditText) findViewById(R.id.editText_FirstName);
        final EditText lastNameEditText = (EditText) findViewById(R.id.editText_LastName);
        final EditText emailEditText = (EditText) findViewById(R.id.editText_Email);
        final EditText phoneEditText = (EditText) findViewById(R.id.editText_Phone);
        final EditText passwordEditText = (EditText) findViewById(R.id.editText_Password);
        final EditText confirmPasswordEditText = (EditText) findViewById(R.id.editText_ConfirmPassword);
        final Spinner genderSpinner = (Spinner) findViewById(R.id.spinner_Gender);
        final Spinner categorySpinner = (Spinner) findViewById(R.id.spinner_Category);
        Button registerButton = (Button) findViewById(R.id.button_Register);
        Button backToLoginButton = (Button) findViewById(R.id.button_BackToLogin);

        // Gender spinner
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Select Gender", "Male", "Female"});
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        // Category spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Select Category", "Engineering", "Science",
                        "Business", "Arts", "Medicine", "Law", "Other"});
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = firstNameEditText.getText().toString().trim();
                String lastName = lastNameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                String gender = genderSpinner.getSelectedItem().toString();
                String category = categorySpinner.getSelectedItem().toString();

                // Validation
                if (firstName.isEmpty()) {
                    firstNameEditText.setError("First name is required");
                    return;
                }
                if (firstName.length() < 3) {
                    firstNameEditText.setError("First name must be at least 3 characters");
                    return;
                }
                if (lastName.isEmpty()) {
                    lastNameEditText.setError("Last name is required");
                    return;
                }
                if (lastName.length() < 3) {
                    lastNameEditText.setError("Last name must be at least 3 characters");
                    return;
                }
                if (email.isEmpty()) {
                    emailEditText.setError("Email is required");
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEditText.setError("Invalid email format");
                    return;
                }
                if (phone.isEmpty()) {
                    phoneEditText.setError("Phone number is required");
                    return;
                }
                if (gender.equals("Select Gender")) {
                    Toast.makeText(RegisterActivity.this,
                            "Please select a gender", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (category.equals("Select Category")) {
                    Toast.makeText(RegisterActivity.this,
                            "Please select a category", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.isEmpty()) {
                    passwordEditText.setError("Password is required");
                    return;
                }
                if (password.length() < 6) {
                    passwordEditText.setError("Password must be at least 6 characters");
                    return;
                }
                if (!password.matches(".*[a-zA-Z].*")) {
                    passwordEditText.setError("Password must contain at least 1 letter");
                    return;
                }
                if (!password.matches(".*[0-9].*")) {
                    passwordEditText.setError("Password must contain at least 1 number");
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    confirmPasswordEditText.setError("Passwords do not match");
                    return;
                }

                // Check if email already exists
                DataBaseHelper dataBaseHelper =
                        new DataBaseHelper(RegisterActivity.this, "SMART_EVENTS_DB", null, 1);

                if (dataBaseHelper.isEmailExists(email)) {
                    emailEditText.setError("This email is already registered");
                    return;
                }

                // Save user
                String hashedPassword = PasswordHasher.hash(password);
                User newUser = new User(email, firstName, lastName,
                        hashedPassword, gender, category, phone);
                dataBaseHelper.insertUser(newUser);

                Toast.makeText(RegisterActivity.this,
                        "Account created successfully!", Toast.LENGTH_LONG).show();

                // Go back to Login
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(intent);
                finish();
            }
        });

        backToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(intent);
                finish();
            }
        });
    }
}