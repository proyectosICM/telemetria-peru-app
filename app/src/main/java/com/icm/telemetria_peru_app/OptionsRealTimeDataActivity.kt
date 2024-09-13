package com.icm.telemetria_peru_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout

class OptionsRealTimeDataActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options_real_time_data)

        val gasItemLayout = findViewById<LinearLayout>(R.id.gasItemLayout)
        val batteryItemLayout = findViewById<LinearLayout>(R.id.batteryItemLayout)

        gasItemLayout.setOnClickListener{
            val intent = Intent(this, GasRealTimeDataActivity::class.java )
            startActivity(intent)
        }

        batteryItemLayout.setOnClickListener{
            val intent = Intent(this, BatteriesRealTimeDataActivity::class.java)
            startActivity(intent)
        }
    }
}