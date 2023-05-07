package com.jinvita.mykakaomap.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jinvita.mykakaomap.Extras
import com.jinvita.mykakaomap.R

class MapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        Extras.getKeyHash()
    }
}