package com.example.ui.main

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        setContent {
            MainScreen()
        }
    }
}
