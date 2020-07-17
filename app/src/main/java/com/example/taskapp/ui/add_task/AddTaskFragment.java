package com.example.taskapp.ui.add_task;

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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.taskapp.App;
import com.example.taskapp.R;
import com.example.taskapp.models.TaskModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.Random;

import petrov.kristiyan.colorpicker.ColorPicker;

public class AddTaskFragment extends Fragment {
    private EditText editTextTitle;
    private EditText editTextDesc;
    private CheckBox checkBox = null;
    private FloatingActionButton floatingActionButton;
    private TaskModel taskModel;
    private int colorForTask = Color.RED;
    private Button colorSelection;
    private NavController navController;
    private ProgressBar progressBar;
    public static final String TEXT_CHECK_BOX = "обноаить задачу в памяти телефона";

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
            colorForTask = taskModel.getColor();
            colorSelection.setBackgroundColor(colorForTask);
            checkBox.setText(getArguments().getString("titleCheckBox", "обновить задачу в облаке"));
        }
    }

    private void initialisation(View view) {
        editTextTitle = view.findViewById(R.id.et_title);
        editTextDesc = view.findViewById(R.id.et_description);
        floatingActionButton = view.findViewById(R.id.fab);
        colorSelection = view.findViewById(R.id.btn_setColor);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        checkBox = view.findViewById(R.id.check_box);
        progressBar = view.findViewById(R.id.progress_bar);
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

                hideKeyboard();

                String title = editTextTitle.getText().toString().trim();
                String description = editTextDesc.getText().toString().trim();

                if (taskModel == null) {
                    taskModel = new TaskModel(title, description);
                    taskModel.setColor(colorForTask);
                    saveToFireStore();
                } else {
                    taskModel.setTitle(title);
                    taskModel.setDescription(description);
                    taskModel.setColor(colorForTask);
                    updateData();
                }

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
                    public void onCancel() {
                    }
                })
                        .setDefaultColorButton(Color.parseColor("#f84c44"))
                        .setColumns(5)
                        .setTitle("выберете цвет")
                        .show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void updateData() {
        if (checkBox.getText().toString().equals(TEXT_CHECK_BOX) && !checkBox.isChecked()) {
            progressBar.setVisibility(View.VISIBLE);
            FirebaseFirestore
                    .getInstance()
                    .collection("tasks")
                    .document(taskModel.getId())
                    .set(taskModel)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                printToast("задача успешно обновлена");
                                progressBar.setVisibility(View.GONE);
                                close();
                            }
                        }
                    });
        } else if (!checkBox.isChecked()) {
            App.getInstance().getDatabase().taskDao().update(taskModel);
            close();

        } else {
            progressBar.setVisibility(View.VISIBLE);
            FirebaseFirestore
                    .getInstance()
                    .collection("tasks")
                    .document(taskModel.getId())
                    .set(taskModel)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                printToast("задача успешно обновлена");
                                App.getInstance().getDatabase().taskDao().update(taskModel);
                                close();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                printToast(Objects.requireNonNull(task.getException()).getMessage());
                                App.getInstance().getDatabase().taskDao().update(taskModel);
                                close();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void saveToFireStore() {
        if (checkBox.isChecked()) {
            progressBar.setVisibility(View.VISIBLE);
            FirebaseFirestore
                    .getInstance()
                    .collection("tasks")
                    .add(taskModel)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                taskModel.setId(Objects.requireNonNull(task.getResult()).getId());
                                App.getInstance().getDatabase().taskDao().insert(taskModel);
                                close();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
        } else {
            taskModel.setId(generateRandomString());
            App.getInstance().getDatabase().taskDao().insert(taskModel);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void close() {
        navController.popBackStack();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null)
            inputManager.hideSoftInputFromWindow(Objects.requireNonNull(requireActivity().getCurrentFocus()).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private static String generateRandomString() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    private void printToast(String s) {
        Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show();
    }

}