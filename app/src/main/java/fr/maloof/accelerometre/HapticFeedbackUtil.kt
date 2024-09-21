package fr.maloof.myaflokkatapp

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator

class HapticFeedbackUtil(context: Context) {

    private val vibrator: Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    // KEYBOARD_RELEASE
    fun keyboardReleaseFeedback() {
        val pattern = longArrayOf(0, 50, 50, 100)
        vibratePattern(pattern)
    }

    // VIRTUAL_KEY_RELEASE
    fun virtualKeyReleaseFeedback() {
        val pattern = longArrayOf(0, 30, 30, 70)
        vibratePattern(pattern)
    }

    // CLOCK_TICK
    fun clockTickFeedback() {
        val pattern = longArrayOf(0, 10, 20, 10)
        vibratePattern(pattern)
    }

    // TEXT_HANDLE_MOVE
    fun textHandleMoveFeedback() {
        val pattern = longArrayOf(0, 40, 40, 80)
        vibratePattern(pattern)
    }

    // GESTURE_END
    fun gestureEndFeedback() {
        val pattern = longArrayOf(0, 60, 60, 120)
        vibratePattern(pattern)
    }

    // KEYBOARD_PRESS
    fun keyboardPressFeedback() {
        vibrateOneShot(50)
    }

    // VIRTUAL_KEY
    fun virtualKeyFeedback() {
        vibrateOneShot(30)
    }

    // CONTEXT_CLICK
    fun contextClickFeedback() {
        vibrateOneShot(100)
    }

    // GESTURE_START
    fun gestureStartFeedback() {
        val pattern = longArrayOf(0, 70, 70, 140)
        vibratePattern(pattern)
    }

    // CONFIRM
    fun confirmFeedback() {
        vibrateOneShot(200)
    }

    // LONG_PRESS
    fun longPressFeedback() {
        vibrateOneShot(400)
    }

    // REJECT
    fun rejectFeedback() {
        val pattern = longArrayOf(0, 50, 50, 50, 50, 50)
        vibratePattern(pattern)
    }

    // TOGGLE_ON
    fun toggleOnFeedback() {
        vibrateOneShot(150)
    }

    // TOGGLE_OFF
    fun toggleOffFeedback() {
        vibrateOneShot(100)
    }

    // GESTURE_THRESHOLD_ACTIVATE
    fun gestureThresholdActivateFeedback(vibrator: Vibrator) {
        val pattern = longArrayOf(0, 200, 50, 200)
        vibratePattern(pattern)
    }

    // GESTURE_THRESHOLD_DEACTIVATE
    fun gestureThresholdDeactivateFeedback(vibrator: Vibrator) {
        val pattern = longArrayOf(0, 200, 50, 100)
        vibratePattern(pattern)
    }





    // DRAG_START
    fun dragStartFeedback() {
        val pattern = longArrayOf(0, 100, 50, 100)
        vibratePattern(pattern)
    }

    // SEGMENT_TICK
    fun segmentTickFeedback() {
        vibrateOneShot(10)
    }

    // SEGMENT_FREQUENT_TICK
    fun segmentFrequentTickFeedback() {
        vibrateOneShot(5)
    }

    private fun vibratePattern(pattern: LongArray) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createWaveform(pattern, -1)
            vibrator.vibrate(effect)
        } else {
            vibrator.vibrate(pattern, -1)
        }
    }

    private fun vibrateOneShot(milliseconds: Long) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val effect = VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(effect)
        } else {
            vibrator.vibrate(longArrayOf(0, milliseconds), -1)
        }
    }
}
