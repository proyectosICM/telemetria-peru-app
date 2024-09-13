package com.icm.telemetria_peru_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import android.widget.TextView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.icm.telemetria_peru_app.integration.MqttManager
import org.json.JSONArray

class BatteriesRealTimeDataActivity : AppCompatActivity() {
    private lateinit var batteryInfoContainer: GridLayout
    private lateinit var mqttManager: MqttManager

    private val maxVoltage = 13.0 // Valor máximo de voltaje para calcular el progreso

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_batteries_real_time_data)

        // Vinculando la vista del contenedor de baterías
        batteryInfoContainer = findViewById(R.id.batteryInfoContainer)

        setupMqttClient()
    }

    private fun setupMqttClient() {
        mqttManager = MqttManager(GlobalSettings.BROKER_MQTT_URL, GlobalSettings.TOPIC)
        mqttManager.onMessageReceived = { jsonObject ->
            try {
                val batteriesData = jsonObject.getJSONArray("bateriesData")
                runOnUiThread {
                    batteryInfoContainer.removeAllViews()
                    displayBatteryData(batteriesData)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mqttManager.setupMqttClient()
    }

    private fun displayBatteryData(batteriesData: JSONArray) {
        batteryInfoContainer.columnCount = 3 // Ajustamos a 3 columnas

        for (i in 0 until batteriesData.length()) {
            val battery = batteriesData.getJSONObject(i)
            val batteryName = battery.getString("name")
            val batteryVoltage = battery.getDouble("voltaje")

            // Nombre de la batería
            val batteryNameTextView = TextView(this).apply {
                text = batteryName
                textSize = 18f
                setTextColor(resources.getColor(R.color.white, null))
                textAlignment = View.TEXT_ALIGNMENT_CENTER // Centramos el texto
                layoutParams = GridLayout.LayoutParams().apply {
                    columnSpec = GridLayout.spec(0, 1) // Primera columna
                    rowSpec = GridLayout.spec(i) // Fila en la que se encuentra
                    setMargins(8, 16, 8, 16)
                }
            }

            // Indicador de progreso
            val progressIndicator = CircularProgressIndicator(this).apply {
                id = View.generateViewId()
                layoutParams = GridLayout.LayoutParams().apply {
                    columnSpec = GridLayout.spec(1, 1) // Segunda columna
                    rowSpec = GridLayout.spec(i) // Fila en la que se encuentra
                    width = 100
                    height = 100
                    setMargins(8, 16, 8, 16)
                }
                isIndeterminate = false
                trackThickness = 15
                progress = calculatePercentage(batteryVoltage, maxVoltage)
                setIndicatorColor(getColor(R.color.green))
            }

            // Texto de voltaje
            val batteryVoltageTextView = TextView(this).apply {
                text = "Voltaje: $batteryVoltage V"
                textSize = 18f
                setTextColor(resources.getColor(R.color.white, null))
                textAlignment = View.TEXT_ALIGNMENT_CENTER // Centramos el texto
                layoutParams = GridLayout.LayoutParams().apply {
                    columnSpec = GridLayout.spec(2, 1) // Tercera columna
                    rowSpec = GridLayout.spec(i) // Fila en la que se encuentra
                    setMargins(8, 16, 8, 16)
                }
            }

            // Añadir las vistas al GridLayout
            batteryInfoContainer.addView(batteryNameTextView)
            batteryInfoContainer.addView(progressIndicator)
            batteryInfoContainer.addView(batteryVoltageTextView)
        }
    }

    // Método para calcular el porcentaje basado en el voltaje actual y el máximo
    private fun calculatePercentage(value: Double, max: Double): Int {
        return ((value / max) * 100).toInt()
    }
}
