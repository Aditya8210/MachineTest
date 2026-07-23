package com.example.machinetest.domain.repositoryInterface

import com.example.machinetest.domain.dataModel.UserData
import com.example.machinetest.utils.ResultState
import kotlinx.coroutines.flow.Flow

interface repoInterface {


    fun registerUserWithEmailAndPassword(userData: UserData): Flow<ResultState<String>>



    fun loginWithEmailAndPassword(email: String, password: String): Flow<ResultState<String>>
}