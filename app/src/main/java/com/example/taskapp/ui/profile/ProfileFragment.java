package com.example.taskapp.ui.profile;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.taskapp.App;
import com.example.taskapp.Prefs;
import com.example.taskapp.R;
import com.example.taskapp.ui.media.ShowImageFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ProfileFragment extends Fragment {
    private ImageView imageView;
    private FloatingActionButton floatingActionButton;
    private NavController navController;
    private TextView textViewTitle;
    private TextView textViewDesc;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialisation(view);
        setListeners();
        textViewTitle.setText(new Prefs(requireActivity()).name());
        textViewDesc.setText(new Prefs(requireActivity()).desc());
        Glide
                .with(this)
                .load(new Prefs(requireActivity()).avatarUrl())
                .circleCrop()
                .into(imageView);
    }

    private void setListeners() {
        floatingActionButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        navController.navigate(R.id.editProfileFragment);
                    }
                }
        );
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("key", new Prefs(requireActivity()).avatarUrl());
                if (!(new Prefs(requireActivity()).avatarUrl().equals(""))){
                    navController.navigate(R.id.showImageFragment, bundle);
                }
            }
        });
    }

    private void initialisation(View view) {
        imageView = view.findViewById(R.id.icon);
        floatingActionButton = view.findViewById(R.id.fab);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        textViewDesc = view.findViewById(R.id.user_desc);
        textViewTitle = view.findViewById(R.id.user_name);
    }
}
