package com.example.encs5150_project.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.encs5150_project.R;
import com.example.encs5150_project.model.config.Contact;
import com.google.android.material.button.MaterialButton;

public class UserContactUsFragment extends Fragment {

    private MaterialButton btnCallUs, btnLocateUs, btnEmailUs;

    public UserContactUsFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_contact_us, container, false);
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

        btnCallUs = view.findViewById(R.id.btnCallUs);
        btnLocateUs = view.findViewById(R.id.btnLocateUs);
        btnEmailUs = view.findViewById(R.id.btnEmailUs);
        btnCallUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dialIntent = new Intent();
                dialIntent.setAction(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + Contact.PHONE_NUMBER));
                startActivity(dialIntent);
            }
        });

        // 2. Locate Us / Maps Intent
        btnLocateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Contact.MAPS_LOCATION_URI));
                intent.setPackage("com.google.android.apps.maps");

                if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Intent webMapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=31.9598,35.1828"));
                    startActivity(webMapsIntent);
                }
            }
        });

        btnEmailUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gmailIntent = new Intent();
                gmailIntent.setAction(Intent.ACTION_SENDTO);
                gmailIntent.setType("message/rfc822"); // Explicit MIME type configuration
                gmailIntent.setData(Uri.parse("mailto:"));
                gmailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {Contact.EMAIL_ADDRESS});
                gmailIntent.putExtra(Intent.EXTRA_SUBJECT, "App Support Inquiry");
                gmailIntent.putExtra(Intent.EXTRA_TEXT, "Content of the message");

                try {
                    startActivity(gmailIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}