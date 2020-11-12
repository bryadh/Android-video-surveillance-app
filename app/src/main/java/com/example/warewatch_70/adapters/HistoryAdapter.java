package com.example.warewatch_70.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warewatch_70.R;
import com.example.warewatch_70.models.AlertsModel;
import com.example.warewatch_70.models.HistoryModel;
import com.example.warewatch_70.views.AlertsViewHolder;
import com.example.warewatch_70.views.HistoryViewHolder;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder> {

    private List<HistoryModel> historyList;

    public HistoryAdapter(List<HistoryModel> historyList) {
        this.historyList = historyList;
    }


    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_rv_history, parent, false);

        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.updateWithHistory(this.historyList.get(position));
    }

    @Override
    public int getItemCount() {
        return this.historyList.size();
    }
}
