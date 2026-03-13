package com.example.hilt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Test file for AndroidEntryPointLineMarkerProvider
 *
 * Expected gutter icon on line 13:
 * - Icon: InspectionsOK icon
 * - Tooltip: "Navigate to ActivityComponent" (inferred from AppCompatActivity superclass)
 */
@AndroidEntryPoint
class HiltTestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}
