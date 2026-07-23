package com.example.machinetest.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.content.Context
import com.example.machinetest.domain.dataModel.UserData
import com.example.machinetest.domain.repositoryInterface.repoInterface
import com.example.machinetest.utils.DashboardDataManager
import com.example.machinetest.utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class MyViewModel @Inject constructor(
    private val repoInterface: repoInterface,
    @ApplicationContext private val context: Context
): ViewModel(){

    private val dataManager = DashboardDataManager(context)

    private val _registerUserSt = MutableStateFlow(RegisterState())
    val registerUser = _registerUserSt.asStateFlow()

    private val _loginUserSt = MutableStateFlow(LoginState())
    val loginUser = _loginUserSt.asStateFlow()

    private val _userDataSt = MutableStateFlow(UserDataState())
    val userData = _userDataSt.asStateFlow()

    private val _adminKeySt = MutableStateFlow<String?>(null)
    val adminKey = _adminKeySt.asStateFlow()

    private val _dashboardSt = MutableStateFlow(DashboardState())
    val dashboardData = _dashboardSt.asStateFlow()

    fun registerUser(userData: UserData) {
        repoInterface.registerUserWithEmailAndPassword(userData).onEach {
            when (it) {
                is ResultState.Loading -> _registerUserSt.value = RegisterState(isLoading = true)
                is ResultState.Success -> _registerUserSt.value = RegisterState(data = it.data)
                is ResultState.Error -> _registerUserSt.value = RegisterState(error = it.exception)
            }
        }.launchIn(viewModelScope)
    }

    fun loginUser(email: String, password: String) {
        repoInterface.loginWithEmailAndPassword(email, password).onEach {
            when (it) {
                is ResultState.Loading -> _loginUserSt.value = LoginState(isLoading = true)
                is ResultState.Success -> {
                    _loginUserSt.value = LoginState(data = it.data)
                    fetchUserData() // Fetch data after login
                }
                is ResultState.Error -> _loginUserSt.value = LoginState(error = it.exception)
            }
        }.launchIn(viewModelScope)
    }

    fun fetchUserData() {
        repoInterface.getUserData().onEach {
            when (it) {
                is ResultState.Loading -> _userDataSt.value = UserDataState(isLoading = true)
                is ResultState.Success -> _userDataSt.value = UserDataState(data = it.data)
                is ResultState.Error -> _userDataSt.value = UserDataState(error = it.exception)
            }
        }.launchIn(viewModelScope)
    }

    fun fetchAdminSecretKey() {
        repoInterface.getAdminSecretKey().onEach { result ->
            if (result is ResultState.Success) {
                _adminKeySt.value = result.data
            }
        }.launchIn(viewModelScope)
    }

    fun fetchDashboardStats() {
        _dashboardSt.value = DashboardState(
            contactsCount = dataManager.getContactsCount(),
            callLogsCount = dataManager.getCallLogsCount(),
            smsCount = dataManager.getSmsCount(),
            lastSyncTime = dataManager.getCurrentTimestamp()
        )
    }
}

data class DashboardState(
    val contactsCount: Int = 0,
    val callLogsCount: Int = 0,
    val smsCount: Int = 0,
    val lastSyncTime: String = "Never"
)

data class UserDataState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val data: UserData? = null
)

data class RegisterState(
    val isLoading: Boolean = false,
    val error: String ?= null,
    val data: String? = null
)


data class LoginState(
    val isLoading: Boolean = false,
    val error: String ?= null,
    val data: String? = null
)