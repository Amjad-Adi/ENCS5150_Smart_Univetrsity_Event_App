package com.example.encs5150_project.view;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.encs5150_project.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        NavigationView navigationView = findViewById(R.id.navigationView);
        View mainContent = findViewById(R.id.mainContent);
        ViewCompat.setOnApplyWindowInsetsListener(mainContent, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.adminFragment,new AdminHomeFragment()).commit();
        }
    }
}
//<TextView
//                            android:id="@+id/adminHomeNumOfUsers"
//                            android:layout_width="wrap_content"
//                            android:layout_height="wrap_content"
//                            android:text="0"
//                            android:textColor="@color/black"
//                            android:textSize="22sp"
//                            android:textStyle="bold"
//                            app:layout_constraintStart_toStartOf="@id/tvApprovedTitle"
//                            app:layout_constraintTop_toBottomOf="@id/tvApprovedTitle" />
//
//                        <TextView
//                            android:id="@+id/adminHomePercentageIncreaseOfUsers"
//                            android:layout_width="wrap_content"
//                            android:layout_height="wrap_content"
//                            android:textColor="@color/success"
//                            android:textSize="22sp"
//                            android:textStyle="bold"
//                            app:layout_constraintStart_toStartOf="@id/adminHomeNumOfUsers"
//                            app:layout_constraintTop_toBottomOf="@id/adminHomeNumOfUsers" />