package com.example.myapplication

import android.content.Context
import com.rousetime.android_startup.AndroidStartup





class SampleSecond : AndroidStartup<Boolean>() {

    override fun callCreateOnMainThread(): Boolean = false

    override fun waitOnMainThread(): Boolean = true

    override fun create(context: Context): Boolean {
        // Simulation execution time.
        Thread.sleep(5000)
        return true
    }

    override fun dependenciesByName(): List<String> {
        return listOf("com.rousetime.sample.startup.SampleFirstStartup")
    }

}

