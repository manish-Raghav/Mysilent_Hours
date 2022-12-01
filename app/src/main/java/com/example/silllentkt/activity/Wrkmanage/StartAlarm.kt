package com.example.silllentkt.activity.Wrkmanage


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.window.SplashScreen
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.silllentkt.activity.Activity.Splash
import com.example.silllentkt.activity.Util.AppConstants
import com.example.silllentkt.activity.Util.StoreSession
import com.example.silllentkt.activity.Util.Utils

import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class StartAlarm @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    private val audioManager: AudioManager by lazy { appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    private val notificationManager: NotificationManager by lazy { appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    override fun doWork(): Result {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    AppConstants.CHANNEL_ID,
                    "Default Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val profileName = "Currently Active Profile: ${inputData.getString("Profile_Name")}"
        val vibrate = inputData.getBoolean("VibrateKey", true)
        val profileEndTime = inputData.getString("EndTimeKey")
        val activeProfileId = inputData.getLong("ActiveProfileId", 0)

        val intent = Intent(applicationContext, Splash::class.java)
        val pi = PendingIntent.getActivity(
            applicationContext,
            333,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        if (StoreSession.readInt(AppConstants.BEGIN_STATUS) == 0)
            StoreSession.writeInt(
                AppConstants.RINGTONE_MODE,
                audioManager.ringerMode
            )
        Utils.sendNotification(applicationContext, profileName, "started", pi)
        StoreSession.writeInt(
            AppConstants.BEGIN_STATUS,
            StoreSession.readInt(AppConstants.BEGIN_STATUS) + 1
        )
        StoreSession.writeString(AppConstants.ACTIVE_PROFILE_NAME, profileName)
        StoreSession.writeLong(AppConstants.ACTIVE_PROFILE_ID, activeProfileId)

        if (vibrate) {
            StoreSession.writeInt(AppConstants.VIBRATE_STATE_ICON, 1)
        } else {
            StoreSession.writeInt(AppConstants.VIBRATE_STATE_ICON, 0)
        }
        StoreSession.writeString(AppConstants.END_TIME, profileEndTime!!)
        audioManager.ringerMode = if (vibrate) {
            AudioManager.RINGER_MODE_VIBRATE
        } else {
            AudioManager.RINGER_MODE_SILENT
        }

        return Result.success()
    }
}