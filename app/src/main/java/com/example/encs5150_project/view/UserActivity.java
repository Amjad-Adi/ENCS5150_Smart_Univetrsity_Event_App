package com.example.encs5150_project.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.encs5150_project.R;
import com.example.encs5150_project.controller.LogOutController;
import com.example.encs5150_project.controller.UserProfileController;
import com.example.encs5150_project.model.PasswordHashingAlgorithm;
import com.example.encs5150_project.model.repository.UserRepository;
import com.example.encs5150_project.model.repository.database.DataBaseHelper;
import com.example.encs5150_project.model.repository.preferences.SharedPrefManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class UserActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private UserProfileController profileController;
    private LogOutController logOutController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        View mainContent = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainContent, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        logOutController=new LogOutController(SharedPrefManager.getInstance(this));
        setupDrawer();
        setupBackPressHandling();
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.user_nav_home);
            replaceFragment(new UserHomeFragment(), "Home");
        }
    }

    private void setupDrawer() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.user_nav_home) {
                    replaceFragment(new UserHomeFragment(), "Home");
                } else if (itemId == R.id.user_nav_events) {
                    replaceFragment(new UserEventsSectionFragment(), "Events");
                } else if (itemId == R.id.user_nav_reservations) {
                    replaceFragment(new UserReservationSectionFragment(), "My Reservations");
                } else if (itemId == R.id.user_nav_favorites) {
                    replaceFragment(new UserFavoritesSectionFragment(), "Favorites");
                } else if (itemId == R.id.user_nav_special) {
                    replaceFragment(new UserSpecialSectionFragment(), "Special Section");
                } else if (itemId == R.id.user_nav_profile) {
                    replaceFragment(new UserProfileFragment(), "Profile Management");
                } else if (itemId == R.id.user_nav_contact) {
                    replaceFragment(new UserContactUsFragment(), "Contact Us");
                } else if (itemId == R.id.user_nav_log_out) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                        @Override
                        public void onDrawerClosed(@NonNull View drawerView) {
                            super.onDrawerClosed(drawerView);
                            drawerLayout.removeDrawerListener(this);
                            if (logOutController != null) {
                                logOutController.logout();
                                Intent intent = new Intent(UserActivity.this, AuthenticationActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                        }
                    });
                    return true;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
}

    private void replaceFragment(Fragment fragment, String title) {
        getSupportFragmentManager().beginTransaction().replace(R.id.userFragment, fragment).commit();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    private void setupBackPressHandling() {
        OnBackPressedCallback drawerBackCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, drawerBackCallback);

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                drawerBackCallback.setEnabled(true);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                drawerBackCallback.setEnabled(false);
            }
        });
    }

    public UserProfileController getProfileController() {
        if (profileController == null) {
            profileController = new UserProfileController(new UserRepository(DataBaseHelper.getInstance(this)), SharedPrefManager.getInstance(this), new PasswordHashingAlgorithm());
        }
        return profileController;
    }
}