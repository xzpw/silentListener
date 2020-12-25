package com.dm.silentmodesample
import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        startListening(am,lifecycleScope,400) {
            state ->
                Log.d("mylog","state was changed. State is ${state.toString()}" )
        }

    }
    /**
     * @param am: AudioManager is system service for check audio state
     * @param lifecycle: is lifecycle scope for avoid memory leak. Need add dependence in build.gradle:app
     * @param intervalCheck is update interval
     * @param listener is lambda for observe
     */
    fun startListening( am: AudioManager,
                        lifecycle: LifecycleCoroutineScope,
                        intervalCheck: Long = 400,
                        listener: (state: ERingMode) -> Unit ) {

        lifecycle.launch {
            var lastState: ERingMode = checkRingState(am)

            while (true) {
                val newState = checkRingState(am)

                if (lastState != newState) {
                   listener.invoke(newState)
               }
                lastState = newState
                delay(intervalCheck)
            }
        }

    }

    /**
     * @param am: AudioManager is system service for check audio state
     * @return ERingMode is enum of states
     */
    fun checkRingState(am: AudioManager) = when (am.ringerMode) {
        AudioManager.RINGER_MODE_SILENT -> ERingMode.SILENT
        AudioManager.RINGER_MODE_VIBRATE -> ERingMode.VIBRATE
        AudioManager.RINGER_MODE_NORMAL -> ERingMode.NORMAL
        else -> ERingMode.NORMAL
    }
}

enum class ERingMode {
    SILENT, VIBRATE, NORMAL
}

