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

package com.huawei.hmscabsoftware.kotlin.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.huawei.hmscabsoftware.R
import com.huawei.hmscabsoftware.kotlin.modal.BookingTypeModal
import kotlinx.android.synthetic.main.bookingtype_item_list.view.*

/**
 * BookingTypeAdapter provide a binding from an app-specific data set to views that are displayed within a RecyclerView.
 * @param bookingTypeList as ArrayList<BookingTypeModal>
 * @param bookingTypeAdapterRecyclerViewItemClickListener as BookingTypeAdapterRecyclerViewItemClickListener
 */
class BookingTypeAdapter(private val bookingTypeList: ArrayList<BookingTypeModal>, private var bookingTypeAdapterRecyclerViewItemClickListener: BookingTypeAdapterRecyclerViewItemClickListener) : RecyclerView.Adapter<BookingTypeAdapter.ViewHolder>() {
    private var context: Context? = null
    private var rowIndex = 0

    /**
     * This will inflate your view
     * @param parent as ViewGroup
     * @param viewType as int
     * @return ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.bookingtype_item_list, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            bindItems(bookingTypeList[position])
            // Pass object using listener on click
            itemView.setOnClickListener {
                rowIndex = position
                bookingTypeAdapterRecyclerViewItemClickListener.clickOnItem(bookingTypeList[position])
                notifyDataSetChanged()
            }
            // Lets change the text color using click position
            if (rowIndex == position) {
                bookingTypeButton.setBackgroundResource(R.color.colorPrimary)
            } else {
                bookingTypeButton.setBackgroundResource(R.color.colorPrimaryDark)
            }
        }
    }

    /**
     * Always start with this method, if return 0; no mather everything else is fine, there will be nothing in the view
     * @return bookingTypeList.size()
     */
    override fun getItemCount(): Int {
        return bookingTypeList.size
    }

    /**
     * This inner class have to be created first, so the parent class can extend to it
     * A ViewHolder is the representation of your xml layout R.layout.bookingtype_item_list
     */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // You can find your views because the argument view is the inflated layout in the onCreateViewHolder
        val bookingTypeButton: Button = itemView.bookingTypeButton
        // This is the view you set in the subclass
        fun bindItems(bookingType: BookingTypeModal) {
            bookingTypeButton.text = bookingType.bookingTypeName
        }
    }

    /**
     * custom listener interface for selecting booking type
     */
    interface BookingTypeAdapterRecyclerViewItemClickListener {
        fun clickOnItem(bookingType: BookingTypeModal)
    }
}
