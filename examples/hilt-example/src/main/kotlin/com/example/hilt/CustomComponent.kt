package com.example.hilt

import dagger.hilt.DefineComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Scope

/**
 * Test file for DefineComponentLineMarkerProvider
 *
 * Expected gutter icon on line 17:
 * - Icon: Class icon
 * - Tooltip: "Navigate to custom Hilt component"
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class CustomScope

@CustomScope
@DefineComponent(parent = SingletonComponent::class)
interface CustomComponent
