package com.example.ryady.view.dialogs.offlineDialog.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.ryady.databinding.FragmentOfflineDialogBinding

class OfflineDialogFragment : DialogFragment() {

    private lateinit var listener: OfflineDialogListener
    private val binding: FragmentOfflineDialogBinding by lazy {
        FragmentOfflineDialogBinding.inflate(layoutInflater)
    }

    interface OfflineDialogListener {
        fun onRetry()
        fun onExit()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OfflineDialogListener) listener = context
        else throw RuntimeException("$context must implement OfflineDialogListener")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding.retryButton.setOnClickListener {
            listener.onRetry()
//            dismiss()
        }
        binding.exitButton.setOnClickListener { listener.onExit() }
        return binding.root
    }
}