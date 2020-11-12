package com.example.warewatch_70;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.example.warewatch_70.models.UserModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class WelcomeActivity extends AppCompatActivity {
    private static final int SPLASH_TIME_DELAY = 4000;
    public static final String CURRENT_USER = "currentUser";
    public static final String NULL_VALUE = "nullValue";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UserModel user;

    private SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                mPreferences = getSharedPreferences(CURRENT_USER,MODE_PRIVATE);

                String spUsername = getSharedPreferences(CURRENT_USER,MODE_PRIVATE).getString(UserModel.USERNAME,NULL_VALUE);

                if(!(spUsername.equals(NULL_VALUE))){
                    Toast.makeText(WelcomeActivity.this,"ALREADY LOGGED IN",Toast.LENGTH_SHORT).show();

                    String spFirstName = getSharedPreferences(CURRENT_USER,MODE_PRIVATE).getString(UserModel.FIRSTNAME,NULL_VALUE);
                    String spLastName = getSharedPreferences(CURRENT_USER,MODE_PRIVATE).getString(UserModel.LASTNAME,NULL_VALUE);
                    String spPassword = getSharedPreferences(CURRENT_USER,MODE_PRIVATE).getString(UserModel.PASSWORD,NULL_VALUE);
                    String spPhone = getSharedPreferences(CURRENT_USER,MODE_PRIVATE).getString(UserModel.PHONE,NULL_VALUE);
                    boolean spAdmin = getSharedPreferences(CURRENT_USER,MODE_PRIVATE).getBoolean(UserModel.ADMIN,false);
                    boolean spFirstLog = getSharedPreferences(CURRENT_USER,MODE_PRIVATE).getBoolean(UserModel.FIRSTLOG,false);
                    Date spLastUpdate = new Date(getSharedPreferences(CURRENT_USER,MODE_PRIVATE).getLong(UserModel.LASTUPDATE,0));
                    String spWhOwnerRef = getSharedPreferences(CURRENT_USER,MODE_PRIVATE).getString(UserModel.WHOWNER,NULL_VALUE);

                    user = new UserModel(spUsername,spFirstName,spLastName,spPassword,spPhone,spAdmin,spFirstLog,spLastUpdate,spWhOwnerRef);

                    Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
                    intent.putExtra("user",user);
                    startActivity(intent);
                    finish();

                }else {
                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                    finish();
                }
            }
        },SPLASH_TIME_DELAY);


    }
}
