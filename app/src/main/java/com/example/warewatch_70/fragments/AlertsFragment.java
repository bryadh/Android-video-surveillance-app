package com.example.warewatch_70.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warewatch_70.R;
import com.example.warewatch_70.adapters.AlertsAdapter;
import com.example.warewatch_70.models.AlertsModel;
import com.example.warewatch_70.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;


import java.util.ArrayList;
import java.util.List;

public class AlertsFragment extends Fragment {

    private UserModel currentUser;

    private RecyclerView recyclerView;
    private List<AlertsModel> alertsList;

    private TextView tvAlertsError;

    private AlertsAdapter alertsAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private DocumentReference userRef;

    private ProgressBar progressBar;

    public AlertsFragment(UserModel user) {
        this.currentUser = user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alerts, container, false);

        progressBar = view.findViewById(R.id.pbAlerts);

        tvAlertsError = view.findViewById(R.id.tvAlertsError);

        recyclerView = view.findViewById(R.id.rvAlerts);
        DividerItemDecoration dividerItemDecoration =  new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

        alertsList = new ArrayList<>();

        userRef = db.collection(UserModel.COLLECTION).document(currentUser.getUsername());

        db.collection(AlertsModel.COLLECTION)
                .orderBy(AlertsModel.TIME, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                            AlertsModel alert = document.toObject(AlertsModel.class);

                            Log.d("REFERENCES","user:"+userRef.getPath()+"\t"+"alert:"+alert.getUser().getPath());

                            if(alert.getUser().equals(userRef)){
                                alertsList.add(alert);
                                Log.d("LIST",""+alertsList);
                            }else{
                                Log.d("ALERTS"," REFERENCE ERROR");
                            }
                        }
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("COMPLETED","It's done");
                        alertsAdapter = new AlertsAdapter(alertsList);
                        recyclerView.setAdapter(alertsAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        tvAlertsError.setText(R.string.network_error);
                        e.printStackTrace();
                    }
                });


        return view;
    }
}
