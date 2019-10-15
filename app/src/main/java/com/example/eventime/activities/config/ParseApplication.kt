package com.example.eventime.activities.config

import android.app.Application
import com.parse.Parse

private const val APPLICATION_ID = "YuXG53XCJBkvMBgkj47tmOI71Yxr45YJIKYnwxVc"
private const val CLIENT_KEY = "LkEDsKdOdjnm0I7EKJnzeH1epDvZaqdSE7fOMaLw"
private const val SERVER_URL = "https://parseapi.back4app.com/"

class ParseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Configure parse using parse server url, app id and client Key
        // Look at https://docs.parseplatform.org for more details
        configureParse {
            applicationId(APPLICATION_ID)
            clientKey(CLIENT_KEY)
            server(SERVER_URL)
        }
    }

    /** Initialize Parse using the provided server configurations
     * @param builder a lambda function with a [Parse.Configuration.Builder] as its context or Receiver
     */
    private inline fun configureParse(builder: Parse.Configuration.Builder.() -> Unit) {

        return Parse.initialize(Parse.Configuration.Builder(this).apply(builder).build())
    }
}