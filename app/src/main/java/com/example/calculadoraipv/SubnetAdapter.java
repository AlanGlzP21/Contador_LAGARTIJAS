package com.example.calculadoraipv;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SubnetAdapter extends RecyclerView.Adapter<SubnetAdapter.SubnetViewHolder> {

    private List<SubnetInfo> subnetList;

    public SubnetAdapter(List<SubnetInfo> subnetList) {
        this.subnetList = subnetList;
    }

    @NonNull
    @Override
    public SubnetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subnet, parent, false);
        return new SubnetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubnetViewHolder holder, int position) {
        SubnetInfo subnet = subnetList.get(position);
        holder.networkAddress.setText(subnet.getNetworkAddress());
        holder.usableHostRange.setText(subnet.getUsableHostRange());
        holder.broadcastAddress.setText(subnet.getBroadcastAddress());
    }

    @Override
    public int getItemCount() {
        return subnetList.size();
    }

    public static class SubnetViewHolder extends RecyclerView.ViewHolder {
        TextView networkAddress, usableHostRange, broadcastAddress;

        public SubnetViewHolder(@NonNull View itemView) {
            super(itemView);
            networkAddress = itemView.findViewById(R.id.networkAddress);
            usableHostRange = itemView.findViewById(R.id.usableHostRange);
            broadcastAddress = itemView.findViewById(R.id.broadcastAddress);
        }
    }
}
