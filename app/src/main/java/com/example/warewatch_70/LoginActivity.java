package com.example.warewatch_70;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.warewatch_70.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class LoginActivity extends AppCompatActivity {
    private static final String CURRENT_USER = "currentUser";

    private EditText edUsername, edPassword;
    private Button btnLogin;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UserModel user;

    private SharedPreferences mPreferences;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Login...");

        edUsername = (EditText) findViewById(R.id.edLoginUsername);
        edPassword = (EditText) findViewById(R.id.edLoginPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        mPreferences = getSharedPreferences(CURRENT_USER,MODE_PRIVATE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logUser();
            }
        });
    }

    private void logUser(){
        progressDialog.show();
        String username = edUsername.getText().toString().trim();
        final String password = edPassword.getText().toString().trim();

        if(username.equals("") || password.equals("")){
            progressDialog.dismiss();
            Toast.makeText(LoginActivity.this,R.string.login_incomplete_fields,Toast.LENGTH_SHORT).show();
        }else{
            DocumentReference userRef = db.collection(UserModel.COLLECTION).document(username);
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        if(documentSnapshot.getString(UserModel.PASSWORD).equals(password)){
                            user = new UserModel(documentSnapshot.getString(UserModel.USERNAME),
                                                 documentSnapshot.getString(UserModel.FIRSTNAME),
                                                 documentSnapshot.getString(UserModel.LASTNAME),
                                                 documentSnapshot.getString(UserModel.PASSWORD),
                                                 documentSnapshot.getString(UserModel.PHONE),
                                                 documentSnapshot.getBoolean(UserModel.ADMIN),
                                                 documentSnapshot.getBoolean(UserModel.FIRSTLOG),
                                                 documentSnapshot.getDate(UserModel.LASTUPDATE),
                                                 documentSnapshot.getDocumentReference(UserModel.WHOWNER).getId());

                            Log.d("OWNER REFERENCE",""+documentSnapshot.getDocumentReference("whOwnerRef").toString());

                            mPreferences.edit().putString(user.USERNAME,documentSnapshot.getString(UserModel.USERNAME)).apply();
                            mPreferences.edit().putString(user.FIRSTNAME,documentSnapshot.getString(UserModel.FIRSTNAME)).apply();
                            mPreferences.edit().putString(user.LASTNAME,documentSnapshot.getString(UserModel.LASTNAME)).apply();
                            mPreferences.edit().putString(user.PASSWORD,documentSnapshot.getString(UserModel.PASSWORD)).apply();
                            mPreferences.edit().putString(user.PHONE,documentSnapshot.getString(UserModel.PHONE)).apply();
                            mPreferences.edit().putBoolean(user.ADMIN,documentSnapshot.getBoolean(UserModel.ADMIN)).apply();
                            mPreferences.edit().putBoolean(user.FIRSTLOG,documentSnapshot.getBoolean(UserModel.FIRSTLOG)).apply();
                            mPreferences.edit().putLong(user.LASTUPDATE,documentSnapshot.getDate(UserModel.LASTUPDATE).getTime()).apply();
                            mPreferences.edit().putString(user.WHOWNER,documentSnapshot.getDocumentReference(UserModel.WHOWNER).getId()).apply();

                            if(user.isAdmin()){
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                intent.putExtra("user",user);
                                startActivity(intent);
                                progressDialog.dismiss();
                                finish();
                            }else{
                                if(user.isFirstLog()){
                                    Intent intent = new Intent(LoginActivity.this,FirstLogActivity.class);
                                    intent.putExtra("user",user);
                                    startActivity(intent);
                                    progressDialog.dismiss();
                                    finish();
                                }else{
                                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                    intent.putExtra("user",user);
                                    startActivity(intent);
                                    progressDialog.dismiss();
                                    finish();
                                }
                            }
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this,R.string.login_incorrect, Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,R.string.login_incorrect, Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this,R.string.database_error, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                }
            });
        }
    }
}
