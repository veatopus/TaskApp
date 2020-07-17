package com.example.taskapp.ui.slideshow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskapp.R;
import com.example.taskapp.interfaces.OnItemClickListener;
import com.example.taskapp.models.TaskModel;
import com.example.taskapp.ui.add_task.AddTaskFragment;
import com.example.taskapp.ui.home.TaskAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SlideshowFragment extends Fragment {
    private boolean isSort;
    private ArrayList<TaskModel> taskModels;
    private TaskAdapter taskAdapter;
    private NavController navController;
    private ProgressBar progressBar;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_slideshow, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initialisation(view);
        onBackPressedCallback();
        initList(view);
        setListeners();
        updateInfo();

    }

    private void updateInfo(){
        progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore
                .getInstance()
                .collection("tasks")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            if (task.getResult() != null) {
                                taskModels.clear();
                                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                    String docId = snapshot.getId();
                                    TaskModel taskModel = snapshot.toObject(TaskModel.class);
                                    taskModel.setId(docId);
                                    taskModels.add(taskModel);
                                }
                                taskAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    private void setListeners() {
        taskAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("keyTask", taskModels.get(position));
                bundle.putString("titleCheckBox", AddTaskFragment.TEXT_CHECK_BOX);
                navController.navigate(R.id.addTaskFragment, bundle);
            }

            @Override
            public void onLongItemClick(final int position) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Вы точно хотите удалить задачу?")
                        .setPositiveButton("да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                progressBar.setVisibility(View.VISIBLE);
                                FirebaseFirestore
                                        .getInstance()
                                        .collection("tasks")
                                        .document(taskModels.get(position).getId())
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    taskModels.remove(position);
                                                    taskAdapter.notifyItemRemoved(position);
                                                    progressBar.setVisibility(View.GONE);
                                                } else {
                                                    if (task.getException() != null)
                                                        Toast.makeText(requireContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("нет", null)
                        .show();
            }

            @Override
            public void onColorViewClick(final int position) {
                int color = taskModels.get(position).getColor();
                for (int i = 0; i < taskModels.size(); i++) {
                    if (taskModels.get(i).getColor() != color) {
                        taskModels.remove(i);
                        taskAdapter.notifyDataSetChanged();
                    }
                }
                isSort = true;
            }
        });
    }

    private void initialisation(View view) {
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void initList(View view) {
        taskModels = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.container);
        taskAdapter = new TaskAdapter(taskModels);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayout.VERTICAL);
        recyclerView.setAdapter(taskAdapter);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void onBackPressedCallback() {
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isSort) {
                    updateInfo();
                    isSort = false;
                }
                else
                requireActivity().finish();
            }
        };
        requireActivity()
                .getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), onBackPressedCallback);
    }
}