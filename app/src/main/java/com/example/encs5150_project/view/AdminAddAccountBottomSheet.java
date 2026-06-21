package com.example.encs5150_project.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.encs5150_project.R;
import com.example.encs5150_project.controller.AddAccountController;
import com.example.encs5150_project.model.entity.AdminRole;
import com.example.encs5150_project.model.entity.PersonGender;
import com.example.encs5150_project.model.entity.UserMajor;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;

public class AdminAddAccountBottomSheet extends BottomSheetDialogFragment {
    private AddAccountController controller;
    private boolean isCreatingAdmin = false;
    private boolean canManageAdmins = false;

    private TextInputEditText etFirstName, etLastName, etEmail, etPassword, etConfirmPassword, etPhone, etSalary;
    private AutoCompleteTextView actvGender, actvMajor, actvRole;
    private LinearLayout llUserFields, llAdminFields;
    private MaterialButtonToggleGroup toggleGroup;
    private MaterialButton btnSubmit;

    public AdminAddAccountBottomSheet() {}

    public void setSetupData(AddAccountController controller, boolean canManageAdmins) {
        this.controller = controller;
        this.canManageAdmins = canManageAdmins;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_admin_add_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etFirstName = view.findViewById(R.id.etAddFirstName);
        etLastName = view.findViewById(R.id.etAddLastName);
        etEmail = view.findViewById(R.id.etAddEmail);
        etPassword = view.findViewById(R.id.etAddPassword);
        etConfirmPassword = view.findViewById(R.id.etAddConfirmPassword);
        etPhone = view.findViewById(R.id.etAddPhone);
        etSalary = view.findViewById(R.id.etAddSalary);
        actvGender = view.findViewById(R.id.actvAddGender);
        actvMajor = view.findViewById(R.id.actvAddMajor);
        actvRole = view.findViewById(R.id.actvAddRole);
        llUserFields = view.findViewById(R.id.llUserFields);
        llAdminFields = view.findViewById(R.id.llAdminFields);
        toggleGroup = view.findViewById(R.id.toggleGroupAccountType);
        btnSubmit = view.findViewById(R.id.btnSubmitAccount);
        if (!canManageAdmins) {
            toggleGroup.setVisibility(View.GONE);
        }
        setupDropdowns();
        setupToggleListener();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSubmit();
            }
        });
    }

    private void setupToggleListener() {
        toggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup materialButtonToggleGroup, int checkedId, boolean isChecked) {
                if (isChecked) {
                    if (checkedId == R.id.btnTypeAdmin) {
                        isCreatingAdmin = true;
                        llUserFields.setVisibility(View.GONE);
                        llAdminFields.setVisibility(View.VISIBLE);
                    } else {
                        isCreatingAdmin = false;
                        llUserFields.setVisibility(View.VISIBLE);
                        llAdminFields.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void setupDropdowns() {
        String[] genders = new String[PersonGender.values().length];
        for (int i = 0; i < PersonGender.values().length; i++) genders[i] = PersonGender.values()[i].name();
        actvGender.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, genders));
        String[] majors = new String[UserMajor.values().length];
        for (int i = 0; i < UserMajor.values().length; i++) majors[i] = UserMajor.values()[i].name();
        actvMajor.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, majors));
        String[] roles = new String[AdminRole.values().length];
        for (int i = 0; i < AdminRole.values().length; i++) roles[i] = AdminRole.values()[i].name();
        actvRole.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, roles));
    }

    private void handleSubmit() {
        String fName = etFirstName.getText().toString().trim();
        String lName = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString();
        String confirm = etConfirmPassword.getText().toString();
        String gender = actvGender.getText().toString().trim();
        AddAccountController.AddResponse response;
        if (isCreatingAdmin) {
            String salary = etSalary.getText().toString().trim();
            String role = actvRole.getText().toString().trim();
            response = controller.addAdmin(fName, lName, email, pass, confirm, gender, salary, role);
        } else {
            String phone = etPhone.getText().toString().trim();
            String major = actvMajor.getText().toString().trim();
            response = controller.addUser(fName, lName, email, phone, pass, confirm, gender, major);
        }
        Toast.makeText(requireContext(), response.message(), Toast.LENGTH_SHORT).show();
        if (response.status() == AddAccountController.AddStatus.SUCCESS) {
            Bundle result = new Bundle();
            result.putBoolean("isUpdated", true);
            getParentFragmentManager().setFragmentResult("RefreshAccountList", result);
            dismiss();
        }
    }
}