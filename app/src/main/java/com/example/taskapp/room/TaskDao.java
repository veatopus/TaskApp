package com.example.taskapp.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.taskapp.models.TaskModel;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM TaskModel")
    List<TaskModel> getAll();

    @Query("SELECT * FROM taskmodel WHERE color = :color")
    List<TaskModel> getAllByColor(int color);

    @Query("SELECT * FROM TaskModel")
    LiveData<List<TaskModel>> getAllLive();

    @Insert
    void insert(TaskModel taskModel);

    @Update
    void update(TaskModel taskModel);

    @Delete
    void delete(TaskModel taskModel);

    @Query("SELECT * FROM taskmodel ORDER BY title ASC")
    List<TaskModel> sort();

    @Query("DELETE FROM TaskModel")
    void nukeTable();
}
