package com.example.warewatch_70;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.warewatch_70.fragments.AlertsFragment;
import com.example.warewatch_70.fragments.HomeFragment;
import com.example.warewatch_70.fragments.MapFragment;
import com.example.warewatch_70.interfaces.HomeFragmentListener;
import com.example.warewatch_70.interfaces.ReloadMapFragment;
import com.example.warewatch_70.models.AlertsModel;
import com.example.warewatch_70.models.UserModel;
import com.example.warewatch_70.models.WarehouseModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity implements HomeFragmentListener, ReloadMapFragment {
    private static final String TAG = "MainActivity";

    private UserModel user;

    private Fragment selectedFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // =========================================================================================
        FirebaseMessaging.getInstance().subscribeToTopic(AlertsModel.COLLECTION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "onComplete: error subscription");
                        } else {
                            Log.d(TAG, "onComplete: successeful subscription");
                        }
                    }
                });
        // =========================================================================================

        Intent intent = getIntent();
        user = (UserModel) intent.getSerializableExtra("user");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment(user)).commit();
        getSupportActionBar().setTitle("HOME");
        getSupportActionBar().setSubtitle(user.getUsername());
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment(user);
                            getSupportActionBar().setTitle("HOME");
                            break;
                        case R.id.nav_map:
                            selectedFragment = new MapFragment(user);
                            getSupportActionBar().setTitle("MAP");
                            break;
                        case R.id.nav_alerts:
                            selectedFragment = new AlertsFragment(user);
                            getSupportActionBar().setTitle("ALERTS");
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                    return true;
                }
            };

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(MainActivity.this,"Menu clicked",Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void genericMethod(WarehouseModel wh, UserModel user) {

        Intent intent = new Intent(MainActivity.this,VideoActivity.class);
        intent.putExtra("user",user);
        intent.putExtra("warehouse",wh);
        startActivity(intent);
    }

    @Override
    public void reloadMap() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MapFragment(user)).commit();
    }
}
