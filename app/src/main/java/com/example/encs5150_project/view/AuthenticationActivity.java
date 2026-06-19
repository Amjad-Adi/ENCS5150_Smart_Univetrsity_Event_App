package com.example.encs5150_project.view;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.encs5150_project.R;
import com.example.encs5150_project.controller.AuthenticationController;
import com.example.encs5150_project.model.PasswordHashingAlgorithm;
import com.example.encs5150_project.model.repository.UserRepository;
import com.example.encs5150_project.model.repository.database.DataBaseHelper;
import com.example.encs5150_project.model.repository.preferences.SharedPrefManager;

public class AuthenticationActivity extends AppCompatActivity {
    private AuthenticationController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_authentication);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        controller = new AuthenticationController(SharedPrefManager.getInstance(this),new UserRepository(DataBaseHelper.getInstance(this)),new PasswordHashingAlgorithm());
        if (savedInstanceState == null) {
            loadFragment(new LoginFragment());
        }
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.main, fragment).commit();
    }

    public AuthenticationController getController() {
        return controller;
    }

    public void setController(AuthenticationController controller) {
        this.controller = controller;
    }
}