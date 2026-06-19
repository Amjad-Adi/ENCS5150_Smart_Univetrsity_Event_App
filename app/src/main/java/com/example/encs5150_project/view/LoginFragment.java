package com.example.encs5150_project.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.encs5150_project.R;
import com.example.encs5150_project.controller.AuthenticationController;

public class LoginFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText emailEditText = view.findViewById(R.id.editText_Email);
        EditText passwordEditText = view.findViewById(R.id.editText_Password);
        CheckBox rememberCheckBox = view.findViewById(R.id.checkBox_Remember);
        Button loginButton = view.findViewById(R.id.button_Login);
        Button signUpButton = view.findViewById(R.id.button_SignUp);
        AuthenticationActivity hostingActivity = (AuthenticationActivity) getActivity();
        AuthenticationController controller;
        if (hostingActivity != null) {
            controller = hostingActivity.getController();
            if (controller != null && controller.getRememberMeStatus()) {
                emailEditText.setText(controller.getRememberedEmail());
                rememberCheckBox.setChecked(true);
            }
        } else {
            controller = null;
        }
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString();
            boolean remember = rememberCheckBox.isChecked();
            if (controller != null) {
                AuthenticationController.AuthResponse response = controller.handleLogin(email, password, remember);
                switch (response.status()) {
                    case SUCCESS_ADMIN:
                        onLoginSuccess(response.message(), true);
                        break;
                    case SUCCESS_USER:
                        onLoginSuccess(response.message(), false);
                        break;
                    default:
                        showError(response.message());
                        break;
                }
            }
        });

        signUpButton.setOnClickListener(v -> {
            if (hostingActivity != null) {
                hostingActivity.loadFragment(new RegistrationFragment());
            }
        });
    }

    public void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void onLoginSuccess(String email, boolean isAdmin) {
        Toast.makeText(getActivity(), isAdmin ? "Admin Login Successful" : "Login Successful", Toast.LENGTH_SHORT).show();
        Class<?> destinationActivity;
        if (isAdmin) {
            destinationActivity = AdminActivity.class;
        } else {
            destinationActivity = UserActivity.class;
        }
        Intent intent = new Intent(getActivity(), destinationActivity);
        intent.putExtra("user_email", email);
        startActivity(intent);
        getActivity().finish();
    }
}