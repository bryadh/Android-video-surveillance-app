package com.example.warewatch_70.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warewatch_70.R;
import com.example.warewatch_70.interfaces.OnWarehouseListener;
import com.example.warewatch_70.models.WarehouseModel;
import com.example.warewatch_70.views.WarehouseViewHolder;

import java.util.List;

public class WarehouseAdapter extends RecyclerView.Adapter<WarehouseViewHolder> {

    private List<WarehouseModel> whList;

    private OnWarehouseListener onWarehouseListener;

    public WarehouseAdapter(List<WarehouseModel> whList, OnWarehouseListener onWarehouseListener) {
        this.whList = whList;
        this.onWarehouseListener = onWarehouseListener;
    }

    @NonNull
    @Override
    public WarehouseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_rv_home, parent, false);

        return new WarehouseViewHolder(view, onWarehouseListener);
    }

    @Override
    public void onBindViewHolder(@NonNull WarehouseViewHolder holder, int position) {
        holder.updateWithWarehouse(this.whList.get(position));
    }

    @Override
    public int getItemCount() {
        return this.whList.size();
    }


}
