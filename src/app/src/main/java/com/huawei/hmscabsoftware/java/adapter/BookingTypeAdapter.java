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
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huawei.hmscabsoftware.R;
import com.huawei.hmscabsoftware.java.modal.BookingTypeModal;

import java.util.ArrayList;


/**
 * Create the inner class before extending it, yes obvious, but sometimes can be forgotten
 */
public class BookingTypeAdapter extends RecyclerView.Adapter<BookingTypeAdapter.ViewHolder> {
    /**
     * Sometimes you can have your set of data as empty but initialized and then update it using an update method
     * This is usefull when you want to do an http request, set the adapter empty then when the AsyncTask is done, refresh it
     */
    private final ArrayList<BookingTypeModal> bookingTypeList;
    private int rowIndex = 0;
    private BookingTypeAdapterRecyclerViewItemClickListener bookingTypeAdapterRecyclerViewItemClickListener;

    /**
     * This is contructor. You can pass the data from the View and initialize callback listener for onclick.
     * @param bookingTypeList as ArrayList<BookingTypeModal>
     * @param bookingTypeAdapterRecyclerViewItemClickListener as BookingTypeAdapterRecyclerViewItemClickListener
     */
    public BookingTypeAdapter(ArrayList<BookingTypeModal> bookingTypeList, BookingTypeAdapterRecyclerViewItemClickListener bookingTypeAdapterRecyclerViewItemClickListener) {
        this.bookingTypeList = bookingTypeList;
        this.bookingTypeAdapterRecyclerViewItemClickListener = bookingTypeAdapterRecyclerViewItemClickListener;
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
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.bookingtype_item_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // This is the view you set in the subclass
        holder.bookingTypeButton.setText(bookingTypeList.get(position).getBookingTypeName());
        // Pass object using listener on click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowIndex = position;
                bookingTypeAdapterRecyclerViewItemClickListener.clickOnItem(bookingTypeList.get(position));
                notifyDataSetChanged();
            }
        });
        // Lets change the text color using click position
        if (rowIndex == position) {
            holder.bookingTypeButton.setBackgroundResource(R.color.colorPrimary);
        } else {
            holder.bookingTypeButton.setBackgroundResource(R.color.colorPrimaryDark);
        }
    }

    /**
     * Always start with this method, if return 0; no mather everything else is fine, there will be nothing in the view
     * @return bookingTypeList.size()
     */
    @Override
    public int getItemCount() {
        return bookingTypeList.size();
    }

    /**
     * This inner class have to be created first, so the parent class can extend to it
     * A ViewHolder is the representation of your xml layout R.layout.bookingtype_item_list
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        // Every variable here should be the representation of each xml element in the layout of the row
        Button bookingTypeButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            // You can find your views because the argument view is the inflated layout in the onCreateViewHolder
            bookingTypeButton = itemView.findViewById(R.id.bookingTypeButton);
        }
    }

    /**
     * custom listener interface for selecting booking type
     */
    public interface BookingTypeAdapterRecyclerViewItemClickListener {
        void clickOnItem(BookingTypeModal bookingType);
    }
}
