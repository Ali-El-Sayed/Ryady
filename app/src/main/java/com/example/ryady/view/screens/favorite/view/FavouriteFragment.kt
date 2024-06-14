package com.example.ryady.view.screens.favorite.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.ryady.databinding.FragmentFavouriteBinding
import com.example.ryady.datasource.remote.RemoteDataSource
import com.example.ryady.network.GraphqlClient
import com.example.ryady.network.model.Response
import com.example.ryady.utils.readCustomerData
import com.example.ryady.view.factory.ViewModelFactory
import com.example.ryady.view.screens.favorite.ViewModel.FavouriteViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "FavouriteFragment"

class FavouriteFragment : Fragment(), IFavouriteFragment {
    lateinit var binding: FragmentFavouriteBinding
    lateinit var userEmail: String
    private val viewModel by lazy {
        val factory = ViewModelFactory(RemoteDataSource.getInstance(client = GraphqlClient.apiService))
        ViewModelProvider(this, factory)[FavouriteViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                readCustomerData(requireContext()) {
                    userEmail = it["user email"].toString()
                    viewModel.getAllFavouriteProduct(userEmail)
                }

            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.productList.collectLatest {
                withContext(Dispatchers.Main) {
                    when (it) {
                        is Response.Error -> {}
                        is Response.Loading -> {}
                        is Response.Success -> {
                            binding.rvFavouriteList.adapter =
                                FavouriteListAdapter(it.data.toMutableList(), this@FavouriteFragment, requireContext())
                        }
                    }
                }
            }
        }
    }

    override fun deleteItem(itemId: String) {
        viewModel.deleteItem(userEmail, itemId)
    }

    override fun onItemClick(itemId: String) {
        findNavController().navigate(FavouriteFragmentDirections.actionFavouriteFragmentToProductInfoFragment(itemId))
    }


}