package com.example.taskapp.ui.home;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.taskapp.App;
import com.example.taskapp.Prefs;
import com.example.taskapp.R;
import com.example.taskapp.interfaces.OnItemClickListener;
import com.example.taskapp.models.TaskModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class HomeFragment extends Fragment {

    private ArrayList<TaskModel> arrayList = new ArrayList<>();
    private FloatingActionButton fab;
    private NavController navController;
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private DividerItemDecoration dividerItemDecoration;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialisation(view);
        setClickListeners();
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(taskAdapter);

        App.getInstance().getDatabase().taskDao().getAllLive().observe(getViewLifecycleOwner(), new Observer<List<TaskModel>>() {
            @Override
            public void onChanged(List<TaskModel> taskModels) {
                arrayList.clear();
                arrayList.addAll(taskModels);
                taskAdapter.notifyDataSetChanged();
            }
        });

        if (arrayList.isEmpty()) {
            arrayList.addAll(App.getInstance().getDatabase().taskDao().getAll());
            taskAdapter.notifyDataSetChanged();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initProfileHeader(View view){
        NavigationView navigationView = requireActivity().findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        Prefs prefs = new Prefs(requireActivity());

        ImageView icon = header.findViewById(R.id.imageView);
        TextView name = header.findViewById(R.id.name);
        TextView desc = header.findViewById(R.id.desc);

        Glide
                .with(this)
                .load(prefs.avatarUrl())
                .circleCrop()
                .into(icon);

        name.setText(prefs.name());
        desc.setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhoneNumber());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initialisation(View view) {
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        recyclerView = view.findViewById(R.id.container);
        taskAdapter = new TaskAdapter(arrayList);
        dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayout.VERTICAL);
        fab = view.findViewById(R.id.fab);
        initProfileHeader(view);
    }

    private void setClickListeners() {
        taskAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("keyTask", arrayList.get(position));
                navController.navigate(R.id.addTaskFragment, bundle);
            }

            @Override
            public void onLongItemClick(final int position) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Вы точно хотите удалить задачу?")
                        .setPositiveButton("да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                App.getInstance().getDatabase().taskDao().delete(arrayList.get(position));
                                arrayList.remove(position);
                            }
                        })
                        .setNegativeButton("нет", null);
                builder.show();
            }

            @Override
            public void onColorViewClick(final int position) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Вы точно хотите оставить только те задачи которые имеют данный цвет?")
                        .setPositiveButton("да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                int color = arrayList.get(position).getColor();
                                arrayList.clear();
                                arrayList.addAll(App.getInstance().getDatabase().taskDao().getAllByColor(color));
                                taskAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("нет", null);
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


    public void sortList(boolean isDefaultSort) {
        if (isDefaultSort) {
            arrayList.clear();
            arrayList.addAll(App.getInstance().getDatabase().taskDao().sort());
            taskAdapter.notifyDataSetChanged();
        } else {
            arrayList.clear();
            arrayList.addAll(App.getInstance().getDatabase().taskDao().getAll());
            taskAdapter.notifyDataSetChanged();
        }
    }

    public void removeAll() {
        App.getInstance().getDatabase().taskDao().nukeTable();
        for (int i = 0; i < arrayList.size()-1; i++) {
            arrayList.remove(arrayList.get(i));
            taskAdapter.notifyItemRemoved(i);
        }
    }
}