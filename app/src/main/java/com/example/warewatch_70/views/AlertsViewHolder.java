package com.example.warewatch_70.views;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warewatch_70.R;
import com.example.warewatch_70.models.AlertsModel;
import com.example.warewatch_70.models.WarehouseModel;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;


public class AlertsViewHolder extends RecyclerView.ViewHolder {

    private TextView tvTime;
    private TextView tvWarehouse;
    private TextView tvUser;


    public AlertsViewHolder(@NonNull View itemView) {
        super(itemView);

        tvTime = itemView.findViewById(R.id.tvRvAlertTime);
        tvWarehouse = itemView.findViewById(R.id.tvRvAlertWh);
        tvUser = itemView.findViewById(R.id.tvRvAlertUser);
    }

    public void updateWithAlerts(AlertsModel alertsModel){

        String user = alertsModel.getUser().getId();
        String wh = alertsModel.getWarehouse().getId();
        Date time = alertsModel.getTime();

        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM,new Locale("FR","fr"));

        String sTime = df.format(time);

        tvUser.setText(user);
        tvWarehouse.setText(wh);
        tvTime.setText(sTime);
    }



}
