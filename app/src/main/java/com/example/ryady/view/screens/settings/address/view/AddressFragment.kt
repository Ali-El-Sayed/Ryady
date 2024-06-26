package com.example.ryady.view.screens.settings.address.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.ryady.R
import com.example.ryady.databinding.DeleteAlertDialogBinding
import com.example.ryady.databinding.FragmentAddressBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.utils.readCustomerData
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.settings.address.utils.SwipeToDeleteCallback
import com.example.ryady.view.screens.settings.address.view.adapter.AddressListAdapter
import com.example.ryady.view.screens.settings.address.viewModel.AddressViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class AddressFragment : Fragment() {
    private val binding: FragmentAddressBinding by lazy { FragmentAddressBinding.inflate(layoutInflater) }
    private lateinit var adapter: AddressListAdapter
    private val viewModel: AddressViewModel by lazy {
        val factory = ViewModelFactory(RemoteDataSource.getInstance(GraphqlClient.apiService))
        ViewModelProvider(this, factory)[AddressViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            readCustomerData(requireContext()) {
                viewModel.userToken = it["user token"] ?: ""
            }
        }
        binding.fabAddAddress.setOnClickListener {
            findNavController().navigate(AddressFragmentDirections.actionAddressFragmentToCustomerDataFragment())
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeToDelete()
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.fetchAddresses()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.addresses.collectLatest {
                    when (it) {
                        is Response.Loading -> binding.frameLayout.visibility = View.VISIBLE
                        is Response.Success -> {
                            binding.frameLayout.visibility = View.GONE
                            binding.imgNotFound.visibility = if (it.data.isEmpty()) View.VISIBLE else View.GONE
                            adapter.submitList(it.data)
                        }

                        is Response.Error -> {
                            binding.imgNotFound.visibility = View.GONE
                            binding.frameLayout.visibility = View.GONE
                        }
                    }
                }
            }
        }

    }

    private fun setupRecyclerView() {
        adapter = AddressListAdapter()
        binding.addressList.itemAnimator = LandingAnimator()
        binding.addressList.adapter = adapter
    }

    private fun setupSwipeToDelete() {
        val swipeHandler = object : SwipeToDeleteCallback(requireActivity()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION && position < adapter.currentList.size) {
                    val address = adapter.currentList[position]
                    showDeleteDialog(address.id)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.addressList)
    }


    private fun showDeleteDialog(addressId: String) {
        val binding = DeleteAlertDialogBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext()).setView(binding.root).setCancelable(false).setBackground(
            AppCompatResources.getDrawable(
                requireContext(), R.drawable.verification_dialog_background
            )
        ).create()

        binding.btnDelete.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                launch {
                    viewModel.deleteAddress(addressId)
                    delay(500)
                    viewModel.fetchAddresses()
                }
            }
            dialog.dismiss()
        }
        binding.btnCancel.setOnClickListener {
            adapter.notifyDataSetChanged()
            dialog.dismiss()
        }
        dialog.show()
    }
}