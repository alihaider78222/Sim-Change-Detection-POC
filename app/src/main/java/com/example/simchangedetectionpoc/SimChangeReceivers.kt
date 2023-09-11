package com.example.simchangedetectionpoc

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class SimChangeReceivers: BroadcastReceiver() {
    private val TAG = "SimChangeReceivers"

    // This is where you'll be receiving the SIM_STATE_CHANGE intent.
    override fun onReceive(p0: Context?, p1: Intent?) {
        var state = ""
        if (p1 != null) {
            state = p1.extras?.getString("ss").toString()
        }
        Log.i(TAG, "SIM State Change Detected $state")
        // State can be ABSENT | READY | LOADED
    }
}