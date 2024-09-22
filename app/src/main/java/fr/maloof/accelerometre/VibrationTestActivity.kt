package fr.maloof.accelerometre

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Looper
import android.os.Vibrator
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import fr.maloof.myaflokkatapp.HapticFeedbackUtil

class VibrationTestActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var vibrator: Vibrator
    private lateinit var hapticFeedbackUtil: HapticFeedbackUtil
    private val alpha = 0.8f  // Le facteur de filtre (ajuste si nécessaire)
    private var gravity = FloatArray(3)
    private var linearAcceleration = FloatArray(3)

    // Utilise Triple pour stocker les données X, Y, Z
    private val accelerometerData = mutableListOf<Triple<Float, Float, Float>>()

    private var vibrationIndex = 0

    private val hapticFeedbackMethods = listOf(
        { hapticFeedbackUtil.keyboardReleaseFeedback() },
        { hapticFeedbackUtil.virtualKeyReleaseFeedback() },
        { hapticFeedbackUtil.clockTickFeedback() },
        { hapticFeedbackUtil.textHandleMoveFeedback() },
        { hapticFeedbackUtil.gestureEndFeedback() },
        { hapticFeedbackUtil.keyboardPressFeedback() },
        { hapticFeedbackUtil.virtualKeyFeedback() },
        { hapticFeedbackUtil.contextClickFeedback() },
        { hapticFeedbackUtil.gestureStartFeedback() },
        { hapticFeedbackUtil.confirmFeedback() },
        { hapticFeedbackUtil.longPressFeedback() },
        { hapticFeedbackUtil.rejectFeedback() },
        { hapticFeedbackUtil.toggleOnFeedback() },
        { hapticFeedbackUtil.toggleOffFeedback() },
        { hapticFeedbackUtil.toggleOffFeedback() },
        { hapticFeedbackUtil.gestureThresholdActivateFeedback() },
        { hapticFeedbackUtil.gestureThresholdDeactivateFeedback() },
        { hapticFeedbackUtil.dragStartFeedback() },
        { hapticFeedbackUtil.segmentTickFeedback() },
        { hapticFeedbackUtil.segmentFrequentTickFeedback() }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vibration_test)

        // Initialisation des capteurs et du vibrateur
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        hapticFeedbackUtil = HapticFeedbackUtil(this)

        // Vérification si l'accéléromètre est disponible
        if (accelerometer == null) {
            println("Aucun capteur d'accéléromètre disponible.")
        }

        // Récupération du bouton après avoir défini la vue
        val startButton = findViewById<Button>(R.id.button_start_vibration)
        startButton.setOnClickListener {
            startHapticFeedbackSequence()  // Lancer la séquence des vibrations de HapticFeedbackUtil
        }
    }

    fun startHapticFeedbackSequence() {
        if (vibrationIndex < hapticFeedbackMethods.size) {
            val vibrationNames = listOf(
                "Keyboard Release Feedback",
                "Virtual Key Release Feedback",
                "Clock Tick Feedback",
                "Text Handle Move Feedback",
                "Gesture End Feedback",
                "Keyboard Press Feedback",
                "Virtual Key Feedback",
                "Context Click Feedback",
                "Gesture Start Feedback",
                "Confirm Feedback",
                "Long Press Feedback",
                "Reject Feedback",
                "Toggle On Feedback",
                "Toggle Off Feedback",
                "Toggle Off Feedback",
                "Gesture Threshold Activate Feedback",
                "Gesture Threshold Deactivate Feedback",
                "Drag Start Feedback",
                "Segment Tick Feedback",
                "Segment Frequent Tick Feedback"
            )

            val vibrationName = vibrationNames[vibrationIndex]
            println("Preparing to play vibration: $vibrationName")

            val handler = android.os.Handler(Looper.getMainLooper())

            // Commence l'enregistrement des données juste avant de jouer la vibration
            println("Starting accelerometer data capture")
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)

            // Lancer la vibration après 1 seconde d'enregistrement des données
            handler.postDelayed({
                println("Playing vibration: $vibrationName")

                // Joue la vibration correspondante à l'index actuel
                hapticFeedbackMethods[vibrationIndex]()

                // Capture les données pendant 2 secondes après la vibration
                handler.postDelayed({
                    println("Fin de la capture après la vibration.")
                    stopVibrationAndCaptureData()  // Arrête l'enregistrement des données
                }, 2000)  // Durée de capture des données après la vibration

            }, 1000)  // Délai de 1 seconde avant la vibration

            vibrationIndex++
        } else {
            println("Toutes les vibrations ont été jouées.")
        }
    }

   /* override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]

            // Stocker les données dans un Triple
            accelerometerData.add(Triple(x, y, z))

            println("Acceleration X: $x, Y: $y, Z: $z")
        }
    }*/

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            // Filtrer la gravité de chaque axe
            gravity[0] = alpha * gravity[0] + (1 - alpha) * it.values[0]
            gravity[1] = alpha * gravity[1] + (1 - alpha) * it.values[1]
            gravity[2] = alpha * gravity[2] + (1 - alpha) * it.values[2]

            // Enlever la gravité pour obtenir l'accélération linéaire (mouvement pur)
            linearAcceleration[0] = it.values[0] - gravity[0]
            linearAcceleration[1] = it.values[1] - gravity[1]
            linearAcceleration[2] = it.values[2] - gravity[2]

            // Stocker les données filtrées
            accelerometerData.add(Triple(linearAcceleration[0], linearAcceleration[1], linearAcceleration[2]))

            println("Linear Acceleration X: ${linearAcceleration[0]}, Y: ${linearAcceleration[1]}, Z: ${linearAcceleration[2]}")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Pas utilisé dans cet exemple
    }

    fun stopVibrationAndCaptureData() {
        // Arrêter l'accéléromètre ici
        sensorManager.unregisterListener(this)
        println("Accéléromètre mis en pause.")
    }

    override fun onPause() {
        super.onPause()
        stopVibrationAndCaptureData()  // Arrêt de la capture de données lors de la pause de l'activité
    }
}
