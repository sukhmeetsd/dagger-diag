package com.example.hilt

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Test file for HiltViewModelLineMarkerProvider
 *
 * Expected gutter icon on line 13:
 * - Icon: Class icon
 * - Tooltip: "Navigate to ViewModelComponent"
 */
@HiltViewModel
class HiltTestViewModel @Inject constructor() : ViewModel()
