package com.example.machinetest.domain.repositoryInterface

import com.example.machinetest.domain.dataModel.ContactModel
import com.example.machinetest.domain.dataModel.UserData
import com.example.machinetest.utils.ResultState
import kotlinx.coroutines.flow.Flow

interface repoInterface {
    fun registerUserWithEmailAndPassword(userData: UserData): Flow<ResultState<String>>
    fun loginWithEmailAndPassword(email: String, password: String): Flow<ResultState<String>>
    fun getUserData(): Flow<ResultState<UserData>>
    fun getAdminSecretKey(): Flow<ResultState<String>>

    // Contacts Sync
    fun syncContacts(contacts: List<ContactModel>): Flow<ResultState<String>>
    fun deleteCloudContacts(): Flow<ResultState<String>>
}
