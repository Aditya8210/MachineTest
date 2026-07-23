package com.example.machinetest.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.machinetest.domain.dataModel.UserData
import com.example.machinetest.domain.repositoryInterface.repoInterface
import com.example.machinetest.utils.ResultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class MyViewModel @Inject constructor(private val repoInterface: repoInterface): ViewModel(){


    private val _registerUserSt = MutableStateFlow(RegisterState())
    val registerUser = _registerUserSt.asStateFlow()



    private val _loginUserSt = MutableStateFlow(LoginState())
    val loginUser = _loginUserSt.asStateFlow()


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
                is ResultState.Success -> _loginUserSt.value = LoginState(data = it.data)
                is ResultState.Error -> _loginUserSt.value = LoginState(error = it.exception)
            }
        }.launchIn(viewModelScope)
    }






}




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