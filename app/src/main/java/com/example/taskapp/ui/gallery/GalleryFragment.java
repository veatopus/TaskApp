package com.example.taskapp.ui.gallery;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.taskapp.Prefs;
import com.example.taskapp.R;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class GalleryFragment extends Fragment {
    private EditText editTextNote;

    private final static String FILE_NAME = "content.txt";
    private static final int REQUEST_PERMISSION_WRITE = 1001;
    private Prefs prefs;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialisation(view);
        setListener();
        openText();
    }

    private void setListener() {
        editTextNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                saveText(s.toString());
            }
        });

    }

    private void initialisation(View view) {
        editTextNote = view.findViewById(R.id.edit_text_note);
        prefs = new Prefs(requireActivity());
    }


    private File getExternalPath() {
        return(new File(Environment.getExternalStorageDirectory(), FILE_NAME));
    }

    private void saveText(String save_text){
        if(!prefs.permissionGranted()){
            checkPermissions();
            return;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(getExternalPath());
            fos.write(save_text.getBytes());
        }
        catch(IOException ex) {
            Toast.makeText(requireActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally{
            try{
                if(fos!=null)
                    fos.close();
            }
            catch(IOException ex){

                Toast.makeText(requireActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openText(){

        if(!prefs.permissionGranted()){
            checkPermissions();
            return;
        }

        FileInputStream fin = null;
        File file = getExternalPath();
        // если файл не существует, выход из метода
        if(!file.exists()) return;
        try {
            fin =  new FileInputStream(file);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String text = new String (bytes);
            editTextNote.setText(text);
        }
        catch(IOException ex) {

            Toast.makeText(requireActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally{

            try{
                if(fin!=null)
                    fin.close();
            }
            catch(IOException ex){

                Toast.makeText(requireActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // проверяем, доступно ли внешнее хранилище для чтения и записи
    private boolean isExternalStorageWriteable(){
        String state = Environment.getExternalStorageState();
        return  Environment.MEDIA_MOUNTED.equals(state);
    }

    // проверяем, доступно ли внешнее хранилище хотя бы только для чтения
    private boolean isExternalStorageReadable(){
        String state = Environment.getExternalStorageState();
        return  (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    private void checkPermissions(){

        if(!isExternalStorageReadable() || !isExternalStorageWriteable()){
            Toast.makeText(requireActivity(), "Внешнее хранилище не доступно", Toast.LENGTH_LONG).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if (requestCode == REQUEST_PERMISSION_WRITE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                prefs.permissionGranted(true);
                Toast.makeText(requireActivity(), "Разрешения получены", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(requireActivity(), "Необходимо дать разрешения", Toast.LENGTH_LONG).show();
            }
        }
    }
}