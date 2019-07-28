package com.yyp.homesafetydemo

import android.app.Application
import com.onesignal.OneSignal

class ApplicationClass : Application() {
    override fun onCreate() {
        super.onCreate()

        // OneSignal Initialization
        //OneSignal.setLogLevel(OneSignal.LOG_LEVEL.DEBUG, OneSignal.LOG_LEVEL.DEBUG);
        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()
    }
}
