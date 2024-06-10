package com.example.ryady.view.screens.home

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
import com.example.ryady.R
import com.example.ryady.databinding.ActivityMainBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.utils.NetworkMonitor
import com.example.ryady.view.dialogs.offlineDialog.view.OfflineDialogFragment
import com.example.ryady.view.screens.settings.currency.TheExchangeRate
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), OfflineDialogFragment.OfflineDialogListener {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var isConnectedJob: Job
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TheExchangeRate.initialize(applicationContext,
            RemoteDataSource.getInstance(client = GraphqlClient.apiService),lifecycleScope)
        enableEdgeToEdge()
        // Disable night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        binding.bottomNavigation.setupWithNavController(navHost.navController)

        // Monitor network connectivity state
        networkMonitor = NetworkMonitor(this)
        isConnectedJob = lifecycleScope.launch {
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

    override fun onDestroy() {
        super.onDestroy()
        isConnectedJob.cancel()
    }
}