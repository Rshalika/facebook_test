package com.strawhat.providertest

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.facebook.appevents.AppEventsLogger

class CustomApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        println("-=-=-= onCreate")
        createSyncAccount(this)
        val newLogger = AppEventsLogger.newLogger(this)
        newLogger.logEvent("adasd")
        println("-=-=-= event logged")
    }

    private fun createSyncAccount(context: Context) {
        val newAccount = Account("Facebook Test", "com.strawhat.providertest.account")
        val accountManager = context.getSystemService(Context.ACCOUNT_SERVICE) as AccountManager
        try {
            if (accountManager.addAccountExplicitly(newAccount, null, null)) {
                ContentResolver.setIsSyncable(newAccount, ContactsContract.AUTHORITY, 1)
                ContentResolver.setSyncAutomatically(newAccount, ContactsContract.AUTHORITY, true)
            }
        } catch (e: Exception) {
            Log.e("CustomApplication", e.toString())
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        println("-=-=-= attachBaseContext")
        MultiDex.install(this)
    }



}