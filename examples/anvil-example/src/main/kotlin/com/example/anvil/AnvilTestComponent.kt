package com.example.anvil

import com.squareup.anvil.annotations.MergeComponent
import javax.inject.Singleton

/**
 * Test file for MergeComponentLineMarkerProvider
 *
 * Expected gutter icon on line 12:
 * - Icon: Merge icon (Vcs.Merge)
 * - Tooltip: "Navigate to merged Component"
 */
@Singleton
@MergeComponent(Singleton::class)
interface AnvilTestComponent {
    fun inject(app: AnvilTestApp)
}
