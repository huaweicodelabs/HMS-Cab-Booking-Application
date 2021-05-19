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
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.huawei.hmscabsoftware.R
import com.huawei.hmscabsoftware.kotlin.modal.CabTypeModal
import com.huawei.hmscabsoftware.kotlin.utils.GlobalValues.RENTAL
import kotlinx.android.synthetic.main.cab_item_list.view.*

/**
 * CabTypeAdapter provide a binding from an app-specific data set to views that are displayed within a RecyclerView.
 * @param cabTypeList as ArrayList<CabTypeModal>
 */
class CabTypeAdapter(
    private val cabTypeList: ArrayList<CabTypeModal>,
) : RecyclerView.Adapter<CabTypeAdapter.ViewHolder>() {
    private var rowIndex = 0
    private var context: Context? = null

    /**
     * This will inflate your view
     * @param parent as ViewGroup
     * @param viewType as int
     * @return ViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cab_item_list, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            cabTypeTextView.text = cabTypeList[position].cabTypeName
            if (rowIndex == position) {
                if (cabTypeList[position].bookingTypeName.contentEquals(RENTAL)) {
                    cabImageView.setBackgroundResource(R.drawable.ic_sedan_fill)
                } else {
                    if (position == 0) {
                        cabImageView.setBackgroundResource(R.drawable.ic_city_fill)
                    }
                    if (position == 1) {
                        cabImageView.setBackgroundResource(R.drawable.ic_sedan_fill)
                    }
                    if (position == 2) {
                        cabImageView.setBackgroundResource(R.drawable.ic_wagonr_fill)
                    }
                }
            } else {
                if (cabTypeList[position].bookingTypeName.contentEquals(RENTAL)) {
                    cabImageView.setBackgroundResource(R.drawable.ic_sedan_empty)
                } else {
                    if (position == 0) {
                        cabImageView.setBackgroundResource(R.drawable.ic_city_empty)
                    }
                    if (position == 1) {
                        cabImageView.setBackgroundResource(R.drawable.ic_sedan_empty)
                    }
                    if (position == 2) {
                        cabImageView.setBackgroundResource(R.drawable.ic_wagonr_empty)
                    }
                }
            }

            itemView.setOnClickListener {
                rowIndex = position
                notifyDataSetChanged()
            }
        }
    }

    /**
     * Always start with this method, if return 0; no mather everything else is fine, there will be nothing in the view
     * @return cabTypeList.size()
     */
    override fun getItemCount(): Int {
        return cabTypeList.size
    }

    /**
     * This inner class have to be created first, so the parent class can extend to it
     * A ViewHolder is the representation of your xml layout R.layout.cab_item_list
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Every variable here should be the representation of each xml element in the layout of the row
        // You can find your views because the argument view is the inflated layout in the onCreateViewHolder
        val cabTypeTextView: TextView = itemView.cabType
        val cabImageView: ImageView = itemView.cabImageView
    }
}
