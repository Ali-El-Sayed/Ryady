package com.example.ryady

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.ryady.databinding.ActivityMainBinding
import com.example.ryady.utils.NetworkMonitor
import com.example.ryady.view.dialogs.offlineDialog.view.OfflineDialogFragment
import kotlinx.coroutines.launch


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), OfflineDialogFragment.OfflineDialogListener {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        // Disable night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        binding.bottomNavigation.setupWithNavController(navHost.navController)

        // Monitor network connectivity state
        networkMonitor = NetworkMonitor(this)
        lifecycleScope.launch {
            networkMonitor.isConnected.collect { isConnected ->
                if (isConnected) dismissOfflineDialog()// Handle network connected
                else showOfflineDialog() // Handle network disconnected
            }
        }
    }

    private fun showOfflineDialog() {
        if (!isOfflineDialogVisible()) {
            val offlineDialogFragment = OfflineDialogFragment()
            offlineDialogFragment.isCancelable = false
            offlineDialogFragment.show(supportFragmentManager, "OfflineDialog")
        }
    }

    override fun onRetry() {
        // Retry logic, e.g., check network again or re-attempt the failed operation
        Toast.makeText(this, "Retry clicked", Toast.LENGTH_SHORT).show()
        // Recheck network status
        networkMonitor.checkNetworkStatus()
    }

    private fun isOfflineDialogVisible(): Boolean {
        val fragment = supportFragmentManager.findFragmentByTag("OfflineDialog")
        return fragment != null
    }

    override fun onExit() {
        finishAffinity()  // Close the app
    }

    private fun dismissOfflineDialog() {
        if (isOfflineDialogVisible()) {
            val offlineDialogFragment = supportFragmentManager.findFragmentByTag("OfflineDialog") as OfflineDialogFragment?
            offlineDialogFragment?.dismiss()
            popAllFragmentsAndReloadFirst()
        }
    }

    // Pop all fragments and reload the start destination
    private fun popAllFragmentsAndReloadFirst() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Pop all fragments
        navController.popBackStack(navController.graph.startDestinationId, true)

        // Navigate to the first fragment (start destination)
        navController.navigate(navController.graph.startDestinationId)
    }
}
