package com.example.ecommerce.di

import com.example.ecommerce.ui.main.MainActivity
import com.example.ecommerce.ui.product.ProductDetailActivity
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector

/**
 * Test file for ContributesAndroidInjectorLineMarkerProvider
 *
 * Expected gutter icons:
 * - Line 18: @ContributesAndroidInjector on mainActivity()
 *   - Icon: ImplementingMethod icon
 *   - Tooltip: "Navigate to generated MainActivity subcomponent"
 *
 * - Line 21: @ContributesAndroidInjector on productDetailActivity()
 *   - Icon: ImplementingMethod icon
 *   - Tooltip: "Navigate to generated ProductDetailActivity subcomponent"
 */
@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun mainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun productDetailActivity(): ProductDetailActivity
}
