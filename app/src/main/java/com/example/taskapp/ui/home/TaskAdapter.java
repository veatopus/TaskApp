package com.example.taskapp.ui.home;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskapp.R;
import com.example.taskapp.models.TaskModel;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private ArrayList<TaskModel> data;
    private boolean aBoolean = false;

    TaskAdapter(ArrayList<TaskModel> data) {
        this.data = data;
    }

    void add(TaskModel taskModel) {
        if (aBoolean) taskModel.setColor(Color.WHITE);
        else taskModel.setColor(Color.BLUE);
        aBoolean = !aBoolean;
        data.add(taskModel);
        notifyDataSetChanged();
    }

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

        void onBind(TaskModel taskModel) {
            buttonTitle.setText(taskModel.getTitle());
            textViewDescription.setText(taskModel.getDescription());
            constraintLayout.setBackgroundColor(taskModel.getColor());
        }
    }
}
