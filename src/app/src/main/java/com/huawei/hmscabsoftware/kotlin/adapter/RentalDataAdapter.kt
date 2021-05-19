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

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.huawei.hmscabsoftware.R
import com.huawei.hmscabsoftware.kotlin.modal.RentalTypeModal
import kotlinx.android.synthetic.main.rental_item_list.view.*

/**
 * RentalDataAdapter provide a binding from an app-specific data set to views that are displayed within a RecyclerView.
 * @param mDataset as ArrayList<RentalTypeModal>
 * @param rentalRecyclerViewItemClickListener as RentalRecyclerViewItemClickListener
 */
class RentalDataAdapter(
    private val mDataset: ArrayList<RentalTypeModal>,
    private var rentalRecyclerViewItemClickListener: RentalRecyclerViewItemClickListener
) : Adapter<RentalDataAdapter.ViewHolder>() {
    private var rowIndex = 0

    /**
     * This will inflate your view
     * @param parent as ViewGroup
     * @param viewType as int
     * @return ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.rental_item_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            // This is the view you set in the subclass
            rentalTypeTextView.text = mDataset[position].rentalTypeName
            // Pass string using listener on click
            itemView.setOnClickListener {
                rowIndex = position
                notifyDataSetChanged()
                rentalRecyclerViewItemClickListener.clickOnRentalItem(rentalTypeTextView.text.toString())
            }
            if (rowIndex == position) {
                itemView.setBackgroundResource(R.color.colorPrimaryDark)
            } else {
                itemView.setBackgroundResource(R.color.colorPrimary)
            }
        }
    }

    /**
     * Always start with this method, if return 0; no mather everything else is fine, there will be nothing in the view
     * @return mDataset.size()
     */
    override fun getItemCount(): Int {
        return mDataset.size
    }

    /**
     * This inner class have to be created first, so the parent class can extend to it
     * A ViewHolder is the representation of your xml layout R.layout.rental_item_list
     */
    inner class ViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v), View.OnClickListener {
        var rentalTypeTextView: TextView = v.rentalTypeTextView
        init {
            v.setOnClickListener(this)
        }
        override fun onClick(v: View) {
            v.setBackgroundResource(R.drawable.ic_launcher_background)
        }
    }

    /**
     * custom listener interface for selecting rental package
     */
    interface RentalRecyclerViewItemClickListener {
        fun clickOnRentalItem(data: String)
    }
}
