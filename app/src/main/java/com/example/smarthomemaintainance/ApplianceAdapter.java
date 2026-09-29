package com.example.smarthomemaintainance;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ApplianceAdapter
        extends RecyclerView.Adapter<ApplianceAdapter.ApplianceViewHolder> {

    private final Context context;
    private final ArrayList<Appliance> applianceList;

    public ApplianceAdapter(
            Context context,
            ArrayList<Appliance> applianceList
    ) {
        this.context = context;
        this.applianceList = applianceList;
    }

    @NonNull
    @Override
    public ApplianceViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(context).inflate(
                R.layout.item_appliance,
                parent,
                false
        );

        return new ApplianceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ApplianceViewHolder holder,
            int position
    ) {

        Appliance appliance =
                applianceList.get(position);

        holder.tvName.setText(
                appliance.getName()
        );

        holder.tvCategory.setText(
                appliance.getCategory()
        );

        String brandModel =
                appliance.getBrand()
                        + " • "
                        + appliance.getModel();

        holder.tvBrand.setText(
                brandModel
        );

        // Open Appliance Details

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(
                    context,
                    ApplianceDetailsActivity.class
            );

            intent.putExtra(
                    "appliance_id",
                    appliance.getId()
            );

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return applianceList.size();
    }

    static class ApplianceViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvCategory;
        TextView tvBrand;

        public ApplianceViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            tvName = itemView.findViewById(
                    R.id.tvApplianceName
            );

            tvCategory = itemView.findViewById(
                    R.id.tvApplianceCategory
            );

            tvBrand = itemView.findViewById(
                    R.id.tvApplianceBrand
            );
        }
    }
}