package com.example.taskapp.ui.home;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskapp.R;
import com.example.taskapp.interfaces.OnItemClickListener;
import com.example.taskapp.models.TaskModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class HomeFragment extends Fragment {

    private ArrayList<TaskModel> arrayList = new ArrayList<>();
    private FloatingActionButton fab;
    private NavController navController;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private DividerItemDecoration dividerItemDecoration;
    private FragmentResultListener fragmentResultListener;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialisation(view);
        setOnClickListeners();
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(taskAdapter);

        getParentFragmentManager().setFragmentResultListener("formRed",
                getViewLifecycleOwner(), fragmentResultListener);

        getParentFragmentManager().setFragmentResultListener("form",
                getViewLifecycleOwner(), fragmentResultListener);
    }

    private void initialisation(View view) {
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        recyclerView = view.findViewById(R.id.container);
        taskAdapter = new TaskAdapter(arrayList);
        dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayout.VERTICAL);
        fab = view.findViewById(R.id.fab);
        fragmentResultListener = new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                switch (requestKey){
                    case"form":
                        TaskModel task = (TaskModel) result.getSerializable("task");
                        arrayList.add(0, task);
                        break;
                    case "formRed":
                        Log.d("ololo", "onFragmentResult: ");
                        int position = result.getInt("position");
                        TaskModel taskModel = (TaskModel) result.getSerializable("task");
                        arrayList.remove(position);
                        arrayList.add(position, taskModel);
                        break;
                }
                taskAdapter.notifyDataSetChanged();
            }
        };
    }

    private void setOnClickListeners() {
        taskAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("keyTask", arrayList.get(position));
                bundle.putInt("keyPosition", position);
                navController.navigate(R.id.addTaskFragment, bundle);
            }
            @Override
            public void onLongItemClick(final int position) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Вы точно хотите удалить задачу?")
                        .setPositiveButton("да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                arrayList.remove(position);
                                taskAdapter.notifyItemRemoved(position);
                            }
                        })
                        .setNegativeButton("нет", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                builder.show();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.addTaskFragment);
            }
        });
    }
}