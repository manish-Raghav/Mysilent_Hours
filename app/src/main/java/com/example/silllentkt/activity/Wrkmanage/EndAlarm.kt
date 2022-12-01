package com.example.silllentkt.activity.Wrkmanage

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
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
class EndAlarm @AssistedInject constructor(@Assisted appContext: Context, @Assisted workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    private val audioManager: AudioManager by lazy { appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    override fun doWork(): Result {
        val profileName = inputData.getString("Profile_Name")
        if (StoreSession.readInt(AppConstants.BEGIN_STATUS) > 0)
            StoreSession.writeInt(
                AppConstants.BEGIN_STATUS,
                StoreSession.readInt(AppConstants.BEGIN_STATUS) - 1
            )
        audioManager.ringerMode =
            if (StoreSession.readInt(AppConstants.BEGIN_STATUS) > 0) {
                if (StoreSession.readInt(AppConstants.VIBRATE_STATE_ICON) == 1)
                    AudioManager.RINGER_MODE_VIBRATE
                else
                    AudioManager.RINGER_MODE_SILENT
            } else AudioManager.RINGER_MODE_NORMAL
        val intent = Intent(applicationContext, Splash::class.java)
        val pi = PendingIntent.getActivity(
            applicationContext,
            333,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        Utils.sendNotification(applicationContext, profileName!!, "ended", pi)

        return Result.success()
    }
}