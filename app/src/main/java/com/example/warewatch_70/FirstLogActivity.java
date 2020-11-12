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
import java.util.HashMap;
import java.util.Map;

public class FirstLogActivity extends AppCompatActivity {
    public static final String CURRENT_USER = "currentUser";
    public static final String NULL_VALUE = "nullValue";

    private UserModel currentUser;
    private DocumentReference currentUserOwnerRef;

    private EditText edUsername, edPass, edConfirmPass;
    private Button btn;

    private boolean deleted;

    private FirebaseFirestore db;

    private SharedPreferences mPreferences;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_log);

        getSupportActionBar().hide();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating password...");

        db =  FirebaseFirestore.getInstance();

        edUsername = (EditText) findViewById(R.id.edFirstUserName);
        edPass = (EditText) findViewById(R.id.edFirstPass);
        edConfirmPass = (EditText) findViewById(R.id.edFirstConfirmPass);
        btn = (Button) findViewById(R.id.btnFirst);

        Intent intent = getIntent();
        currentUser = (UserModel) intent.getSerializableExtra("user");

        edUsername.setText(currentUser.getUsername());

        mPreferences = getSharedPreferences(CURRENT_USER,MODE_PRIVATE);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = edUsername.getText().toString().trim();
                String pass = edPass.getText().toString().trim();
                String confirm = edConfirmPass.getText().toString().trim();

                String firsname = currentUser.getFirstname();
                String lastname = currentUser.getLastname();
                String phone = currentUser.getPhone();
                Boolean admin = currentUser.isAdmin();
                Date lastUpdate = new Date();
                currentUserOwnerRef = db.collection(UserModel.COLLECTION).document(currentUser.getWhOwnerRef());

                if(!(pass.equals(confirm))){

                    Toast.makeText(FirstLogActivity.this,"passwords do not match",Toast.LENGTH_SHORT).show();

                }else{

                    progressDialog.show();

                    Map<String, Object> mUser = new HashMap<>();
                    mUser.put(UserModel.USERNAME, username);
                    mUser.put(UserModel.FIRSTNAME, firsname);
                    mUser.put(UserModel.LASTNAME, lastname);
                    mUser.put(UserModel.PHONE, phone);
                    mUser.put(UserModel.PASSWORD, pass);
                    mUser.put(UserModel.ADMIN, false);
                    mUser.put(UserModel.LASTUPDATE, lastUpdate);
                    mUser.put(UserModel.FIRSTLOG, false);
                    mUser.put(UserModel.WHOWNER, currentUserOwnerRef);

                    db.collection(UserModel.COLLECTION)
                            .document(currentUser.getUsername())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("DELETE SUCCESS","Successfuly deleted");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("DELETE FAILURE","Failed to delete");
                                    mPreferences.edit().clear().commit();
                                    progressDialog.dismiss();
                                    Toast.makeText(FirstLogActivity.this,R.string.database_error,Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(FirstLogActivity.this, LoginActivity.class));
                                }
                            });

                        db.collection("users")
                                .document(username)
                                .set(mUser)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //Toast.makeText(FirstLogActivity.this, "PASSWORD UPDATED", Toast.LENGTH_SHORT).show();
                                        //Load the updated document
                                        db.collection("users").document(currentUser.getUsername()).get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        Log.d("FIRSTLOG_OLDPASS", "" + currentUser.getPassword());
                                                        Log.d("FIRSTLOG_OLDTIME", "" + currentUser.getLastUpdate().toString());

                                                        currentUser.setUsername(documentSnapshot.getString(UserModel.USERNAME));
                                                        currentUser.setPassword(documentSnapshot.getString(UserModel.PASSWORD));
                                                        currentUser.setLastUpdate(documentSnapshot.getDate(UserModel.LASTUPDATE));
                                                        currentUser.setFirstLog(documentSnapshot.getBoolean(UserModel.FIRSTLOG));

                                                        Log.d("FIRSTLOG_NEWPASS", "" + currentUser.getPassword());
                                                        Log.d("FIRSTLOG_NEWTIME", "" + currentUser.getLastUpdate().toString());

                                                        mPreferences.edit().putString(currentUser.PASSWORD, currentUser.getPassword()).apply();
                                                        mPreferences.edit().putBoolean(currentUser.FIRSTLOG, currentUser.isFirstLog()).apply();
                                                        mPreferences.edit().putLong(currentUser.LASTUPDATE, currentUser.getLastUpdate().getTime()).apply();

                                                        //START MAIN ACTIVITY
                                                        Intent intent = new Intent(FirstLogActivity.this, MainActivity.class);
                                                        intent.putExtra("user", currentUser);
                                                        progressDialog.dismiss();
                                                        startActivity(intent);
                                                        finish();

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(FirstLogActivity.this, R.string.database_error, Toast.LENGTH_SHORT).show();
                                                Log.w("FIRSTLOG_FAILURE", "" + e.getMessage());
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toast.makeText(FirstLogActivity.this, R.string.password_change_error, Toast.LENGTH_SHORT).show();
                                        Log.w("FIRSTLOG_FAILURE", e.getMessage());
                                    }
                                });

                }
            }
        });
    }
}
