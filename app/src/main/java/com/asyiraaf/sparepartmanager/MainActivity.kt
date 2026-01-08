package com.asyiraaf.sparepartmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.asyiraaf.sparepartmanager.ui.navigation.AppNavigation
import com.asyiraaf.sparepartmanager.viewmodel.SparepartViewModel
import com.asyiraaf.sparepartmanager.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: SparepartViewModel by viewModels {
        ViewModelFactory((application as SparepartApp).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation(viewModel)
        }
    }
}