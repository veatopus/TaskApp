package com.example.taskapp.ui.media;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.taskapp.R;

public class ShowImageFragment extends Fragment {
    private ImageView imageView;
    private String address;
    public final String KEY = "key";

    public ShowImageFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageView = view.findViewById(R.id.imageView);
        if (getArguments() != null){
            address = getArguments().getString(KEY);
            show();
        }
    }

    private void show() {
        Glide
                .with(this)
                .load(address)
                .into(imageView);
    }
}