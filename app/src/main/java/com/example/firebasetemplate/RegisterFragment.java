package com.example.firebasetemplate;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.firebasetemplate.databinding.FragmentRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;


public class RegisterFragment extends AppFragment {
    private FragmentRegisterBinding binding;
    private Uri uriImagen;
    private Uri downloadUrl;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentRegisterBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fotoPerfil.setOnClickListener(v -> galeria.launch("image/*"));

        appViewModel.uriImagenPerfilSeleccionada.observe(getViewLifecycleOwner(), uri -> {
            if (uri != null){
                Glide.with(this).load(uri).into(binding.fotoPerfil);
                uriImagen = uri;
            }
        });

        binding.verifyEmailButton.setOnClickListener(v -> {

        });

        binding.createAccountButton.setOnClickListener(v -> {
            if (binding.passwordEditText.getText().toString().isEmpty()) {
                binding.passwordEditText.setError("Required");
                return;
            }
            FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(
                            binding.emailEditText.getText().toString(),
                            binding.passwordEditText.getText().toString()
                    ).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            FirebaseStorage.getInstance().getReference("profileimage_" + user.getUid()).putFile(uriImagen).addOnSuccessListener(runnable -> {
                                Log.d("tag", "profileimage_" + user.getUid());
                                FirebaseStorage.getInstance().getReference().child("profileimage_" + user.getUid()).getDownloadUrl().addOnSuccessListener(uri -> {
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(uri)
                                            .build();
                                    user.updateProfile(profileUpdates);
                                }).addOnFailureListener(runnable2 -> {
                                    Log.d("tag", runnable2.getMessage() + runnable2.getLocalizedMessage());
                                });
                            });

                            navController.navigate(R.id.action_registerFragment_to_postsHomeFragment);
                        } else {
                            Toast.makeText(requireContext(), task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });



        });
    }

    private final ActivityResultLauncher<String> galeria = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        appViewModel.setUriImagenPerfilSeleccionada(uri);
    });

}