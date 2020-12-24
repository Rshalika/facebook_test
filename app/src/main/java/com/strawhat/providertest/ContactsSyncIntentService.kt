package com.strawhat.providertest

import android.Manifest
import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class ContactsSyncIntentService : IntentService("ContactsSyncIntentService") {
    override fun onHandleIntent(intent: Intent?) {
        println("-=-=-= onHandleIntent")
        val task: (() -> Unit)? = null
        task?.let { runTask(it) }
    }


    private fun runTask(task: () -> Unit) {
        runTaskInForeground { task() }
    }


    private fun runTaskInForeground(task: () -> Unit) {
        val channel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
            channel
        )
        startForeground(
            FOREGROUND_PROCESS_ID, NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setContentText(getString(R.string.contactSyncInProgress))
                .build()
        )
        task()
        stopForeground(true)
    }


    companion object {
        private const val FOREGROUND_PROCESS_ID = 356468 //should be unique across app
        private const val CHANNEL_ID = "contact_sync_channel"
        private const val CHANNEL_NAME = "contact_sync_channel_name"
        private const val SYNCHRONIZE = "SYNCHRONIZE"
        private const val RUN_IN_FOREGROUND = "RUN_IN_FOREGROUND"

        fun startSync(ctx: Context) {
            println("-=-=-= startSync")
            startServiceIntent(
                ctx,
                Intent(ctx, ContactsSyncIntentService::class.java).also {
                    it.putExtra(
                        SYNCHRONIZE,
                        true
                    )
                })
        }

        /**
         * Starts given [serviceIntent] with [Context.startForegroundService] or [Context.startService] depending on OS version.
         * Firstly, service is tried to run as background service via [Context.startService].
         * On Android 8+ it can result in exception because of service limitations.
         * Then the service is run as foreground service.
         *
         * If an app does not have a permission to read or write to contacts, given [serviceIntent] is not started.
         *
         * This hack is implemented to try to avoid notification at login page at newer OS.
         * Otherwise a notification appears every time user is navigated to login page (logout, start app).
         * If a force sync at login page is deleted, foreground service can be started in proper way (without try catch).
         */
        private fun startServiceIntent(ctx: Context, serviceIntent: Intent) {
            println("-=-=-= startServiceIntent")
            if (!contactsPermissionsGranted(ctx)) {
                return
            }
            try {
                ctx.startService(serviceIntent)
            } catch (e: IllegalStateException) {
                val foregroundServiceIntent =
                    serviceIntent.apply { putExtra(RUN_IN_FOREGROUND, true) }
                ctx.startForegroundService(foregroundServiceIntent)
            }
        }

        /**
         * Returns true if [Manifest.permission.WRITE_CONTACTS] and [Manifest.permission.READ_CONTACTS] are granted.
         */
        private fun contactsPermissionsGranted(context: Context): Boolean = true


    }
}