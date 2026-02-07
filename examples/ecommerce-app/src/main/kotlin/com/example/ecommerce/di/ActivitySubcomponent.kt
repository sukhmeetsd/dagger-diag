package com.example.ecommerce.di

import com.example.ecommerce.ui.main.MainActivity
import dagger.Subcomponent

/**
 * Test file for SubcomponentLineMarkerProvider
 *
 * Expected gutter icon on line 13:
 * - Icon: Hierarchy icon (Hierarchy.Subtypes)
 * - Tooltip: "Navigate to Dagger graph for ActivitySubcomponent"
 */
@Subcomponent(modules = [ActivityModule::class])
interface ActivitySubcomponent {
    fun inject(activity: MainActivity)

    @Subcomponent.Factory
    interface Factory {
        fun create(): ActivitySubcomponent
    }
}
