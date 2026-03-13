package com.example.hilt

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Test file for HiltAndroidAppLineMarkerProvider
 *
 * Expected gutter icon on line 10:
 * - Icon: Class icon
 * - Tooltip: "Navigate to ApplicationC component"
 */
@HiltAndroidApp
class HiltTestApp : Application()
