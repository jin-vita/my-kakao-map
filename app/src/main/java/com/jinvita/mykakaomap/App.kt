package com.jinvita.mykakaomap

import android.util.Log
import android.widget.Toast

class App {
    companion object {
        var isDebug = true
        var isError = true
        fun debug(tag: String, msg: String) {
            if (isDebug) Log.d(tag, msg)
        }

        fun error(tag: String, msg: String) {
            if (isError) Log.e(tag, msg)
        }

        fun error(tag: String, msg: String, ex: Exception) {
            if (isError) Log.e(tag, msg, ex)
        }

        fun showToast(msg: String) {
            if (::toast.isInitialized) toast.cancel()
            toast = Toast.makeText(MyApp.getContext(), msg, Toast.LENGTH_SHORT)
            toast.show()
        }

        private lateinit var toast: Toast
    }
}