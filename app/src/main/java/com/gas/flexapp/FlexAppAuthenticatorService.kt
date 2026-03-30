package com.gas.flexapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.gas.flexapp.network.AuthService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FlexAppAuthenticatorService : Service() {
    private lateinit var authenticator: FlexAppAccountAuthenticator
    @Inject
    lateinit var authService: AuthService

    override fun onCreate() {
        super.onCreate()
        authenticator = FlexAppAccountAuthenticator(this, authService)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return authenticator.iBinder
    }
}