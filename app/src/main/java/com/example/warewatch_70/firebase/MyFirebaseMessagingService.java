package com.example.warewatch_70.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.warewatch_70.R;
import com.example.warewatch_70.models.AlertsModel;
import com.example.warewatch_70.models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MessagingService";
    private static final String KEY = "idAlert";
    public static final String CURRENT_USER = "currentUser";
    public static final String NULL_VALUE = "nullValue";
    private static final String CHANNEL_ID = "channelId";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private SharedPreferences mPreferences;

    private UserModel currentUser;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        mPreferences = getSharedPreferences(CURRENT_USER,MODE_PRIVATE);

        String spUserName = mPreferences.getString(UserModel.USERNAME,NULL_VALUE);
        String spFirstName = mPreferences.getString(UserModel.FIRSTNAME,NULL_VALUE);
        String spLastName = mPreferences.getString(UserModel.LASTNAME,NULL_VALUE);
        String spPassword = mPreferences.getString(UserModel.PASSWORD,NULL_VALUE);
        String spPhone = mPreferences.getString(UserModel.PHONE,NULL_VALUE);
        boolean spAdmin = mPreferences.getBoolean(UserModel.ADMIN,false);
        boolean spFirstLog = mPreferences.getBoolean(UserModel.FIRSTLOG,false);
        Date spLastUpdate = new Date(mPreferences.getLong(UserModel.LASTUPDATE,0));
        String spWhOwnerRef = mPreferences.getString(UserModel.WHOWNER,NULL_VALUE);

        currentUser = new UserModel(spUserName,spFirstName,spLastName,spPassword,spPhone,spAdmin,spFirstLog,spLastUpdate,spWhOwnerRef);

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            final String alertId = remoteMessage.getData().get(KEY);
            Log.d(TAG, "Message data payload: alert id " + remoteMessage.getData().get(KEY));

            db.collection(AlertsModel.COLLECTION)
                    .document(alertId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            AlertsModel alert = documentSnapshot.toObject(AlertsModel.class);
                            Log.d(TAG, "onSuccess: Retreived the alert of "+alert.getUser().getId()+" on "+alert.getWarehouse().getId());

                            boolean assert01 = alert.getUser().getId().equals(currentUser.getUsername());
                            boolean assert02 = alert.getOwner().getId().equals(currentUser.getWhOwnerRef());

                            Log.d(TAG, "onSuccess: the assertions are "+ (assert01 || assert02));

                            if(assert01 || assert02){
                                Log.d(TAG, "onSuccess: Entering the IF");
                                createNotificationChannel();

                                NotificationCompat.Builder builder = new NotificationCompat.Builder(MyFirebaseMessagingService.this,CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_notifications_active_black_24dp)
                                        .setContentTitle("ALERT")
                                        .setContentText("Une alerte a été lancé")
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                                mNotificationManager.notify(0, builder.build());
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            e.printStackTrace();
                        }
                    });
        }

    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Channel01";
            String description = "Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "createNotificationChannel: NOTIFICATION CHANNEL CREATED");
        }
    }


}
