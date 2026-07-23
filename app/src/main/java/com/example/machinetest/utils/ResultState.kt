package com.example.machinetest.utils

sealed class ResultState <out T>{
    data class Success<T>(val data: T) : ResultState<T>()
    data class Error(val exception: String) : ResultState<Nothing>()
    object Loading : ResultState<Nothing>()
}