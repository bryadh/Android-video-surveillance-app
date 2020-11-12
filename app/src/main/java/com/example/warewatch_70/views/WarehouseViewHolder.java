package com.example.warewatch_70.views;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warewatch_70.R;
import com.example.warewatch_70.adapters.WarehouseAdapter;
import com.example.warewatch_70.interfaces.OnWarehouseListener;
import com.example.warewatch_70.models.WarehouseModel;


public class WarehouseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView tv;

    private OnWarehouseListener onWarehouseListener;

    public WarehouseViewHolder(@NonNull View itemView, OnWarehouseListener onWarehouseListener) {
        super(itemView);

        tv = itemView.findViewById(R.id.tvRvHomeItem);

        this.onWarehouseListener = onWarehouseListener;

        itemView.setOnClickListener(this);

    }

    public void updateWithWarehouse(WarehouseModel wh){
        tv.setText(wh.getName());
    }

    @Override
    public void onClick(View v) {

        onWarehouseListener.onWarehouseClick(getAdapterPosition());

    }
}
