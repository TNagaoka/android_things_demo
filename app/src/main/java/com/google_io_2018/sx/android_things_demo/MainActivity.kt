package com.google_io_2018.sx.android_things_demo

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.android.things.pio.PeripheralManager
import com.google.android.things.pio.Gpio
import java.io.IOException
import com.google.android.things.pio.GpioCallback

class MainActivity : Activity() {
    private val TAG = "MainActivity"
    private val LED_PIN_NAME = "BCM6"
    private val BUTTON_PIN_NAME = "BCM21"
    private lateinit var mLedGpio: Gpio
    private lateinit var mButtonGpio: Gpio

    // Callback for button
    private val mCallback = object : GpioCallback {
        override fun onGpioEdge(gpio: Gpio?): Boolean {
            Log.i(TAG, "GPIO changed, button pressed")

            val mLedGpio = mLedGpio ?: return false
            try {
                mLedGpio.value = !mLedGpio.value
            } catch (e: IOException) {
                Log.e(TAG, "Error on PeripheralIO API", e)
            }

            return true
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val service = PeripheralManager.getInstance()

        val pioService = PeripheralManager.getInstance()
        for (name in pioService.gpioList) {
            Log.d(TAG, "gpio : $name")
        }

        Log.d(TAG, "Available GPIO: " + service.gpioList)

        try {
            // Setup LED
            mLedGpio = service.openGpio(LED_PIN_NAME)
            mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
            mLedGpio.value = false
            Log.i(TAG, "Setup LED pin")
            // Setup Button
            mButtonGpio = service.openGpio(BUTTON_PIN_NAME)
            mButtonGpio.setDirection(Gpio.DIRECTION_IN)
            mButtonGpio.setEdgeTriggerType(Gpio.EDGE_FALLING)
            mButtonGpio.registerGpioCallback(mCallback)
            Log.i(TAG, "Setup Button pin")
        } catch (e: IOException) {
            Log.e(TAG, "Error on PeripheralIO API", e)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mButtonGpio.unregisterGpioCallback(mCallback)
        try {
            mLedGpio.close()
            mButtonGpio.close()
        } catch (e: IOException) {
            Log.e(TAG, "Error on PeripheralIO API", e)
        }
    }
}