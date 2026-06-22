package com.example.encs5150_project.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.encs5150_project.R;
import com.example.encs5150_project.controller.AddAccountController;
import com.example.encs5150_project.controller.AdminDetailsController;
import com.example.encs5150_project.controller.AdminManagementController;
import com.example.encs5150_project.controller.AdminUserDetailsController;
import com.example.encs5150_project.model.entity.Admin;
import com.example.encs5150_project.model.entity.Person;
import com.example.encs5150_project.model.entity.User;
import com.example.encs5150_project.model.repository.database.contracts.AdminContract;
import com.example.encs5150_project.model.repository.database.contracts.PersonContract;
import com.example.encs5150_project.model.repository.database.contracts.UserContract;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AdminManagementFragment extends Fragment {

    private AdminManagementController controller;
    private RecyclerView rvAccounts;
    private TabLayout tabLayout;
    private TextInputEditText etSearch;
    private TextView tvNoData;
    private MaterialButton btnFilter;
    private AccountAdapter adapter;
    private AutoCompleteTextView autoCompleteCategory;
    private String currentSearchBy = PersonContract.COLUMN_FIRST_NAME;
    private boolean isAscending = true;
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    ExtendedFloatingActionButton fabAddPerson;
    private final int SEARCH_DELAY_MS = 300;private class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {

        private List<Person> items = new ArrayList<>();

        public void updateData(List<Person> newItems) {
            this.items.clear();
            this.items.addAll(newItems);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public AccountViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_person, parent, false);
            return new AccountViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AccountViewHolder holder, int position) {
            Person person = items.get(position);
            String name = person.getFirstName() + " " + person.getLastName();
            String subtitle = "";
            String status = "";
            if (person instanceof User) {
                User u = (User) person;
                subtitle = "Major: " + u.getMajor().name();
                status = u.getAccountStatus().name();
            } else if (person instanceof Admin) {
                Admin a = (Admin) person;
                subtitle = "Role: " + a.getRole().name();
                status = a.getAccountStatus().name();
            }
            holder.bind(person, name, subtitle, status);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//I didn't apply Open Closed principle as i think its over-engineering for this app, But I would really encourage to add it in future.
                    Person currentPerson = items.get(holder.getAdapterPosition());
                    if (currentPerson instanceof User) {
                        AdminUserDetailBottomSheet bottomSheet = new AdminUserDetailBottomSheet();
                        AdminUserDetailsController detailController = ((AdminActivity) getActivity()).getAdminUserDetailsController();
                        bottomSheet.setSetupData((User) currentPerson, detailController);
                        bottomSheet.show(getParentFragmentManager(), "UserDetailSheet");
                    } else if (currentPerson instanceof Admin) {
                        AdminDetailsBottomSheet bottomSheet = new AdminDetailsBottomSheet();
                        AdminDetailsController detailController = ((AdminActivity) getActivity()).getAdminDetailsController();
                        bottomSheet.setSetupData((Admin) currentPerson, detailController);
                        bottomSheet.show(getParentFragmentManager(), "AdminDetailSheet");
                    }
                }
            });
        }

        @Override
        public int getItemCount() { return items.size(); }

        static class AccountViewHolder extends RecyclerView.ViewHolder {
            private final ShapeableImageView ivProfilePic;
            private final TextView tvName, tvSubtitle, tvEmail, tvStatus;

            public AccountViewHolder(@NonNull View itemView) {
                super(itemView);
                ivProfilePic = itemView.findViewById(R.id.ivPersonProfilePic);
                tvName = itemView.findViewById(R.id.tvPersonName);
                tvSubtitle = itemView.findViewById(R.id.tvPersonSubtitle);
                tvEmail = itemView.findViewById(R.id.tvPersonEmail);
                tvStatus = itemView.findViewById(R.id.tvPersonStatus);
            }

            public void bind(Person person, String name, String subtitle, String status) {
                tvName.setText(name);
                tvSubtitle.setText(subtitle);
                tvEmail.setText(person.getEmail());
                tvStatus.setText(status);
                if ("ENABLED".equalsIgnoreCase(status)) {
                    tvStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.success));
                    tvStatus.setTypeface(null, android.graphics.Typeface.BOLD);
                } else {
                    tvStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.error));
                    tvStatus.setTypeface(null, android.graphics.Typeface.NORMAL);
                }
                Glide.with(itemView.getContext())
                        .load(person.getProfilePicturePath())
                        .skipMemoryCache(true)
                        .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
                        .placeholder(R.drawable.profile)
                        .error(R.drawable.profile)
                        .into(ivProfilePic);
            }
        }
    }
    public AdminManagementFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_account_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() instanceof AdminActivity) {
            controller = ((AdminActivity) getActivity()).getLogOutController();
        }
        rvAccounts = view.findViewById(R.id.rvAccounts);
        tabLayout = view.findViewById(R.id.tabLayoutAccounts);
        etSearch = view.findViewById(R.id.etSearchAccounts);
        btnFilter = view.findViewById(R.id.btnFilterAccounts);
        tvNoData = view.findViewById(R.id.emptyView);
        autoCompleteCategory = view.findViewById(R.id.autoComplete_Category);
        fabAddPerson=view.findViewById(R.id.fabAddPerson);
        setupTabs();
        setupSearch();
        setupCategoryDropdown();
        setupSortButton();
        adapter = new AccountAdapter();
        rvAccounts.setAdapter(adapter);
        refreshList();
        fabAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminAddAccountBottomSheet bottomSheet = new AdminAddAccountBottomSheet();
                AdminActivity parentActivity = (AdminActivity) getActivity();
                if (parentActivity != null) {
                    AddAccountController addController = parentActivity.getAddAccountController();
                    boolean canManageAdmins = parentActivity.getLogOutController().canManageAdmins();
                    bottomSheet.setSetupData(addController, canManageAdmins);
                    bottomSheet.show(getParentFragmentManager(), "AddAccountSheet");
                }
            }
        });
        getParentFragmentManager().setFragmentResultListener("RefreshAccountList", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                boolean isUpdated = bundle.getBoolean("isUpdated", false);
                if (isUpdated) {
                    refreshList();
                }
            }
        });
    }

    private void setupTabs() {
        if (!controller.canManageAdmins()) tabLayout.removeTabAt(1);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                controller.setAccountType(tab.getPosition() == 0 ? AdminManagementController.AccountType.USER : AdminManagementController.AccountType.ADMIN);
                refreshList();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { refreshList(); }
            @Override
            public void afterTextChanged(Editable s) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                searchHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshList();
                    }
                }, SEARCH_DELAY_MS);
            }
        });
    }
    private void setupCategoryDropdown() {
        String[] displayNames;
        String[] columnNames;
        if (controller.getCurrentType() == AdminManagementController.AccountType.USER) {
            displayNames = new String[]{"First Name", "Last Name", "Email", "Gender", "Major", "Phone Number", "Account Status"};
            columnNames = new String[]{UserContract.COLUMN_ID, PersonContract.COLUMN_FIRST_NAME, PersonContract.COLUMN_LAST_NAME, PersonContract.COLUMN_EMAIL, PersonContract.COLUMN_GENDER, UserContract.COLUMN_MAJOR, UserContract.COLUMN_PHONE_NUMBER, UserContract.COLUMN_ACCOUNT_STATUS};
        } else {
            displayNames = new String[]{"First Name", "Last Name", "Email", "Gender", "Salary", "Role", "Account Status"};
            columnNames = new String[]{AdminContract.COLUMN_ID, PersonContract.COLUMN_FIRST_NAME, PersonContract.COLUMN_LAST_NAME, PersonContract.COLUMN_EMAIL, PersonContract.COLUMN_GENDER, AdminContract.COLUMN_SALARY, AdminContract.COLUMN_ROLE, AdminContract.COLUMN_ACCOUNT_STATUS};
        }
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, displayNames);
        autoCompleteCategory.setAdapter(dropdownAdapter);
        autoCompleteCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentSearchBy = columnNames[position];
                refreshList();
            }
        });
    }
    private void setupSortButton() {
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAscending = !isAscending;
                updateSortIcon();
                adapter.updateData(controller.toggleSortDirection(isAscending));
            }
        });
    }
    private void updateSortIcon() {
        int iconRes = isAscending ? android.R.drawable.arrow_up_float : android.R.drawable.arrow_down_float;
        btnFilter.setIconResource(iconRes);
    }

    private void refreshList() {
        String query = etSearch.getText() != null ? etSearch.getText().toString() : "";
        List<Person> results = controller.performSearch(currentSearchBy, isAscending, query);
        if (results.isEmpty()) {
            rvAccounts.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        } else {
            rvAccounts.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.GONE);
            adapter.updateData(results);
        }
        updateSortIcon();
    }
}