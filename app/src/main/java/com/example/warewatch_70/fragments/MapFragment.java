package com.example.warewatch_70.fragments;

import android.Manifest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.warewatch_70.R;
import com.example.warewatch_70.interfaces.HomeFragmentListener;
import com.example.warewatch_70.interfaces.ReloadMapFragment;
import com.example.warewatch_70.models.UserModel;
import com.example.warewatch_70.models.WarehouseModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MapFragment extends Fragment {
    private final static String TAG = "MAPFRAGMENT";

    private GoogleMap mMap;
    private UserModel currentUser;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient client;

    private ReloadMapFragment reloader;

    private HomeFragmentListener homeFragmentListener;

    //private Button btn;

    private LocationManager locationManager;

    private TextView tvDialog;
    private Button btnLive, btnAlert, btnDetails;

    private FirebaseFirestore db;

    public MapFragment(UserModel user) {
        this.currentUser = user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        //btn = view.findViewById(R.id.btnMap);
        //if(checkGpsStatus()){
            Log.d(TAG, "onCreateView: gps status:"+checkGpsStatus());

            //btn.setVisibility(Button.INVISIBLE);
            //btn.setEnabled(false);

            db = FirebaseFirestore.getInstance();

            mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            if(mapFragment == null){
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                mapFragment = SupportMapFragment.newInstance();
                fragmentTransaction.replace(R.id.map, mapFragment).commit();
            }

            client = LocationServices.getFusedLocationProviderClient(getActivity());

            //Check permission
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    getCurrentLocation();
            }else{
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        44);
            }
            /*
        }else{
            Log.d(TAG, "onCreateView: gps status:"+checkGpsStatus());

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!checkGpsStatus()){
                        Log.d(TAG, "onClick: HERE WHEN GPS IS NOT ENABLED");
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }else{
                        Log.d(TAG, "onClick: HERE WHEN GPS IS ENABLE ("+checkGpsStatus()+")");
                        reloader.reloadMap();
                    }

                }
            });
        }
        */

        return view;
    }

    private void getCurrentLocation(){
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if(location != null){

                    //Sync map with fragment
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(final GoogleMap googleMap) {
                            //create new position
                            final LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());

                            //create new marker
                            MarkerOptions options = new MarkerOptions().position(latLng)
                                                                       .title("ME");
                            //zoom
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));

                            //add marker to the map
                            googleMap.addMarker(options);

                            //add warehouses markers
                            db.collection(WarehouseModel.COLLECTION)
                              .get()
                              .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                  @Override
                                  public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                      for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                                          WarehouseModel wh = documentSnapshot.toObject(WarehouseModel.class);

                                          LatLng latLng1 = new LatLng(wh.getPosition().getLatitude(),wh.getPosition().getLongitude());
                                          MarkerOptions whOption = new MarkerOptions().position(latLng1).title(wh.getName());
                                          googleMap.addMarker(whOption);
                                      }
                                  }
                              })
                              .addOnFailureListener(new OnFailureListener() {
                                  @Override
                                  public void onFailure(@NonNull Exception e) {
                                      Log.d(TAG, "onFailure: failed to mark warehouses"+e.getMessage());
                                  }
                              });

                            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {

                                    if(marker.getTitle().equals("ME")){
                                        //do nothing
                                    }else{
                                        Log.d(TAG, "onMarkerClick: clicked on "+marker.getTitle());
                                        db.collection(WarehouseModel.COLLECTION)
                                                .document(marker.getTitle())
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
                                    }
                                    return false;
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    public boolean checkGpsStatus(){
        boolean ret;

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        ret = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return ret;
    }

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 44){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof ReloadMapFragment){
            reloader = (ReloadMapFragment) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ReloadMapFragment");
        }

        if(context instanceof HomeFragmentListener){
            homeFragmentListener = (HomeFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement HomeFragmentListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        reloader = null;
        homeFragmentListener = null;
    }
}
