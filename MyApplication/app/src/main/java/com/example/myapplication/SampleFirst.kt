package com.example.myapplication

import android.content.Context
import com.rousetime.android_startup.AndroidStartup


/**
 * kotlin例子，用于反编译成java供查看
 * Tools -> kotlin -> show kotlin bytecode ->Decompile
 */
class SampleFirst : AndroidStartup<String>() {

    override fun callCreateOnMainThread(): Boolean = true

    override fun waitOnMainThread(): Boolean = false

    override fun create(context: Context): String? {
        // todo something
        return this.javaClass.simpleName
    }

    override fun dependenciesByName(): List<String>? {
        return null
    }

}