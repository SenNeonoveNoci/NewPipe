package org.schabi.newpipe

import android.app.Application
import android.util.Log
import com.splunk.rum.SplunkRum
import io.opentelemetry.api.common.Attributes
import okhttp3.Call
import okhttp3.OkHttpClient

object SplunkRUMWrapper {

    private const val TAG = "SplunkRUMWrapper"

    /**
     * Environment constants.
     */
    private const val APPLICATION_NAME = "NewPipe"
    private const val DEPLOYMENT_ENVIRONMENT = "testing"
    private const val REALM = "lab0"
    private const val RUM_ACCESS_TOKEN = BuildConfig.RUM_ACCESS_TOKEN

    /**
     * Keep this private and use the SplunkRUMWrapper to access the rum functionality.
     * This will enable us to keep the code in the NewPipe the same even if we change the
     * implementation is Splunk agent.
     */
    private var rum: SplunkRum? = null

    @JvmStatic
    fun install(application: Application) {
        Log.d(TAG, "install() called with: application = $application")

        rum = SplunkRum.builder()
            .setApplicationName(APPLICATION_NAME)
            .setDeploymentEnvironment(DEPLOYMENT_ENVIRONMENT)
            .setRealm(REALM)
            .setRumAccessToken(RUM_ACCESS_TOKEN)
            .enableDebug()
            .build(application)
    }

    @JvmStatic
    fun trackCustomEvent(name: String, attributes: Attributes?) {
        Log.d(TAG, "trackCustomEvent() called with: name = $name, attributes = $attributes")

        rum?.addRumEvent(name, attributes ?: Attributes.empty())
    }

    @JvmStatic
    fun trackException(throwable: Throwable, attributes: Attributes?) {
        Log.d(TAG, "trackException() called with: throwable = $throwable, attributes = $attributes")

        if (attributes != null)
            rum?.addRumException(throwable, attributes)
        else
            rum?.addRumException(throwable)
    }

    @JvmStatic
    fun instrumentOkHttp(okHttpClient: OkHttpClient): Call.Factory? {
        Log.d(TAG, "instrumentOkHttp() called")

        return rum?.createRumOkHttpCallFactory(OkHttpClient())
    }
}
