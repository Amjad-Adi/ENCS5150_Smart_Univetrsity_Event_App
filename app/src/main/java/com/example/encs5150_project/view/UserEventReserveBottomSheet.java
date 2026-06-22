package com.example.encs5150_project.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.encs5150_project.R;
import com.example.encs5150_project.controller.UserEventReserveController;
import com.example.encs5150_project.model.EventSummary;
import com.example.encs5150_project.model.entity.Event;
import com.example.encs5150_project.model.entity.ReservationType;
import com.example.encs5150_project.model.entity.User;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserEventReserveBottomSheet extends BottomSheetDialogFragment {

    private UserEventReserveController controller;
    private EventSummary eventSummary;
    private User currentUser;
    private TextInputEditText etParticipationCount;
    private AutoCompleteTextView actvReservationType;
    private TextInputEditText etAdditionalInfo;
    private MaterialButton btnConfirmReservation;
    private MaterialButton btnCancelReservation;
    private ImageView ivEventImage;
    private TextView tvEventTitle, tvEventCategory, tvEventDate, tvEventLocation, tvEventCapacity;

    public UserEventReserveBottomSheet() {}

    public void setSetupData(EventSummary eventSummary, UserEventReserveController controller, User currentUser) {
        this.eventSummary = eventSummary;
        this.controller = controller;
        this.currentUser = currentUser;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_user_reserve, container, false);
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        populateEventDetails();
        setupReservationTypeDropdown();

        btnCancelReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnConfirmReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSubmit();
            }
        });
    }

    private void initViews(View view) {
        etParticipationCount = view.findViewById(R.id.etParticipationCount);
        actvReservationType = view.findViewById(R.id.actvReservationType);
        etAdditionalInfo = view.findViewById(R.id.etAdditionalInfo);
        btnConfirmReservation = view.findViewById(R.id.btnConfirmReservation);
        btnCancelReservation = view.findViewById(R.id.btnCancelReservation);
        View includedLayout = view.findViewById(R.id.includedEventDetails);
        ivEventImage = includedLayout.findViewById(R.id.ivEventImage);
        tvEventTitle = includedLayout.findViewById(R.id.tvEventTitle);
        tvEventCategory = includedLayout.findViewById(R.id.tvEventCategory);
        tvEventDate = includedLayout.findViewById(R.id.tvEventDate);
        tvEventLocation = includedLayout.findViewById(R.id.tvEventLocation);
        tvEventCapacity = includedLayout.findViewById(R.id.tvEventCapacity);
    }

    private void populateEventDetails() {
        if (eventSummary == null) return;
        Event event = eventSummary.event();
        tvEventTitle.setText(event.getTitle());
        tvEventCategory.setText(event.getCategory());
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);
        String dateTimeFormat = event.getDate().toString() + " • " + event.getTime().format(timeFormatter);
        tvEventDate.setText(dateTimeFormat);
        tvEventLocation.setText(event.getLocation());
        tvEventCapacity.setText("(" + eventSummary.bookedSeats() + "/" + event.getTotalSeats() + " Seats)");
        if (event.getImagePath() == null || event.getImagePath().isEmpty()) {
            ivEventImage.setImageResource(R.drawable.events);
        } else {
            Glide.with(requireContext())
                    .load(event.getImagePath())
                    .skipMemoryCache(true)
                    .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.events)
                    .error(R.drawable.events)
                    .into(ivEventImage);
        }
    }

    private void setupReservationTypeDropdown() {
        List<String> types = new ArrayList<>();
        for (ReservationType type : ReservationType.values()) {types.add(type.name());}
        ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, types);
        actvReservationType.setAdapter(dropdownAdapter);
    }

    private void handleSubmit() {
        if (controller == null || eventSummary == null) return;
        String countStr = etParticipationCount.getText().toString().trim();
        String typeStr = actvReservationType.getText().toString().trim();
        String extraInfo = etAdditionalInfo.getText().toString().trim();
        if (countStr.isEmpty()) {
            Toast.makeText(requireContext(), "Participation count is required.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (typeStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please select a reservation type.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            int requestedCount = Integer.parseInt(countStr);
            Event event = eventSummary.event();
            UserEventReserveController.ReservationResponse response = controller.submitReservation(event.getId(), currentUser.getId(), eventSummary.bookedSeats(), event.getTotalSeats(), requestedCount, typeStr, extraInfo);
            Toast.makeText(requireContext(), response.message(), Toast.LENGTH_LONG).show();
            if (response.status() == UserEventReserveController.ReservationStatus.SUCCESS) {
                Bundle result = new Bundle();
                result.putBoolean("isUpdated", true);
                getParentFragmentManager().setFragmentResult("RefreshUserEventList", result);
                dismiss();
            } else if (response.status() == UserEventReserveController.ReservationStatus.ERROR_VALIDATION) {
                etParticipationCount.setText("");
            }

        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Participation count must be a valid number.", Toast.LENGTH_SHORT).show();
            etParticipationCount.setText("");
        }
    }
}