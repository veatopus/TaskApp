package com.example.taskapp.ui;

import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskapp.R;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private ArrayList<Task> data;
    private boolean aBoolean = false;

    public TaskAdapter(ArrayList<Task> data) {
        this.data = data;
    }

    public void add(Task task) {
        if (aBoolean) task.setColor(Color.WHITE);
        else task.setColor(Color.BLUE);
        aBoolean = !aBoolean;
        data.add(task);
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new TaskViewHolder(inflater.inflate(R.layout.view_holder_task, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.onBind(data.get(position));

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        Button buttonTitle;
        TextView textViewDescription;
        ConstraintLayout constraintLayout;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            buttonTitle = itemView.findViewById(R.id.btn_title);
            textViewDescription = itemView.findViewById(R.id.tv_description);
            constraintLayout = itemView.findViewById(R.id.constaintlayoutbg);
        }

        void onBind(Task task) {
            buttonTitle.setText(task.getTitle());
            textViewDescription.setText(task.getDescription());
            constraintLayout.setBackgroundColor(task.getColor());
        }
    }
}
