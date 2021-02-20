package com.lingo.router.runtime

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log

object Router {
    private const val TAG = "Router"
    private const val GENERATED_MAPPING = "com.lingo.router.mapping.generated.RouterMapping"

    private val mapping: HashMap<String, String> = HashMap()

    fun init() {
        try {
            val clazz = Class.forName(GENERATED_MAPPING)
            val method = clazz.getMethod("get")
            val allMapping = method.invoke(null) as Map<String, String>
            if (allMapping.isNotEmpty()) {
                printLog("init: get all mapping:")
                allMapping.onEach {
                    printLog("\t${it.key} -> ${it.value}")
                }
                mapping.putAll(allMapping)
            }
        } catch (e: Throwable) {
            printELog("init: error while init router: $e")
        }
    }

    fun go(context: Context, url: String) {
        // router://lingo/profile?name=lingo
        val uri = Uri.parse(url)
        val scheme = uri.scheme
        val host = uri.host
        val path = uri.path

        var targetActivityClass = ""

        mapping.onEach {

            val rUri = Uri.parse(it.key)
            val rScheme = rUri.scheme
            val rHost = rUri.host
            val rPath = rUri.path

            if (rScheme == scheme && rHost == host && rPath == path) {
                targetActivityClass = it.value
            }

        }

        if (targetActivityClass == "") {
            printELog("go: no destination found.")
            return
        }

        val bundle = Bundle()
        val query = uri.query
        query?.let {
            if (it.length >= 3) {
                it.split("&").onEach { arg ->
                    val splits = arg.split("=")
                    bundle.putString(splits[0], splits[1])
                }
            }
        }

        try {
            val activity = Class.forName(targetActivityClass)
            val intent = Intent(context, activity)
            intent.putExtras(bundle)
            context.startActivity(intent)
        } catch (e: Throwable) {
            printELog("go: error while start activity: $targetActivityClass, e: $e")
        }
    }

    private fun printLog(msg: String) {
        Log.i(TAG, msg)
    }

    private fun printELog(msg: String) {
        Log.e(TAG, msg)
    }
}