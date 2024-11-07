package com.example.challengeodontoprev

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeNav -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.insuranceNav -> {
                    navController.navigate(R.id.insuranceFragment)
                    true
                }
                R.id.historyNav -> {
                    navController.navigate(R.id.historyFragment)
                    true
                }
                R.id.userNav -> {
                    navController.navigate(R.id.userFragment)
                    true
                }
                else -> false
            }
        }

    }

}