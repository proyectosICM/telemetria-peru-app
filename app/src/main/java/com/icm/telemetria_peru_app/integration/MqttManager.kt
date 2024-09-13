package com.icm.telemetria_peru_app.integration

import android.util.Log
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.json.JSONObject

class MqttManager(private val brokerUrl: String, private val topic: String) : MqttCallback {

    private lateinit var mqttClient: MqttClient
    var onMessageReceived: ((JSONObject) -> Unit)? = null

    fun setupMqttClient() {
        try {
            mqttClient = MqttClient(brokerUrl, MqttClient.generateClientId(), null)
            val options = MqttConnectOptions()
            options.isCleanSession = true
            mqttClient.connect(options)
            mqttClient.setCallback(this)
            mqttClient.subscribe(topic)

            Log.i("MQTT", "Conectado a MQTT Broker y suscrito al tópico $topic")
        } catch (e: MqttException) {
            Log.e("MQTT", "Error al conectar o suscribir al broker MQTT", e)
            e.printStackTrace()
        }
    }

    override fun connectionLost(cause: Throwable?) {
        Log.e("MQTT", "Conexión perdida: ${cause?.message}")
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        val messageStr = message?.toString()
        Log.d("MQTT", "Mensaje recibido: $messageStr")

        try {
            // Parsear el mensaje JSON
            val jsonObject = JSONObject(messageStr)

            // Notificar a la actividad sobre el nuevo mensaje JSON
            onMessageReceived?.invoke(jsonObject)
        } catch (e: Exception) {
            Log.e("MQTT", "Error al procesar el mensaje JSON", e)
        }
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
        // No es necesario manejar en este caso
    }
}
