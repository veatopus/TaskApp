package com.example.taskapp.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskapp.R;
import com.example.taskapp.interfaces.OnItemClickListener;
import com.example.taskapp.models.TaskModel;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private ArrayList<TaskModel> data;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    TaskAdapter(ArrayList<TaskModel> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        TaskViewHolder taskViewHolder = new TaskViewHolder(inflater.inflate(R.layout.view_holder_task, parent, false));
        taskViewHolder.setOnItemClickListener(onItemClickListener);
        return taskViewHolder;
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
        private OnItemClickListener onItemClickListener;

        void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(getAdapterPosition());
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickListener.onLongItemClick(getAdapterPosition());
                    return true;
                }
            });
            buttonTitle = itemView.findViewById(R.id.btn_title);
            textViewDescription = itemView.findViewById(R.id.tv_description);
            constraintLayout = itemView.findViewById(R.id.constaintlayoutbg);
        }

        void onBind(TaskModel taskModel) {
            buttonTitle.setText(taskModel.getTitle());
            textViewDescription.setText(taskModel.getDescription());
        }
    }
}
