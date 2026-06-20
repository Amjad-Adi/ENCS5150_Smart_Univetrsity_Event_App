package com.example.encs5150_project.view;

import static com.example.encs5150_project.view.constants.Introduction.*;
import android.content.Intent;
import android.os.*;
import android.view.*;
import android.view.animation.AnimationUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.cloudinary.android.MediaManager;
import com.example.encs5150_project.R;
import com.example.encs5150_project.controller.IntroductionController;
import com.example.encs5150_project.model.observer.FetchStatus;
import com.example.encs5150_project.model.repository.EventRepository;
import com.example.encs5150_project.model.config.CloudServer;
import com.example.encs5150_project.model.repository.database.DataBaseHelper;

import java.util.HashMap;
import java.util.Map;

public class IntroductionActivity extends AppCompatActivity implements FetchStatus {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);
        ImageView splashLogo = findViewById(R.id.imageView_SplashLogo);
        LinearLayout introContent = findViewById(R.id.layout_IntroContent);
        Button buttonConnect = findViewById(R.id.button_Connect);
        ProgressBar progressBar=findViewById(R.id.progressBar);
        splashLogo.startAnimation(AnimationUtils.loadAnimation(IntroductionActivity.this, R.anim.pulse));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splashLogo.clearAnimation();
                splashLogo.startAnimation(AnimationUtils.loadAnimation(IntroductionActivity.this, R.anim.slide_up));
                introContent.setVisibility(View.VISIBLE);
                introContent.startAnimation(AnimationUtils.loadAnimation(IntroductionActivity.this, R.anim.fade_in));
            }
        }, 2500);
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonConnect.setEnabled(false);
                progressBar.setVisibility(ProgressBar.VISIBLE);
                IntroductionController introductionController = new IntroductionController(new EventRepository(DataBaseHelper.getInstance(IntroductionActivity.this)),IntroductionActivity.this);
                introductionController.fetchData();
            }
        });
        try {
            Map<String, String> config = new HashMap<>();
            config.put(CloudServer.CLOUD_NAME_FIELD, CloudServer.CLOUD_NAME_DATA);
            MediaManager.init(getApplicationContext(), config);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fetchSuccess() {
        ProgressBar progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.GONE);
        startActivity(new Intent(IntroductionActivity.this, AuthenticationActivity.class));
        finish();
    }

    @Override
    public void fetchFailure(int reason) {
        Button buttonConnect = findViewById(R.id.button_Connect);
        ProgressBar progressBar=findViewById(R.id.progressBar);
        progressBar.setEnabled(false);
        buttonConnect.setEnabled(true);
        Toast.makeText(IntroductionActivity.this,reason==0? FAIL_TO_LOAD_FROM_API :DATA_PROBLEM,Toast.LENGTH_SHORT).show();
    }
}