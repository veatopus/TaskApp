package com.example.taskapp.ui;

import android.content.Context;
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
import android.widget.EditText;
import android.widget.Toast;

import com.example.taskapp.R;
import com.example.taskapp.models.TaskModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class AddTaskFragment extends Fragment {
    private EditText editTextTitle;
    private EditText editTextDesc;
    private FloatingActionButton floatingActionButton;
    private int position;
    private String requestKey = "form";

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
            TaskModel taskModel = (TaskModel) getArguments().getSerializable("keyTask");
            position = getArguments().getInt("keyPosition");
            assert taskModel != null;
            editTextTitle.setText(taskModel.getTitle());
            editTextDesc.setText(taskModel.getDescription());
            requestKey = "formRed";
        }
    }

    private void initialisation(View view) {
        editTextTitle = view.findViewById(R.id.et_title);
        editTextDesc = view.findViewById(R.id.et_description);
        floatingActionButton = view.findViewById(R.id.fab);
    }

    private void setOnClickListeners() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                TaskModel taskModel = new TaskModel(editTextTitle.getText().toString().trim(), editTextDesc.getText().toString().trim());
                if (taskModel.getTitle().equals("")) {
                    Toast.makeText(getActivity(), "Введите title", Toast.LENGTH_SHORT).show();
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putSerializable("task", taskModel);
                getParentFragmentManager().setFragmentResult(requestKey, bundle);
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);


                InputMethodManager inputManager = (InputMethodManager) requireContext(). getSystemService(Context.INPUT_METHOD_SERVICE);
                assert inputManager != null;
                inputManager.hideSoftInputFromWindow( Objects.requireNonNull(requireActivity().getCurrentFocus()).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                navController.navigateUp();
            }
        });
    }
}
