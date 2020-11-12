package com.example.warewatch_70;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.warewatch_70.models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddUserActivity extends AppCompatActivity {

    private UserModel currentUser;
    private DocumentReference currentUserRef;

    private EditText edFirstName, edLastName, edPhone;
    private Button btn;

    private FirebaseFirestore db;
    private Date today;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("ADD A NEW USER");


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating user...");

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        currentUser = (UserModel) intent.getSerializableExtra("user");

        getSupportActionBar().setSubtitle(currentUser.getUsername());

        edFirstName = (EditText) findViewById(R.id.edAddFirstName);
        edLastName = (EditText) findViewById(R.id.edAddLasttName);
        edPhone = (EditText) findViewById(R.id.edAddPhone);
        btn = (Button) findViewById(R.id.btnAddUser);

        today = new Date();
        currentUserRef = db.collection(UserModel.COLLECTION).document(currentUser.getUsername());
        Log.d("CURRENT_USER_REF","current user reference: "+currentUserRef);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String firstname = edFirstName.getText().toString();
                String lastname = edLastName.getText().toString();
                String username = firstname+""+lastname;
                String phone = edPhone.getText().toString();

                Map<String, Object> mUser = new HashMap<>();
                mUser.put(UserModel.USERNAME, username);
                mUser.put(UserModel.FIRSTNAME, firstname);
                mUser.put(UserModel.LASTNAME, lastname);
                mUser.put(UserModel.PHONE, phone);
                mUser.put(UserModel.PASSWORD,"azertyu");
                mUser.put(UserModel.ADMIN, false);
                mUser.put(UserModel.LASTUPDATE,today);
                mUser.put(UserModel.FIRSTLOG,true);
                mUser.put(UserModel.WHOWNER,currentUserRef);

                db.collection("users")
                        .document(username)
                        .set(mUser)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Toast.makeText(AddUserActivity.this,"USER ADDED",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(AddUserActivity.this,"FAILED TO ADD USER",Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        });
            }
        });
    }
}
