package com.example.taskapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.example.taskapp.ui.home.HomeFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    //private FloatingActionButton fab;
    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialisation();
        setOnClickListeners();
        initNavController();
        showFab();
        if (!new Prefs(this).isShown()) navController.navigate(R.id.boardFragment);

    }

    private void showFab() {
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.boardFragment) {
                    toolbar.setVisibility(View.GONE);
                } else {
                    toolbar.setVisibility(View.VISIBLE);
                }
                if (destination.getId() == R.id.profileFragment || destination.getId() == R.id.addTaskFragment){
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
        NavigationView navigationView = findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
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
        //fab = findViewById(R.id.fab);
    }

    private void initNavController() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
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
}
