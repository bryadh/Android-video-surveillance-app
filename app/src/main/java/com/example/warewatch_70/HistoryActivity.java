package com.example.warewatch_70;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.warewatch_70.adapters.HistoryAdapter;
import com.example.warewatch_70.models.HistoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private static final String TAG = "HistoryActivity";

    private RecyclerView recyclerView;
    private List<HistoryModel> historyList;

    private HistoryAdapter historyAdapter;

    private TextView tvHistoryError;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("HISTORY");


        tvHistoryError = findViewById(R.id.tvHistoryError);

        progressBar = findViewById(R.id.pbHistory);

        recyclerView = findViewById(R.id.rvHistory);
        DividerItemDecoration dividerItemDecoration =  new DividerItemDecoration(HistoryActivity.this,DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

        historyList = new ArrayList<>();

        db.collection(HistoryModel.COLLECTION)
                .orderBy(HistoryModel.TIME, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                            HistoryModel hModel = document.toObject(HistoryModel.class);
                            historyList.add(hModel);
                            Log.d(TAG, "onSuccess: ITEM"+hModel.toString());
                            Log.d(TAG, "onSuccess: collection found");
                        }
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        Log.d(TAG, "onComplete: LIST LENGTH"+historyList.size());
                        historyAdapter = new HistoryAdapter(historyList);
                        recyclerView.setAdapter(historyAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));

                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        tvHistoryError.setText(R.string.network_error);
                        e.printStackTrace();
                    }
                });
    }
}
