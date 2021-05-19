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
package com.huawei.hmscabsoftware.kotlin.utils

import android.content.Context
import android.graphics.*
import com.huawei.hms.maps.model.LatLng
import com.huawei.hmscabsoftware.R
import kotlin.math.abs
import kotlin.math.atan

object MapUtils {

    private const val FLOAT_MINUS_ONE = -1f
    private const val WIDTH_TWENTY = 20
    private const val WIDTH_FIFTY = 50
    private const val HEIGHT_TWENTY = 20
    private const val HEIGHT_HUNDRED = 100

    /**
     * Display cab icon from resource folder
     * @param context as Context
     * @return Bitmap
     */
    fun getCabBitmap(context: Context): Bitmap {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_white_car)
        return Bitmap.createScaledBitmap(bitmap, WIDTH_FIFTY, HEIGHT_HUNDRED, false)
    }

    /**
     * Pointing marker on source and destination coordinate with the help of Bitmap
     * @return Bitmap
     */
    fun getOriginDestinationMarkerBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(HEIGHT_TWENTY, WIDTH_TWENTY, Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        canvas.drawRect(0F, 0F, WIDTH_TWENTY.toFloat(), HEIGHT_TWENTY.toFloat(), paint)
        return bitmap
    }

    /**
     * Move cab marker on turn left and right along with polyline
     * @param start as LatLng
     * @param end as LatLng
     * @return rotation as Float value.
     */
    fun getRotation(start: LatLng, end: LatLng): Float {
        val latDifference: Double = abs(start.latitude - end.latitude)
        val lngDifference: Double = abs(start.longitude - end.longitude)
        var rotation = FLOAT_MINUS_ONE
        when {
            start.latitude < end.latitude && start.longitude < end.longitude -> {
                rotation = Math.toDegrees(atan(lngDifference / latDifference)).toFloat()
            }
            start.latitude >= end.latitude && start.longitude < end.longitude -> {
                rotation = (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 90).toFloat()
            }
            start.latitude >= end.latitude && start.longitude >= end.longitude -> {
                rotation = (Math.toDegrees(atan(lngDifference / latDifference)) + 180).toFloat()
            }
            start.latitude < end.latitude && start.longitude >= end.longitude -> {
                rotation =
                    (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 270).toFloat()
            }
        }
        return rotation
    }
}
