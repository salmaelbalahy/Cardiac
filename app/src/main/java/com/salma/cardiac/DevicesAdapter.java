package com.salma.cardiac;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder> {

    List<String> mConnectedDeviceList = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.deviceNameTV.setText(mConnectedDeviceList.get(position));
    }

    public void setConnectedDeviceList(List<String> mConnectedDeviceList) {
        this.mConnectedDeviceList = mConnectedDeviceList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mConnectedDeviceList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView deviceNameTV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceNameTV = itemView.findViewById(R.id.deviceNameTV);
        }
    }
}
