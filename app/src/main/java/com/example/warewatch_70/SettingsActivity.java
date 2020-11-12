package com.example.warewatch_70;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.warewatch_70.models.UserModel;

public class SettingsActivity extends AppCompatActivity {

    String[] valuesAdmin = new String[]{
            "ADD USER",
            "CHANGE PASSWORD",
            "VIEW HISTORY",
            "LOGOUT"
    };

    String[] values = new String[]{
            "CHANGE PASSWORD",
            "LOGOUT"
    };

    private UserModel currentUser;

    private ListView listView;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("SETTINGS");


        Intent intent = getIntent();
        currentUser = (UserModel) intent.getSerializableExtra("user");

        getSupportActionBar().setSubtitle(currentUser.getUsername());

        listView = (ListView) findViewById(R.id.listSettings);

        if(currentUser.isAdmin()){
            adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,android.R.id.text1, valuesAdmin);
        }else{
            adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,android.R.id.text1, values);
        }


        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String itemValue = (String) listView.getItemAtPosition(position);

                if(currentUser.isAdmin()){
                    switch (itemValue){
                        case "ADD USER":
                            //Toast.makeText(SettingsActivity.this, "you want to add user, "+currentUser.getUsername(),Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(SettingsActivity.this,AddUserActivity.class);
                            intent.putExtra("user",currentUser);
                            startActivity(intent);
                            break;
                        case "CHANGE PASSWORD":
                            Toast.makeText(SettingsActivity.this, "you want to change password",Toast.LENGTH_SHORT).show();
                            break;
                        case "VIEW HISTORY":
                            Toast.makeText(SettingsActivity.this, "you want to view history",Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(SettingsActivity.this,HistoryActivity.class));
                            break;
                        case "LOGOUT":
                            SharedPreferences mPreferences = getSharedPreferences("currentUser",MODE_PRIVATE);
                            mPreferences.edit().clear().commit();

                            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                            finishAffinity();
                    }
                }else{
                    switch (itemValue){
                        case "CHANGE PASSWORD":
                            Toast.makeText(SettingsActivity.this, "you want to change password",Toast.LENGTH_SHORT).show();
                            break;
                        case "LOGOUT":
                            SharedPreferences mPreferences = getSharedPreferences("currentUser",MODE_PRIVATE);
                            mPreferences.edit().clear().commit();

                            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                            finishAffinity();
                    }
                }

            }
        });
    }

}
