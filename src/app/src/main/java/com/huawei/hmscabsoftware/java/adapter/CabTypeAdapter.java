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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hmscabsoftware.R;
import com.huawei.hmscabsoftware.java.modal.CabTypeModal;

import java.util.ArrayList;

import static com.huawei.hmscabsoftware.java.utils.GlobalValues.RENTAL;

/**
 * Create the inner class before extending it, yes obvious, but sometimes can be forgotten
 */
public class CabTypeAdapter extends RecyclerView.Adapter<CabTypeAdapter.ViewHolder> {
    /**
     * Sometimes you can have your set of data as empty but initialized and then update it using an update method
     * This is usefull when you want to do an http request, set the adapter empty then when the AsyncTask is done, refresh it
     */
    private final ArrayList<CabTypeModal> cabTypeList;
    private int rowIndex = 0;

    /**
     * This is contructor. You can pass the data from the View and initialize callback listener for onclick.
     * @param cabTypeList as ArrayList<CabTypeModal>
     */
    public CabTypeAdapter(ArrayList<CabTypeModal> cabTypeList) {
        this.cabTypeList = cabTypeList;
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
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.cab_item_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.cabTypeTextView.setText(cabTypeList.get(position).getCabTypeName());
        if (rowIndex == position) {
            if (cabTypeList.get(position).getBookingTypeName().contentEquals(RENTAL)) {
                holder.cabImageView.setBackgroundResource(R.drawable.ic_sedan_fill);
            } else {
                if (position == 0) {
                    holder.cabImageView.setBackgroundResource(R.drawable.ic_city_fill);
                }
                if (position == 1) {
                    holder.cabImageView.setBackgroundResource(R.drawable.ic_sedan_fill);
                }
                if (position == 2) {
                    holder.cabImageView.setBackgroundResource(R.drawable.ic_wagonr_fill);
                }
            }
        } else {
            if (cabTypeList.get(position).getBookingTypeName().contentEquals(RENTAL)) {
                holder.cabImageView.setBackgroundResource(R.drawable.ic_sedan_empty);
            } else {
                if (position == 0) {
                    holder.cabImageView.setBackgroundResource(R.drawable.ic_city_empty);
                }
                if (position == 1) {
                    holder.cabImageView.setBackgroundResource(R.drawable.ic_sedan_empty);
                }
                if (position == 2) {
                    holder.cabImageView.setBackgroundResource(R.drawable.ic_wagonr_empty);
                }
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowIndex = position;
                notifyDataSetChanged();
            }
        });
    }

    /**
     * Always start with this method, if return 0; no mather everything else is fine, there will be nothing in the view
     * @return cabTypeList.size()
     */
    @Override
    public int getItemCount() {
        return cabTypeList.size();
    }

    /**
     * This inner class have to be created first, so the parent class can extend to it
     * A ViewHolder is the representation of your xml layout R.layout.cab_item_list
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        // Every variable here should be the representation of each xml element in the layout of the row
        private final TextView cabTypeTextView;
        private final ImageView cabImageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // You can find your views because the argument view is the inflated layout in the onCreateViewHolder
            cabTypeTextView = itemView.findViewById(R.id.cabType);
            cabImageView = itemView.findViewById(R.id.cabImageView);
        }
    }
}
