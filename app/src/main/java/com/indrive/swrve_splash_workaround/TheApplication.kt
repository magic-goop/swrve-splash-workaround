package com.indrive.swrve_splash_workaround

import android.app.Application
import android.content.Context
import com.swrve.sdk.*
import com.swrve.sdk.config.SwrveConfig

class TheApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val config = SwrveConfig()
        val appId = 123
        val apiKey = "apeyKey"

        SwrveSDKProxy.createProxyInstance(this, appId, apiKey, config)
    }
}

internal object SwrveSDKProxy : SwrveSDK() {

    @Synchronized
    fun createProxyInstance(
        application: Application?,
        appId: Int,
        apiKey: String?,
        config: SwrveConfig
    ): ISwrve {
        if (application == null) {
            SwrveHelper.logAndThrowException("Application is null")
        } else if (SwrveHelper.isNullOrEmpty(apiKey)) {
            SwrveHelper.logAndThrowException("Api key not specified")
        }
        if (!SwrveHelper.sdkAvailable(config.modelBlackList)) {
            instance = SwrveEmptyProxy(application, apiKey)
        }
        if (instance == null) {
            instance = SwrveProxy(application, appId, apiKey, config)
        }
        return instance as ISwrve
    }
}


internal class SwrveProxy(
    application: Application?,
    appId: Int,
    apiKey: String?,
    config: SwrveConfig?,
) : Swrve(application, appId, apiKey, config) {

    override fun loadCampaignFromNotification() {
        val curActivityClass = activityContext.get()?.javaClass ?: return
        if (curActivityClass != SplashActivity::class.java) {
            super.loadCampaignFromNotification()
        }
    }
}

internal class SwrveEmptyProxy(context: Context?, apiKey: String?) : SwrveEmpty(context, apiKey)
