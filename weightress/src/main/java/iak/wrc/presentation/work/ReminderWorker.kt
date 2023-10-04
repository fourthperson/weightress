package iak.wrc.presentation.work

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import iak.wrc.R
import iak.wrc.presentation.ui.MainActivity
import timber.log.Timber

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) :
    Worker(appContext, workerParams) {

    private val notificationChannelId = "wc_reminders_channel"

    private val context = appContext.applicationContext

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override fun doWork(): Result {
        try {
            // show notification
            // create notification channel
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(
                    notificationChannelId,
                    "Reminder Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Reminders to record your weight"
                }
                notificationManager.createNotificationChannel(notificationChannel)
            }

            // check notification permission
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Timber.e("POST_NOTIFICATIONS permissions not allowed. Unable to display notification")
                return Result.success()
            }

            // build notification
            val notificationBuilder = NotificationCompat.Builder(context, notificationChannelId)
                .setSmallIcon(R.drawable.ic_stat_scale)
                .setContentTitle("It's time to record your weight")
                .setContentText("Click here to open Weightress")
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText("Click here to open Weightress and record your weight for today!")
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // create action to do when notification is tapped
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
            // add action to builder
            notificationBuilder.setContentIntent(pendingIntent)
            notificationBuilder.setAutoCancel(true)
            // build and show notification
            val notification: Notification = notificationBuilder.build()
            // show notification
            NotificationManagerCompat.from(context)
                .notify(System.currentTimeMillis().toInt(), notification)
            // return success
            return Result.success()
        } catch (e: Exception) {
            Timber.e(e)
            return Result.failure()
        }
    }

}