package com.example.encs5150_project.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.encs5150_project.R;
import com.example.encs5150_project.controller.AuthenticationController;
import com.example.encs5150_project.model.entity.PersonGender;
import com.example.encs5150_project.model.entity.UserMajor;

public class RegistrationFragment extends Fragment {

    private EditText firstNameEditText, lastNameEditText, emailEditText, phoneEditText, passwordEditText, confirmPasswordEditText;
    private AutoCompleteTextView genderAutoComplete, categoryAutoComplete;
    private AuthenticationController controller;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    public static RegistrationFragment newInstance() {
        return new RegistrationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firstNameEditText = view.findViewById(R.id.editText_FirstName);
        lastNameEditText = view.findViewById(R.id.editText_LastName);
        emailEditText = view.findViewById(R.id.editText_Email);
        phoneEditText = view.findViewById(R.id.editText_Phone);
        passwordEditText = view.findViewById(R.id.editText_Password);
        confirmPasswordEditText = view.findViewById(R.id.editText_ConfirmPassword);
        genderAutoComplete = view.findViewById(R.id.autoComplete_Gender);
        categoryAutoComplete = view.findViewById(R.id.autoComplete_Category);
        Button registerButton = view.findViewById(R.id.button_Register);
        Button backToLoginButton = view.findViewById(R.id.button_BackToLogin);
        AuthenticationActivity parentActivity = (AuthenticationActivity) getActivity();
        if (parentActivity != null) {
            controller = parentActivity.getController();
        }
        loadAdapter();
        registerButton.setOnClickListener(v -> {
            String genderStr = genderAutoComplete.getText().toString().trim();
            String majorStr = categoryAutoComplete.getText().toString().trim();
            if (genderStr.isEmpty()) {
                showError("Please select a gender");
                return;
            }
            if (majorStr.isEmpty()) {
                showError("Please select a category");
                return;
            }

            if (controller != null) {
                AuthenticationController.AuthResponse response = controller.handleRegistration(firstNameEditText.getText().toString().trim(), lastNameEditText.getText().toString().trim(), emailEditText.getText().toString().trim(), phoneEditText.getText().toString().trim(), passwordEditText.getText().toString(), confirmPasswordEditText.getText().toString(), genderStr, majorStr);
                if (response.status() == AuthenticationController.AuthStatus.SUCCESS_REGISTRATION) {
                    onRegistrationSuccess();
                } else {
                    showError(response.message());
                }
            }
        });

        backToLoginButton.setOnClickListener(v -> goBackToLogin(parentActivity));
    }

    private void loadAdapter() {
        String[] genders = new String[PersonGender.values().length];
        for (int i = 0; i < PersonGender.values().length; i++) {
            genders[i] = PersonGender.values()[i].name();
        }
        String[] majors = new String[UserMajor.values().length];
        for (int i = 0; i < UserMajor.values().length; i++) {
            majors[i] = UserMajor.values()[i].name();
        }
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, genders);
        genderAutoComplete.setAdapter(genderAdapter);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, majors);
        categoryAutoComplete.setAdapter(categoryAdapter);
    }

    private void goBackToLogin(AuthenticationActivity parentActivity) {
        if (parentActivity != null) {
            parentActivity.loadFragment(new LoginFragment());
        }
    }

    public void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    public void onRegistrationSuccess() {
        Toast.makeText(getActivity(), "Account created successfully!", Toast.LENGTH_LONG).show();
        goBackToLogin((AuthenticationActivity) getActivity());
    }
}