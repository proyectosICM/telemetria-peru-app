package com.icm.telemetria_peru_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.icm.telemetria_peru_app.integration.MqttManager

class VehicleRealTimeDataActivity : AppCompatActivity() {

    private lateinit var circularProgressIndicator: CircularProgressIndicator
    private lateinit var progressText: TextView
    private lateinit var mqttManager: MqttManager
    private val brokerUrl = "tcp://telemetriaperu.com:1883"
    private val topic = "tmp_gasPressure/1"
    private val maxPressure = 200f // Define el valor máximo para la presión

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_real_time_data)

        // Vinculando las vistas del XML
        circularProgressIndicator = findViewById(R.id.circularProgressIndicator)
        progressText = findViewById(R.id.progressText)


        setupMqttClient();
        val progressValue = 0
        updateProgress(calculatePercentage(progressValue.toFloat(), maxPressure), progressValue.toFloat())
    }

    private fun updateProgress(percentage: Int, pressureValue: Float) {
        circularProgressIndicator.progress = percentage
        progressText.text = "$pressureValue psi"
    }

    private fun calculatePercentage(value: Float, max: Float): Int {
        return ((value / max) * 100).toInt()
    }

    private fun setupMqttClient(){
        mqttManager = MqttManager(brokerUrl, topic)
        mqttManager.onMessageReceived = { jsonObject ->
            try {
                val gasInfo = jsonObject.getDouble("gasInfo").toFloat()
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
