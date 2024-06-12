package com.example.ryady.view.dialogs.unRegister.view

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.ryady.databinding.FragmentUnRegisterDialogBinding
import com.example.ryady.view.extensions.move
import com.example.ryady.view.screens.auth.AuthActivity

class UnRegisterDialogFragment : DialogFragment() {

    private val binding: FragmentUnRegisterDialogBinding by lazy { FragmentUnRegisterDialogBinding.inflate(layoutInflater) }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(binding.root)
//        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        binding.registerBtn.setOnClickListener {
            requireActivity().move(requireActivity(), AuthActivity::class.java)
            dismiss()
            requireActivity().finish()
        }

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }
        return dialog

    }


}