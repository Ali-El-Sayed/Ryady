package com.example.ryady.view.dialogs.verification

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.ryady.databinding.VerificationDialogBinding


class VerificationDialog : DialogFragment() {
    private lateinit var listener: VerificationDialogListener
    private val binding: VerificationDialogBinding by lazy {
        VerificationDialogBinding.inflate(layoutInflater)
    }

    interface VerificationDialogListener {
        fun checkVerified()
        fun onCancel()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is VerificationDialogListener) listener = context
        else throw RuntimeException("$context must implement VerificationDialogListener")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        binding.btnVerification.setOnClickListener {
            listener.checkVerified()
        }
        binding.btnCancel.setOnClickListener { listener.onCancel() }
        return binding.root
    }
}