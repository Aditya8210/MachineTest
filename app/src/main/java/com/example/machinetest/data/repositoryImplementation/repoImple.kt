package com.example.machinetest.data.repositoryImplementation

import com.example.machinetest.domain.dataModel.UserData
import com.example.machinetest.domain.repositoryInterface.repoInterface
import com.example.machinetest.utils.ResultState
import com.example.machinetest.utils.USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class repoImple @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) : repoInterface {
    override fun registerUserWithEmailAndPassword(userData: UserData): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)

        auth.createUserWithEmailAndPassword(userData.email, userData.password)
            .addOnSuccessListener { result ->
                firestore.collection(USERS).document(result.user?.uid.toString()).set(userData)
                    .addOnSuccessListener {
                        trySend(ResultState.Success("User Register Successfully"))
                        close()
                    }.addOnFailureListener {
                        trySend(ResultState.Error(it.message.toString()))
                        close()
                    }
            }.addOnFailureListener {
                trySend(ResultState.Error(it.message.toString()))
                close()
            }
            
        awaitClose()
    }

    override fun loginWithEmailAndPassword(email: String, password: String): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                trySend(ResultState.Success("User Login Successfully"))
                close()
            }.addOnFailureListener {
                trySend(ResultState.Error(it.message.toString()))
                close()
            }

        awaitClose()
    }
}
