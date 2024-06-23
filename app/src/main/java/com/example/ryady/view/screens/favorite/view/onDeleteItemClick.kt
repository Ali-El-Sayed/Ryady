package com.example.ryady.view.screens.favorite.view

interface IFavouriteFragment {

    fun deleteItem(itemId: String , listSize : Int)

    fun onItemClick(itemId: String)
}