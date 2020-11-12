package com.example.warewatch_70;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.warewatch_70.models.UserModel;
import com.example.warewatch_70.models.WarehouseModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VideoActivity extends AppCompatActivity {
    private static final String TAG = "VideoActivity";

    private VideoView videoView;
    private Button btnClose;
    private Button btnAlert;

    private WarehouseModel wh;
    private UserModel user;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        wh = (WarehouseModel) intent.getSerializableExtra("warehouse");
        user = (UserModel) intent.getSerializableExtra("user");

        getSupportActionBar().setTitle(wh.getName());
        getSupportActionBar().setSubtitle(user.getUsername());

        videoView = findViewById(R.id.videoView);
        btnClose = (Button) findViewById(R.id.btnVideoClose);
        btnAlert = (Button) findViewById(R.id.btnVideoAlert);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date time = new Date();
                createAlert(user.getWhOwnerRef(),user.getUsername(),wh.getName(),time);
            }
        });

        String videoPath = wh.getVideoPath();
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        /*
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);
         */

        videoView.start();
    }

    private void createAlert(String owner, String user, String warehouse, Date time){
        DocumentReference ownerRef = db.collection(UserModel.COLLECTION).document(owner);
        DocumentReference userRef = db.collection(UserModel.COLLECTION).document(user);
        DocumentReference whRef = db.collection(WarehouseModel.COLLECTION).document(warehouse);

        Map<String, Object> mAlert = new HashMap<>();
        mAlert.put("owner",ownerRef);
        mAlert.put("user",userRef);
        mAlert.put("warehouse",whRef);
        mAlert.put("time",time);

        db.collection("alerts")
                .document()
                .set(mAlert)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(VideoActivity.this,"ALERT ADDED",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(VideoActivity.this,"FAILED TO ADD ALERT",Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: failed to add alert\n"+e.getMessage());
                    }
                });
    }
}
