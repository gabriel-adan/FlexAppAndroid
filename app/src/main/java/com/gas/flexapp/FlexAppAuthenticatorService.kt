package com.gas.flexapp

import android.app.Service
import android.content.Intent
import android.os.IBinder

class FlexAppAuthenticatorService : Service() {
    private lateinit var authenticator: FlexAppAccountAuthenticator

    override fun onCreate() {
        super.onCreate()
        authenticator = FlexAppAccountAuthenticator(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return authenticator.iBinder
    }
}