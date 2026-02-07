package com.example.anvil

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Test file for ContributesToLineMarkerProvider
 *
 * Expected gutter icon on line 14:
 * - Icon: Push icon (Vcs.Push)
 * - Tooltip: "Navigate to contributed scope Singleton"
 */
@Module
@ContributesTo(Singleton::class)
object AnvilTestModule {

    @Provides
    @Singleton
    fun provideTestString(): String = "Anvil Test"
}
