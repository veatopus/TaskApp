package com.example.taskapp.ui;

import android.content.Context;
import android.graphics.Color;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.taskapp.App;
import com.example.taskapp.R;
import com.example.taskapp.models.TaskModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import petrov.kristiyan.colorpicker.ColorPicker;

public class AddTaskFragment extends Fragment {
    private EditText editTextTitle;
    private EditText editTextDesc;
    private FloatingActionButton floatingActionButton;
    private TaskModel taskModel;
    private int colorForTask = Color.RED;
    private Button colorSelection;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_add_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialisation(view);
        setOnClickListeners();

        if (getArguments() != null) {
            taskModel = (TaskModel) getArguments().getSerializable("keyTask");
            assert taskModel != null;
            editTextTitle.setText(taskModel.getTitle());
            editTextDesc.setText(taskModel.getDescription());
        }
    }

    private void initialisation(View view) {
        editTextTitle = view.findViewById(R.id.et_title);
        editTextDesc = view.findViewById(R.id.et_description);
        floatingActionButton = view.findViewById(R.id.fab);
        colorSelection = view.findViewById(R.id.btn_setColor);
    }

    private void setOnClickListeners() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                if (editTextTitle.getText().toString().trim().equals("")) {
                    Toast.makeText(getActivity(), "Введите title", Toast.LENGTH_SHORT).show();
                    return;
                }

                String title = editTextTitle.getText().toString().trim();
                String description = editTextDesc.getText().toString().trim();

                if (taskModel == null) {
                    taskModel = new TaskModel(title, description);
                    taskModel.setColor(colorForTask);
                    App.getInstance().getDatabase().taskDao().insert(taskModel);
                } else {
                    taskModel.setTitle(title);
                    taskModel.setDescription(description);
                    taskModel.setColor(colorForTask);
                    App.getInstance().getDatabase().taskDao().update(taskModel);
                }

                hideKeyboard();
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigateUp();
            }
        });
        colorSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ColorPicker colorPicker = new ColorPicker(requireActivity());
                colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                    @Override
                    public void setOnFastChooseColorListener(int position, int color) {
                        colorForTask = color;
                        colorSelection.setBackgroundColor(color);
                    }

                    @Override
                    public void onCancel(){
                        // put code
                    }
                })
                        .setDefaultColorButton(Color.parseColor("#f84c44"))
                        .setColumns(5)
                        .show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputManager != null;
        inputManager.hideSoftInputFromWindow(Objects.requireNonNull(requireActivity().getCurrentFocus()).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}