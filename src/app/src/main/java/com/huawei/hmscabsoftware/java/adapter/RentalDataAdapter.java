/*
 * Copyright 2021. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.huawei.hmscabsoftware.java.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hmscabsoftware.R;
import com.huawei.hmscabsoftware.java.modal.RentalTypeModal;

import java.util.ArrayList;


/**
 * Create the inner class before extending it, yes obvious, but sometimes can be forgotten
 */
public class RentalDataAdapter extends RecyclerView.Adapter<RentalDataAdapter.ViewHolder> {
    /**
     * Sometimes you can have your set of data as empty but initialized and then update it using an update method
     * This is usefull when you want to do an http request, set the adapter empty then when the AsyncTask is done, refresh it
     */
    private final ArrayList<RentalTypeModal> mDataset;
    private int rowIndex = 0;
    private RentalRecyclerViewItemClickListener rentalRecyclerViewItemClickListener;

    /**
     * This is contructor. You can pass the data from the View and initialize callback listener for onclick.
     * @param mDataset as ArrayList<RentalTypeModal>
     * @param rentalRecyclerViewItemClickListener as RentalRecyclerViewItemClickListener
     */
    public RentalDataAdapter(ArrayList<RentalTypeModal> mDataset, RentalRecyclerViewItemClickListener rentalRecyclerViewItemClickListener) {
        this.mDataset = mDataset;
        this.rentalRecyclerViewItemClickListener = rentalRecyclerViewItemClickListener;
    }

    /**
     * This will inflate your view
     * @param parent as ViewGroup
     * @param viewType as int
     * @return ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.rental_item_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // This is the view you set in the subclass
        holder.rentalTypeTextView.setText(mDataset.get(position).getRentalTypeName());
        // Pass string using listener on click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowIndex = position;
                notifyDataSetChanged();
                rentalRecyclerViewItemClickListener.clickOnRentalItem(holder.rentalTypeTextView.getText().toString());
            }
        });

        if (rowIndex == position) {
            holder.itemView.setBackgroundResource(R.color.colorPrimaryDark);
        } else {
            holder.itemView.setBackgroundResource(R.color.colorPrimary);
        }
    }

    /**
     * Always start with this method, if return 0; no mather everything else is fine, there will be nothing in the view
     * @return mDataset.size()
     */
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    /**
     * This inner class have to be created first, so the parent class can extend to it
     * A ViewHolder is the representation of your xml layout R.layout.rental_item_list
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        // Every variable here should be the representation of each xml element in the layout of the row
        TextView rentalTypeTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // You can find your views because the argument view is the inflated layout in the onCreateViewHolder
            rentalTypeTextView = itemView.findViewById(R.id.rentalTypeTextView);
        }
    }

    /**
     * custom listener interface for selecting booking type
     */
    public interface RentalRecyclerViewItemClickListener {
        void clickOnRentalItem(String data);
    }
}
