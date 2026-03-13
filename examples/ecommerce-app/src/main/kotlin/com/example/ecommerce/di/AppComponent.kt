package com.example.ecommerce.di

import com.example.ecommerce.ui.cart.CartActivity
import com.example.ecommerce.ui.checkout.CheckoutActivity
import com.example.ecommerce.ui.main.MainActivity
import com.example.ecommerce.ui.product.ProductDetailActivity
import com.example.ecommerce.ui.profile.ProfileActivity
import com.example.ecommerce.ui.search.SearchActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NetworkModule::class,
        DatabaseModule::class,
        RepositoryModule::class,
        UseCaseModule::class,
        ViewModelModule::class
    ]
)
interface AppComponent {
    // Inject into activities
    fun inject(activity: MainActivity)
    fun inject(activity: ProductDetailActivity)
    fun inject(activity: CartActivity)
    fun inject(activity: CheckoutActivity)
    fun inject(activity: ProfileActivity)
    fun inject(activity: SearchActivity)
}
