package com.example.taskapp.ui.profile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.taskapp.App;
import com.example.taskapp.Prefs;
import com.example.taskapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class EditProfileFragment extends Fragment {
    private EditText editTextName;
    private EditText editTextDesc;
    private FloatingActionButton fab;
    private NavController navController;
    private ProgressBar progressBar;
    private ImageView imageView;
    private Button btnPhoto;
    private Prefs prefs;


    public EditProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialisation(view);
        setListeners();

        Glide
                .with(this)
                .load(prefs.avatarUrl())
                .circleCrop()
                .into(imageView);
    }

    private void initialisation(View view) {
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        editTextName = view.findViewById(R.id.user_name);
        editTextDesc = view.findViewById(R.id.user_desc);
        fab = view.findViewById(R.id.fab);
        progressBar = view.findViewById(R.id.progress_bar);
        imageView = view.findViewById(R.id.icon);
        btnPhoto = view.findViewById(R.id.btn_set_profile_icon);
        prefs = new Prefs(requireActivity());

        editTextName.setText(prefs.name());
        editTextDesc.setText(prefs.desc());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void updateUser(String key, String value) {
       // final boolean[] b = new boolean[1];
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .update(key, value)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       // Toast.makeText(requireActivity(), task.isSuccessful() + "", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setListeners() {
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String desc = editTextDesc.getText().toString().trim();
                if (name.equals(""))
                    Toast.makeText(requireActivity(), "введите имя", Toast.LENGTH_SHORT).show();
                else {
                    progressBar.setVisibility(View.VISIBLE);
                    prefs.name(name);
                    prefs.desc(desc);

                    updateUser("name", name);
                    updateUser("desc", desc);

                    navController.popBackStack();
                }
            }
        });

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prefs.avatarUrl().equals("")){
                    requestPermission();
                    return;
                }
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("установить фото")
                        .setPositiveButton("удалить фото", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            public void onClick(DialogInterface dialog, int id) {
                                progressBar.setVisibility(View.VISIBLE);
                                FirebaseStorage.getInstance().getReference().child("images/" +
                                        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/avatar")
                                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            FirebaseFirestore
                                                    .getInstance()
                                                    .collection("users")
                                                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .update("avatar", "")
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                prefs.avatarUrl("");
                                                                progressBar.setVisibility(View.GONE);
                                                                Toast.makeText(requireActivity(), "успех", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                        }else {
                                            Toast.makeText(requireActivity(), "error", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("выбрать из галереи", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermission();
                            }
                        });
                builder.show();
            }
        });
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED)
            openGallery();
        else requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 102) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 101);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("ololo", "onActivityResult: edit profile");
        if (resultCode == RESULT_OK && requestCode == 101) {
            assert data != null;
            Uri uri = data.getData();
            String destinationFileName = UUID.randomUUID().toString() + ".jpg";
            assert uri != null;
            UCrop.of(uri, Uri.fromFile(new File(requireActivity().getCacheDir(), destinationFileName)))
                    .withAspectRatio(1, 1)
                    .start(requireActivity());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void cropPhoto(Uri uri) {
        uploadToFB(uri);
        Glide
                .with(this)
                .load(uri)
                .circleCrop()
                .into(imageView);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void uploadToFB(Uri uri) {
        btnPhoto.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("images/" +
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/avatar");
        reference.putFile(uri)
                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        return reference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(requireContext(), "успех", Toast.LENGTH_SHORT).show();

                    progressBar.setVisibility(View.GONE);
                    btnPhoto.setVisibility(View.VISIBLE);
                    saveAvatarUrl(Objects.requireNonNull(task.getResult()).toString());
                } else {
                    Toast.makeText(requireContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void saveAvatarUrl(String url) {
        prefs.avatarUrl(url);
        updateUser("avatar", url);
    }
}