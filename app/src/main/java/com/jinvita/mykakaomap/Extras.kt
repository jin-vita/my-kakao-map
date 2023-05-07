package com.jinvita.mykakaomap

import android.content.pm.PackageManager
import android.util.Base64
import java.security.MessageDigest

object Extras {
    fun getKeyHash() {
        val information =
            MyApp.getContext().packageManager.getPackageInfo(
                MyApp.getContext().packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            )
        val signatures = information.signingInfo.apkContentsSigners
        for (signature in signatures) {
            val md = MessageDigest.getInstance("SHA").apply { update(signature.toByteArray()) }
            val hashCode = String(Base64.encode(md.digest(), 0))
            println("hashCode -> $hashCode")
        }
    }
}