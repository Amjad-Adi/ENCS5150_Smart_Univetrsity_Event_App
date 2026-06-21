package com.example.encs5150_project.view;

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
import com.example.encs5150_project.controller.AddAccountController;
import com.example.encs5150_project.controller.AdminDetailsController;
import com.example.encs5150_project.controller.AdminManagementController;
import com.example.encs5150_project.controller.AdminProfileController;
import com.example.encs5150_project.controller.AdminUserDetailsController;
import com.example.encs5150_project.model.PasswordHashingAlgorithm;
import com.example.encs5150_project.model.repository.AdminRepository;
import com.example.encs5150_project.model.repository.PersonRepository;
import com.example.encs5150_project.model.repository.UserRepository;
import com.example.encs5150_project.model.repository.database.DataBaseHelper;
import com.example.encs5150_project.model.repository.preferences.SharedPrefManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class AdminActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private AdminProfileController profileController;
    private AdminManagementController managementController;
    private AdminUserDetailsController adminUserDetailsController;
    private AddAccountController addAccountController;
    private AdminDetailsController adminDetailsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        View mainContent = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainContent, (v, insets) -> {
                        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                        return insets;
                    });
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        setupDrawer();
        setupBackPressHandling();
        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.home);
            replaceFragment(new AdminHomeFragment(), "Admin Home");
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
                if (itemId == R.id.home) {
                    replaceFragment(new AdminHomeFragment(), "Admin Home");
                } else if (itemId == R.id.events) {
                    replaceFragment(new AdminEventsFragment(), "Manage Events");
                } else if (itemId == R.id.reservations) {
                    replaceFragment(new AdminReservationsFragment(), "Reservations");
                } else if (itemId == R.id.accounts) {
                    replaceFragment(new AdminManagementFragment(), "Manage Accounts");
                } else if (itemId == R.id.profile) {
                    replaceFragment(new AdminProfileFragment(), "My Profile");
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void replaceFragment(Fragment fragment, String title) {
        getSupportFragmentManager().beginTransaction().replace(R.id.adminFragment, fragment).commit();
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
    public AdminProfileController getProfileController() {
        if (profileController == null) {
            profileController = new AdminProfileController(new AdminRepository(DataBaseHelper.getInstance(this)), SharedPrefManager.getInstance(this),new PasswordHashingAlgorithm());
        }
        return profileController;
    }
    public AdminManagementController getAdminController() {
        if (managementController == null) {
            managementController = new AdminManagementController(new UserRepository(DataBaseHelper.getInstance(this)),new AdminRepository(DataBaseHelper.getInstance(this)), SharedPrefManager.getInstance(this));
        }
        return managementController;
    }
    public AdminUserDetailsController getAdminUserDetailsController() {
        if (adminUserDetailsController == null) {
            adminUserDetailsController = new AdminUserDetailsController(new UserRepository(DataBaseHelper.getInstance(this)),new PersonRepository(),DataBaseHelper.getInstance(this).getReadableDatabase());
        }
        return adminUserDetailsController;
    }
    public AddAccountController getAddAccountController() {
        if (addAccountController == null) {
            addAccountController = new AddAccountController(new UserRepository(DataBaseHelper.getInstance(this)),new AdminRepository(DataBaseHelper.getInstance(this)),new PasswordHashingAlgorithm());
        }
        return addAccountController;
    }
    public AdminDetailsController getAdminDetailsController() {
        if (adminDetailsController == null) {
            adminDetailsController = new AdminDetailsController(new AdminRepository(DataBaseHelper.getInstance(this)));
        }
        return adminDetailsController;
    }
}