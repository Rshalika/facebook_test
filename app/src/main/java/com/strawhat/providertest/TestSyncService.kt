package com.strawhat.providertest

import android.accounts.Account
import android.accounts.OperationCanceledException
import android.app.Service
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log

class TestSyncService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        println("-=-=-= onBind")
        return SyncAdapterImpl.getInstance(applicationContext).syncAdapterBinder
    }


    private class SyncAdapterImpl(mContext: Context) : AbstractThreadedSyncAdapter(mContext, true) {

        companion object {
            @Volatile
            private var INSTANCE: SyncAdapterImpl? = null

            fun getInstance(context: Context): SyncAdapterImpl = INSTANCE ?: synchronized(this) {
                INSTANCE ?: SyncAdapterImpl(context).also { INSTANCE = it }
            }
        }

        override fun onPerformSync(
            account: Account,
            extras: Bundle,
            authority: String,
            provider: ContentProviderClient,
            syncResult: SyncResult
        ) {
            try {
                Log.d("TAG", "adapter onPerformSync sync called for Facebook test  app!")
                ContactsSyncIntentService.startSync(context)
            } catch (e: OperationCanceledException) {
                Log.e("TAG", "Synchronization operation was canceled.")
            }
        }
    }
}