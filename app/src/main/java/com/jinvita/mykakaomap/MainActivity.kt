package com.jinvita.mykakaomap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.jinvita.mykakaomap.ui.MapActivity

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
        private const val PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!checkPermission()) requestPermission() else start()
    }

    private fun start() {
        intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        when {
            // 이전에 한번 거부한 경우, 설명과 함께 권한을 요청
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("설정 - 위치 권한을 허용해 주세요!")
                builder.setCancelable(false)
                builder.setPositiveButton("허용") { _, _ ->
                    ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
                }
                builder.setNegativeButton("거부") { _, _ ->
                    App.showToast("위치 권한이 없어 앱을 종료 합니다.")
                    finish()
                }
                builder.create().show()
            }
            // 최초 권한 요청시
            else -> ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 최초 설치시 리턴
        if (permissions.isEmpty() || grantResults.isEmpty()) return
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    App.debug(TAG, "permission granted all")
                    start()
                } else {
                    // 사용자가 첫번째로 거부한 경우 => 다음번 앱에서 권한 설정 가능
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    )
                        App.showToast("위치 권한이 없어 앱을 종료 합니다.")
                    // 2번 이상 사용자가 거부한 경우 => 설정에서 권한 변경 필요
                    else App.showToast("설정 - 위치 권한을 허용해 주세요!")
                    finish()
                }
                return
            }
        }
    }
}