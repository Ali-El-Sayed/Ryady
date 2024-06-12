package com.example.ryady.view.screens

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.ryady.R
import com.example.ryady.utils.NetworkMonitor
import com.example.ryady.view.dialogs.offline.view.OfflineDialogFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

open class BaseActivity : AppCompatActivity(), OfflineDialogFragment.OfflineDialogListener {
    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var isConnectedJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
            val transaction = supportFragmentManager.beginTransaction()
            offlineDialogFragment.isCancelable = false
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            transaction.add(offlineDialogFragment, "OfflineDialog").commitAllowingStateLoss()
        }
    }

    override fun onRetry() {
        // Retry logic, e.g., check network again or re-attempt the failed operation
        Toast.makeText(this, "Check Network Again", Toast.LENGTH_SHORT).show()
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
            val transaction = supportFragmentManager.beginTransaction()
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            transaction.remove(offlineDialogFragment!!).commitAllowingStateLoss()
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