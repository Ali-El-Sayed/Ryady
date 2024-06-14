package com.example.ryady.view.screens.splash

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ryady.R
import com.example.ryady.databinding.FragmentSplashBinding
import com.example.ryady.utils.readCustomerData
import com.example.ryady.view.extensions.move
import com.example.ryady.view.screens.home.MainActivity
import kotlinx.coroutines.launch

private const val TAG = "SplashFragment"
class SplashFragment : Fragment() {


    lateinit var binding: FragmentSplashBinding
    lateinit var userToken : String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title.animation = AnimationUtils.loadAnimation(requireContext(),R.anim.bottom_animation)
        binding.subTitle.animation = AnimationUtils.loadAnimation(requireContext(),R.anim.bottom_animation)
        lifecycleScope.launch {
            readCustomerData(requireContext()){
                userToken = it["user token"] ?:""
            }
        }
        val handler = Handler()
        handler.postDelayed({
            Log.i(TAG, "onViewCreated: $userToken")
            if (userToken.isNotEmpty()){
                requireActivity().move(
                    requireContext(),
                    MainActivity::class.java
                )
                requireActivity().finish()
            }else{

            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
            }
        }, 5500)
    }

}