package com.example.hilt

import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Test file for AndroidEntryPointLineMarkerProvider
 *
 * Expected gutter icon on line 11:
 * - Icon: InspectionsOK icon
 * - Tooltip: "Navigate to FragmentComponent" (inferred from Fragment superclass)
 */
@AndroidEntryPoint
class HiltTestFragment : Fragment()
