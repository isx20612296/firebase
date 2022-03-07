package com.example.firebasetemplate;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.firebasetemplate.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;


public class ProfileFragment extends AppFragment {
    private FragmentProfileBinding binding;
    private TextView textName;
    private TextView textEmail;
    private ImageView imageProfile;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentProfileBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        textName.setText(user.getDisplayName());
//        textEmail.setText(user.getEmail());
//        Glide.with(this).load(user.getPhotoUrl()).into(imageProfile);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                binding.textName.setText(profile.getDisplayName());
                binding.textEmail.setText(profile.getEmail());
                Glide.with(this).load(profile.getPhotoUrl()).into(binding.imageProfile);
            }
        }


    }
}