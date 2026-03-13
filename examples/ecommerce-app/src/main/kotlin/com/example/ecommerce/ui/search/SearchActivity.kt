package com.example.ecommerce.ui.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.data.repository.SearchRepository
import com.example.ecommerce.domain.usecase.SearchProductsUseCase
import javax.inject.Inject

class SearchActivity : AppCompatActivity() {

    @Inject
    lateinit var searchViewModel: SearchViewModel

    @Inject
    lateinit var searchProductsUseCase: SearchProductsUseCase

    @Inject
    lateinit var searchRepository: SearchRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // DaggerAppComponent.create().inject(this)

        searchViewModel.initialize()
    }
}
