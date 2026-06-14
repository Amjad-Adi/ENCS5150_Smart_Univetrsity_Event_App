package edu.birzeit.final_project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final SharedPreferences sharedPreferences =
                getSharedPreferences("app_prefs", MODE_PRIVATE);

        final EditText emailEditText = (EditText) findViewById(R.id.editText_Email);
        final EditText passwordEditText = (EditText) findViewById(R.id.editText_Password);
        final CheckBox rememberCheckBox = (CheckBox) findViewById(R.id.checkBox_Remember);
        Button loginButton = (Button) findViewById(R.id.button_Login);
        Button signUpButton = (Button) findViewById(R.id.button_SignUp);

        // Remember Me: prefill the saved email
        if (sharedPreferences.getBoolean("remember_flag", false)) {
            emailEditText.setText(sharedPreferences.getString("remember_email", ""));
            rememberCheckBox.setChecked(true);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString();

                // input validation
                if (email.isEmpty()) {
                    emailEditText.setError("Email is required");
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailEditText.setError("Invalid email format");
                    return;
                }
                if (password.isEmpty()) {
                    passwordEditText.setError("Password is required");
                    return;
                }

                // save Remember Me
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (rememberCheckBox.isChecked()) {
                    editor.putBoolean("remember_flag", true);
                    editor.putString("remember_email", email);
                } else {
                    editor.putBoolean("remember_flag", false);
                    editor.putString("remember_email", "");
                }
                editor.apply();

                // admin account (from requirement 4.11)
                if (email.equals("admin@admin.com") && password.equals("Admin123!")) {
                    sharedPreferences.edit().putString("session_email", email).apply();
                    Toast.makeText(LoginActivity.this, "Admin login OK", Toast.LENGTH_SHORT).show();
                    // Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                    // LoginActivity.this.startActivity(intent); finish();
                    return;
                }

                // normal user
                DataBaseHelper dataBaseHelper =
                        new DataBaseHelper(LoginActivity.this, "SMART_EVENTS_DB", null, 1); // use your real DB name/version
                User user = dataBaseHelper.getUserByEmail(email);

                if (user == null) {
                    Toast.makeText(LoginActivity.this,
                            "No account found with this email", Toast.LENGTH_LONG).show();
                    return;
                }

                boolean correct = PasswordHasher.verify(password, user.getmPassword()); // your verify method
                if (!correct) {
                    passwordEditText.setError("Incorrect password");
                    Toast.makeText(LoginActivity.this,
                            "Incorrect email or password", Toast.LENGTH_LONG).show();
                    return;
                }

                // success
                sharedPreferences.edit().putString("session_email", user.getmEmail()).apply();
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("user_email", user.getmEmail());
                LoginActivity.this.startActivity(intent);
                finish();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });
    }
}