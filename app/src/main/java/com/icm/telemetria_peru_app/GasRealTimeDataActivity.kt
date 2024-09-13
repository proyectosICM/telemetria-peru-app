package com.icm.telemetria_peru_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.icm.telemetria_peru_app.integration.MqttManager

class GasRealTimeDataActivity : AppCompatActivity() {
    private lateinit var circularProgressIndicator: CircularProgressIndicator
    private lateinit var progressText: TextView
    private lateinit var currentPressureText: TextView
    private lateinit var mqttManager: MqttManager
    private lateinit var btnBack: Button
    private val maxPressure = 200f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gas_real_time_data)

        // Vinculando las vistas del XML
        circularProgressIndicator = findViewById(R.id.circularProgressIndicator)
        progressText = findViewById(R.id.progressText)
        currentPressureText = findViewById(R.id.currentPressureText)
        btnBack = findViewById(R.id.btnBack)

        setupMqttClient();
        val progressValue = 0
        updateProgress(calculatePercentage(progressValue.toFloat(), maxPressure), progressValue.toFloat())

        btnBack.setOnClickListener{
            finish()
        }
    }

    private fun updateProgress(percentage: Int, pressureValue: Float) {
        circularProgressIndicator.progress = percentage
        progressText.text = "$pressureValue psi"
        progressText.text = "$pressureValue psi"
        currentPressureText.text = "Presión actual: $pressureValue psi"
    }

    private fun calculatePercentage(value: Float, max: Float): Int {
        return ((value / max) * 100).toInt()
    }

    private fun setupMqttClient(){
        mqttManager = MqttManager(GlobalSettings.BROKER_MQTT_URL, GlobalSettings.TOPIC)
        mqttManager.onMessageReceived = { jsonObject ->
            try {
                val gasInfo = jsonObject.getDouble(GlobalSettings.GAS_INFO_KEY).toFloat()
                val percentage = calculatePercentage(gasInfo, maxPressure)
                runOnUiThread {
                    updateProgress(percentage, gasInfo)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mqttManager.setupMqttClient()
    }
}