package com.strawhat.providertest.auth

import android.app.Service
import android.content.Intent
import android.os.IBinder

class AuthenticatorService : Service() {

    private var mAuthenticator: StubAuthenticator? = null

    override fun onCreate() {
        mAuthenticator = StubAuthenticator(this)
    }

    override fun onBind(intent: Intent): IBinder? = mAuthenticator!!.iBinder
}
