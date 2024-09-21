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
    private lateinit var hapticFeedbackUtil: HapticFeedbackUtil  // Utilisation de HapticFeedbackUtil

    // Utilise Triple pour stocker les données X, Y, Z
    private val accelerometerData = mutableListOf<Triple<Float, Float, Float>>()

    private var vibrationIndex = 0

    // Liste des vibrations via HapticFeedbackUtil
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

        // Initialisation de HapticFeedbackUtil
        hapticFeedbackUtil = HapticFeedbackUtil(this)

        // Vérification si l'accéléromètre est disponible
        if (accelerometer == null) {
            println("Aucun capteur d'accéléromètre disponible.")
        }

        // Démarrer la capture des données de l'accéléromètre
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)

        // Récupération du bouton après avoir défini la vue
        val startButton = findViewById<Button>(R.id.button_start_vibration)
        startButton.setOnClickListener {
            startHapticFeedbackSequence()  // Lancer la séquence des vibrations de HapticFeedbackUtil
        }

    }


    fun startHapticFeedbackSequence() {
        if (vibrationIndex < hapticFeedbackMethods.size) {
            // Liste des noms des vibrations correspondant à chaque méthode
             val vibrationNames = listOf(
                "Keyboard Release Feedback",           // keyboardReleaseFeedback
                "Virtual Key Release Feedback",        // virtualKeyReleaseFeedback
                "Clock Tick Feedback",                 // clockTickFeedback
                "Text Handle Move Feedback",           // textHandleMoveFeedback
                "Gesture End Feedback",                // gestureEndFeedback
                "Keyboard Press Feedback",             // keyboardPressFeedback
                "Virtual Key Feedback",                // virtualKeyFeedback
                "Context Click Feedback",              // contextClickFeedback
                "Gesture Start Feedback",              // gestureStartFeedback
                "Confirm Feedback",                    // confirmFeedback
                "Long Press Feedback",                 // longPressFeedback
                "Reject Feedback",                     // rejectFeedback
                "Toggle On Feedback",                  // toggleOnFeedback
                "Toggle Off Feedback",                 // toggleOffFeedback
                "Drag Start Feedback",                 // dragStartFeedback
                "Segment Tick Feedback",               // segmentTickFeedback
                "Segment Frequent Tick Feedback"       // segmentFrequentTickFeedback
            )

            // Afficher le nom de la vibration dans le Logcat
            val vibrationName = vibrationNames[vibrationIndex]
            println("Playing vibration: $vibrationName")

            // Démarre l'enregistrement des données de l'accéléromètre avant la vibration
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)

            // Appelle directement la lambda correspondante à l'index actuel pour jouer la vibration
            hapticFeedbackMethods[vibrationIndex]()

            // Définis la durée de chaque vibration, ici 500ms (ou adapte cette valeur selon chaque vibration)
            val vibrationDuration = 500L  // Ajuste selon la durée de la vibration actuelle

            // Utiliser un Handler pour arrêter la capture des données après la vibration
            val handler = android.os.Handler(Looper.getMainLooper())
            handler.postDelayed({
                // Arrête l'enregistrement des données après la vibration
                stopVibrationAndCaptureData()
            }, vibrationDuration)

            // Incrémentation de l'index pour la prochaine vibration
            vibrationIndex++
        } else {
            println("Toutes les vibrations ont été jouées.")
        }
    }



    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            // Capture des données d'accélération sur les axes X, Y, Z
            val x = it.values[0]
            val y = it.values[1]
            val z = it.values[2]

            // Stocker les données dans un Triple
            accelerometerData.add(Triple(x, y, z))

            // Affichage des données dans le Logcat
            println("Acceleration X: $x, Y: $y, Z: $z")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Pas utilisé dans cet exemple
    }

    fun stopVibrationAndCaptureData() {
        // Arrêt du capteur et affichage des données
        sensorManager.unregisterListener(this)

        // Affichage ou exportation des données
        accelerometerData.forEach { (x, y, z) ->
            println("Acceleration X: $x, Y: $y, Z: $z")
        }
    }

    override fun onPause() {
        super.onPause()
        stopVibrationAndCaptureData()  // Arrêt de la capture de données lors de la pause de l'activité
    }
}
