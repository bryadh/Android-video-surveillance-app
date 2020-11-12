package com.example.warewatch_70.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warewatch_70.R;
import com.example.warewatch_70.models.AlertsModel;
import com.example.warewatch_70.models.WarehouseModel;
import com.example.warewatch_70.views.AlertsViewHolder;
import com.example.warewatch_70.views.WarehouseViewHolder;

import java.util.List;

public class AlertsAdapter extends RecyclerView.Adapter<AlertsViewHolder> {

    private List<AlertsModel> alertsList;

    public AlertsAdapter(List<AlertsModel> alertsList) {
        this.alertsList = alertsList;
    }


    @NonNull
    @Override
    public AlertsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_rv_alerts, parent, false);

        return new AlertsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlertsViewHolder holder, int position) {
        holder.updateWithAlerts(this.alertsList.get(position));
    }

    @Override
    public int getItemCount() {
        return this.alertsList.size();
    }
}
