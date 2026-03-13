package com.example.anvil

import com.squareup.anvil.annotations.MergeSubcomponent
import dagger.Subcomponent

/**
 * Test file for MergeSubcomponentLineMarkerProvider
 *
 * Expected gutter icon on line 13:
 * - Icon: Hierarchy icon (Hierarchy.Subtypes)
 * - Tooltip: "Navigate to merged Subcomponent"
 */
@ActivityScope
@MergeSubcomponent(ActivityScope::class)
interface AnvilTestSubcomponent {
    fun inject(activity: AnvilTestActivity)

    @Subcomponent.Factory
    interface Factory {
        fun create(): AnvilTestSubcomponent
    }
}

// Custom scope
@javax.inject.Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope
