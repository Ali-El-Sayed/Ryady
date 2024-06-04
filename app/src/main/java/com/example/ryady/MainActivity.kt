package com.example.ryady

import android.os.Bundle

import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.CustomerCreateMutation
import com.example.ryady.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
       // init firebase
        FirebaseApp.initializeApp(this)
        val database = FirebaseDatabase.getInstance()
        val customerRef = database.getReference("Customers")
        //val item = CustomerCreateMutation.Customer(firstName = "ahmed", lastName = "ghon", email = "ahmeduuugogo@gmail.com", phone = "+20123456123", acceptsMarketing = true,)
       // customerRef.push().setValue(item)

        // Disable night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        binding.bottomNavigation.setupWithNavController(navHost.navController)

    }


}
