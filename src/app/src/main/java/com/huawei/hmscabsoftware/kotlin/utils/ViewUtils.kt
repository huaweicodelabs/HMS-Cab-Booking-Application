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

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.huawei.hmscabsoftware.R
import com.huawei.hmscabsoftware.kotlin.view.HuaweiMapActivity

private const val PENDING_INTENT_REQUEST_CODE = 0

/**
 * Show toast message on UI.
 * @param message as String
 */
fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

/**
 * Show progress bar on UI for blocking UI during API call.
 */
fun ProgressBar.show() {
    visibility = View.VISIBLE
}

/**
 * Hide progress bar once get response from API.
 */
fun ProgressBar.hide() {
    visibility = View.GONE
}

/**
 * Load file from asset folder.
 * @param fileName as String
 */
fun Context.loadJSONFromAssets(fileName: String): String {
    return applicationContext.assets.open(fileName).bufferedReader().use { reader ->
        reader.readText()
    }
}

/**
 * Showing notification on notification tray using notificationBuilder
 * @param messageBody as String
 * @param messageTitle as String
 */
fun Context.sendNotification(messageBody: String, messageTitle: String) {
    val intent = Intent(this, HuaweiMapActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    val pendingIntent = PendingIntent.getActivity(
        this,
        PENDING_INTENT_REQUEST_CODE /* Request code */,
        intent,
        PendingIntent.FLAG_ONE_SHOT
    )
    val channelId = getString(R.string.default_notification_channel_id)
    val defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val notificationBuilder =
        NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.round_button_background)
            .setContentTitle(messageTitle)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
    val notificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Since android Oreo notification channel is needed.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            getString(R.string.channel_human_readable_title),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }
    notificationManager.notify(0, notificationBuilder.build())
}
