package com.example.taskapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.taskapp.models.UserModel;
import com.example.taskapp.ui.home.HomeFragment;
import com.example.taskapp.ui.profile.EditProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yalantis.ucrop.UCrop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    Toolbar toolbar;
    NavigationView navigationView;
    View header;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initialisation();
        setOnClickListeners();
        initNavController();
        showFab();
        if (!new Prefs(this).isShown()) navController.navigate(R.id.boardFragment);
        else if (FirebaseAuth.getInstance().getCurrentUser() == null)
            navController.navigate(R.id.phoneFragment);

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            final Prefs prefs = new Prefs(this);
            //if (prefs.avatarUrl().equals("")){return;}
            String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseFirestore.getInstance().collection("users").document(uId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            UserModel userModel = task.getResult().toObject(UserModel.class);
                            assert userModel != null;
                            prefs.name(userModel.getName());
                            prefs.desc(userModel.getDesc());
                            prefs.avatarUrl(userModel.getAvatar());
                        }
                    }
                }
            });
           initProfileHeader();
        }

    }

    private void initProfileHeader(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        Prefs prefs = new Prefs(this);

        ImageView icon = header.findViewById(R.id.imageView);
        TextView name = header.findViewById(R.id.name);
        TextView desc = header.findViewById(R.id.desc);

        Glide
                .with(this)
                .load(prefs.avatarUrl())
                .circleCrop()
                .into(icon);

        name.setText(prefs.name());
        desc.setText(prefs.desc());
    }


    private void showFab() {
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.boardFragment || destination.getId() == R.id.phoneFragment) {
                    toolbar.setVisibility(View.GONE);
                } else {
                    toolbar.setVisibility(View.VISIBLE);
                }
                if (destination.getId() == R.id.profileFragment || destination.getId() == R.id.addTaskFragment) {
                    DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                } else {
                    DrawerLayout mDrawerLayout = findViewById(R.id.drawer_layout);
                    mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                }
            }
        });
    }

    private void setOnClickListeners() {
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.profileFragment);
            }
        });
    }

    private void initialisation() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.nav_view);
        header = navigationView.getHeaderView(0);

        ImageView icon = header.findViewById(R.id.imageView);
        TextView name = header.findViewById(R.id.name);
        TextView desc = header.findViewById(R.id.desc);

        Glide
                .with(this)
                .load(new Prefs(this).avatarUrl())
                .circleCrop()
                .into(icon);

        name.setText(new Prefs(this).name());
        desc.setText(new Prefs(this).desc());

    }

    private void initNavController() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                new Prefs(MainActivity.this).clear();
                finish();
                break;

            case R.id.deliteAll:
                Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                assert navHostFragment != null;
                ((HomeFragment) navHostFragment.getChildFragmentManager().getFragments().get(0)).removeAll();
                break;

            case R.id.change_sort_type:
                if (item.getTitle().equals("сортировка по алфавиту")) {
                    item.setTitle("сортировка по умолчанию");
                    sortList(true);
                } else {
                    item.setTitle("сортировка по алфавиту");
                    sortList(false);
                }
                break;

            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void sortList(boolean argument) {
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        ((HomeFragment) navHostFragment.getChildFragmentManager().getFragments().get(0)).sortList(argument);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UCrop.REQUEST_CROP) {
            assert data != null;
            Log.e("ololo", "onActivityResult: edit profile suuuupppeerr");
            Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            assert navHostFragment != null;
            EditProfileFragment editProfileFragment = ((EditProfileFragment) navHostFragment.getChildFragmentManager().getFragments().get(0));
            editProfileFragment.cropPhoto(UCrop.getOutput(data));
        }
    }
}
