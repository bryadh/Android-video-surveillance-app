package com.example.warewatch_70.fragments;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warewatch_70.R;
import com.example.warewatch_70.adapters.WarehouseAdapter;
import com.example.warewatch_70.interfaces.HomeFragmentListener;
import com.example.warewatch_70.interfaces.OnWarehouseListener;
import com.example.warewatch_70.models.UserModel;
import com.example.warewatch_70.models.WarehouseModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements OnWarehouseListener{
    private static final String TAG = "HOMEFRAGMENT";

    private UserModel currentUser;
    private TextView tvHomeError;

    private RecyclerView recyclerView;
    private List<WarehouseModel> whList;

    private WarehouseAdapter adapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private DocumentReference ownerRef;

    private HomeFragmentListener homeFragmentListener;

    private TextView tvDialog;
    private Button btnLive, btnAlert, btnDetails;

    private ProgressBar progressBar;

    public HomeFragment(UserModel user){
        this.currentUser = user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        progressBar = view.findViewById(R.id.pbHome);




        tvHomeError = view.findViewById(R.id.tvHomeError);

        recyclerView = view.findViewById(R.id.rvHome);
        DividerItemDecoration dividerItemDecoration =  new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

        whList = new ArrayList<>();

        ownerRef = db.collection(UserModel.COLLECTION).document(currentUser.getWhOwnerRef());

        Log.d("OWNERREFERENCE",""+ownerRef.getPath());

        /* =============================================================== */
        //LOADING THE WAREHOUSES FROM FIREBASE
        db.collection(WarehouseModel.COLLECTION).get()
            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        WarehouseModel wh = document.toObject(WarehouseModel.class);

                        boolean bool = wh.getOwner().getPath().equals(ownerRef.getPath());
                        Log.d("COMPARISON",""+bool);

                        if(wh.getOwner().getPath().equals(ownerRef.getPath())){
                            whList.add(wh);
                            Log.d("WAREHOUSE",""+wh.getName());
                        }else{
                            Log.d("WAREHOUSE"," REFERENCE ERROR");
                        }

                    }
                }
            })
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    adapter = new WarehouseAdapter(whList,HomeFragment.this);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    progressBar.setVisibility(ProgressBar.INVISIBLE);

                }
            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                tvHomeError.setText(R.string.network_error);
                e.printStackTrace();
            }
        });
        /* =============================================================== */


        return view;
    }

    /* =============================================================== */
    //WAREHOUSE LIST ITEM ON CLICK LISTENER
    @Override
    public void onWarehouseClick(int position) {

        Log.d("HOMEFRAGMENT", "onWarehouseClick: "+whList.get(position).getOwner().getId());

        final String warehouse = whList.get(position).getName();

        // =========================================================================================

        db.collection(WarehouseModel.COLLECTION)
                .document(warehouse)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d(TAG, "onSuccess: successfully loaded warehouse");
                        final WarehouseModel wh = new WarehouseModel(documentSnapshot.getString(WarehouseModel.NAME),
                                documentSnapshot.getString(WarehouseModel.VIDEOPATH));

                        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        View mView = getLayoutInflater().inflate(R.layout.dialog_warehouse,null);

                        tvDialog = mView.findViewById(R.id.tvDialogWH);
                        btnLive = mView.findViewById(R.id.btnDialogLive);
                        btnAlert = mView.findViewById(R.id.btnDialogAlert);
                        btnDetails = mView.findViewById(R.id.btnDialogDetails);

                        tvDialog.setText(wh.getName());
                        btnLive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //adding to history
                                String userRef = currentUser.getUsername();
                                Date time = new Date();
                                createHistory(userRef,wh.getName(),time);

                                //Start video Activity
                                homeFragmentListener.genericMethod(wh, currentUser);

                            }
                        });

                        btnAlert.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String owner = currentUser.getWhOwnerRef();
                                String user = currentUser.getUsername();
                                Date time = new Date();

                                createAlert(owner, user, wh.getName(), time);
                                alertDialog.dismiss();
                            }
                        });

                        alertDialog.setView(mView);
                        alertDialog.show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: failed to load warehouses");
                        e.printStackTrace();
                    }
                });
        // =========================================================================================

        /*
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(warehouse);
        alertDialog.setMessage("DO SOMETHING");

        // ========================== //
        //BUTTON ALERT TO CREATE AN ALERT//
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "ALERT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String owner = currentUser.getWhOwnerRef();
                String user = currentUser.getUsername();
                Date time = new Date();

                createAlert(owner, user, warehouse, time);
                dialog.dismiss();
            }
        });
        // ========================== //

        // ========================== //
        //BUTTON LIVE TO LUNCH VIDEO ACTIVITY
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "LIVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("HOMEFRAGMENT", "onClick: LIVE");

                //Load the warehouse to send as extra
                db.collection("warehouses")
                        .document(warehouse)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                WarehouseModel wh = new WarehouseModel(warehouse,
                                                                       documentSnapshot.getString(WarehouseModel.VIDEOPATH));

                                //adding to history
                                String userRef = currentUser.getUsername();
                                Date time = new Date();
                                createHistory(userRef,warehouse,time);

                                //Start video Activity
                                homeFragmentListener.genericMethod(wh, currentUser);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(),"FAILED TO LOAD VIDEO",Toast.LENGTH_SHORT).show();
                                Log.d("HOMEFRAGMENT", "onFailure: "+e.getMessage());
                            }
                        });
                dialog.dismiss();
            }
        });
        // ========================== //
        alertDialog.show();

        */
    }
    /* =============================================================== */

    /* =============================================================== */
    // CREATE THEN STORE AN ALERT ON FIREBASE
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
                        Toast.makeText(getActivity(),"ALERT ADDED",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"FAILED TO ADD ALERT",Toast.LENGTH_SHORT).show();
                        Log.d("HOMEFRAGMENT", "onFailure: "+e.getMessage());
                    }
                });
    }
    /* =============================================================== */

    /* =============================================================== */
    // CREATE THEN STORE AN HISTORY ITEM ON FIREBASE
    private void createHistory(String user, String warehouse, Date time){
        DocumentReference userRef = db.collection(UserModel.COLLECTION).document(user);
        DocumentReference whRef = db.collection(UserModel.COLLECTION).document(warehouse);

        Map<String, Object> mHistory = new HashMap<>();
        mHistory.put("user",userRef);
        mHistory.put("warehouse",whRef);
        mHistory.put("time",time);

        db.collection("history")
                .document()
                .set(mHistory)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(),"HISTORY UPDATED",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"FAILED TO FAILED TO UPDATE HISTORY",Toast.LENGTH_SHORT).show();
                        Log.d("HOMEFRAGMENT", "onFailure: "+e.getMessage());
                    }
                });
    }
    /* =============================================================== */

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof HomeFragmentListener){
            homeFragmentListener = (HomeFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HomeFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        homeFragmentListener = null;
    }


}
