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

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import com.huawei.hmscabsoftware.R;
import com.huawei.hmscabsoftware.java.view.HuaweiMapActivity;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import okio.BufferedSource;
import okio.Okio;


/**
 * Contains global methods.
 */
public class ViewUtils {

    private static final int PENDING_INTENT_REQUEST_CODE = 0;

    /**
     * Show toast message on UI.
     * @param context as Context
     * @param message as String
     */
    public static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Show progress bar on UI for blocking UI during API call.
     * @param progressBar as ProgressBar
     */
    public static void show(ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Hide progress bar once get response from API.
     * @param progressBar as ProgressBar
     */
    public static void hide(ProgressBar progressBar) {
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Load file from asset folder.
     * @param context as Context
     * @param fileName as String
     * @return String
     */
    public static String loadJSONFromAssets(Context context, String fileName) {
        String result = null;
        try {
            InputStream input = context.getAssets().open(fileName);
            BufferedSource source = Okio.buffer(Okio.source(input));
            result = source.readByteString().string(StandardCharsets.UTF_8);
            return result;
        } catch (IOException e) {
        }
        return result;
    }

    /**
     * Showing notification on notification tray using notificationBuilder
     * @param context as Context
     * @param messageBody as String
     * @param messageTitle as String
     */
    public static void sendNotification(Context context, String messageBody, String messageTitle) {
        Intent intent = new Intent(context, HuaweiMapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                PENDING_INTENT_REQUEST_CODE /* Request code */,
                intent,
                PendingIntent.FLAG_ONE_SHOT
        );
        String channelId = context.getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.round_button_background)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    context.getString(R.string.channel_human_readable_title),
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0, notificationBuilder.build());
    }
}
