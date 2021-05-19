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
package com.huawei.hmscabsoftware.java.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.huawei.hms.maps.model.LatLng;
import com.huawei.hmscabsoftware.R;

import static java.lang.Math.abs;
import static java.lang.Math.atan;

/**
 * Map Utils Class
 */
public class MapUtils {

    private static final float FLOAT_MINUS_ONE = -1F;
    private static final int WIDTH_TWENTY = 20;
    private static final int WIDTH_FIFTY = 50;
    private static final int HEIGHT_TWENTY = 20;
    private static final int HEIGHT_HUNDRED = 100;

    /**
     * Display cab icon from resource folder
     * @param context as Context
     * @return Bitmap
     */
    public static Bitmap getCabBitmap(Context context) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_white_car);
        return Bitmap.createScaledBitmap(bitmap, WIDTH_FIFTY, HEIGHT_HUNDRED, false);
    }

    /**
     * Pointing marker on source and destination coordinate with the help of Bitmap
     * @return Bitmap
     */
    public static Bitmap getOriginDestinationMarkerBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(HEIGHT_TWENTY, WIDTH_TWENTY, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        canvas.drawRect(0F, 0F, (float) WIDTH_TWENTY, (float) HEIGHT_TWENTY, paint);
        return bitmap;
    }

    /**
     * Move cab marker on turn left and right along with polyline
     * @param start as LatLng
     * @param end as LatLng
     * @return rotation as Float value.
     */
    public static Float getRotation(LatLng start, LatLng end) {
        Double latDifference = abs(start.latitude - end.latitude);
        Double lngDifference = abs(start.longitude - end.longitude);
        float rotation = FLOAT_MINUS_ONE;
        if (start.latitude < end.latitude && start.longitude < end.longitude) {
            rotation = (float) Math.toDegrees(atan(lngDifference / latDifference));
        }
        else if (start.latitude >= end.latitude && start.longitude < end.longitude) {
            rotation = (float) (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 90);
        }
        else if (start.latitude >= end.latitude && start.longitude >= end.longitude) {
            rotation = (float) (Math.toDegrees(atan(lngDifference / latDifference)) + 180);
        }
        else if (start.latitude < end.latitude && start.longitude >= end.longitude) {
            rotation =
                    (float) (90 - Math.toDegrees(atan(lngDifference / latDifference)) + 270);
        }
        return rotation;
    }
}
