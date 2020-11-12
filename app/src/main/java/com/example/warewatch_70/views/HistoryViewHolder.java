package com.example.warewatch_70.views;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warewatch_70.R;
import com.example.warewatch_70.models.AlertsModel;
import com.example.warewatch_70.models.HistoryModel;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;


public class HistoryViewHolder extends RecyclerView.ViewHolder {

    private TextView tvTime;
    private TextView tvWarehouse;
    private TextView tvUser;


    public HistoryViewHolder(@NonNull View itemView) {
        super(itemView);

        tvTime = itemView.findViewById(R.id.tvRvHistoryTime);
        tvWarehouse = itemView.findViewById(R.id.tvRvHistoryWh);
        tvUser = itemView.findViewById(R.id.tvRvHistoryUser);
    }

    public void updateWithHistory(HistoryModel historyModel){

        String user = historyModel.getUser().getId();
        String wh = historyModel.getWarehouse().getId();
        Date time = historyModel.getTime();

        DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM,new Locale("FR","fr"));

        String sTime = df.format(time);

        tvUser.setText(user);
        tvWarehouse.setText(wh);
        tvTime.setText(sTime);
    }



}
