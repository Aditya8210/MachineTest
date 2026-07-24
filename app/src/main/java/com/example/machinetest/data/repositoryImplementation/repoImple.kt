package com.example.machinetest.data.repositoryImplementation

import com.example.machinetest.domain.dataModel.ContactModel
import com.example.machinetest.domain.dataModel.UserData
import com.example.machinetest.domain.repositoryInterface.repoInterface
import com.example.machinetest.utils.ADMIN_DOC
import com.example.machinetest.utils.APP_CONFIG
import com.example.machinetest.utils.CONTACTS
import com.example.machinetest.utils.ResultState
import com.example.machinetest.utils.USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
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

    override fun getUserData(): Flow<ResultState<UserData>> = callbackFlow {
        trySend(ResultState.Loading)
        val uid = auth.currentUser?.uid
        if (uid != null) {
            firestore.collection(USERS).document(uid).get()
                .addOnSuccessListener { document ->
                    val userData = document.toObject(UserData::class.java)
                    if (userData != null) {
                        trySend(ResultState.Success(userData))
                    } else {
                        trySend(ResultState.Error("User data not found"))
                    }
                    close()
                }.addOnFailureListener {
                    trySend(ResultState.Error(it.message.toString()))
                    close()
                }
        } else {
            trySend(ResultState.Error("User not logged in"))
            close()
        }
        awaitClose()
    }

    override fun getAdminSecretKey(): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        firestore.collection(APP_CONFIG).document(ADMIN_DOC).get()
            .addOnSuccessListener { document ->
                val key = document.getString("secret_key")
                if (key != null) {
                    trySend(ResultState.Success(key))
                } else {
                    trySend(ResultState.Error("Admin key not found"))
                }
                close()
            }.addOnFailureListener {
                trySend(ResultState.Error(it.message.toString()))
                close()
            }
        awaitClose()
    }

    override fun syncContacts(contacts: List<ContactModel>): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(ResultState.Error("User not logged in"))
            close()
            return@callbackFlow
        }

        val contactsRef = firestore.collection(USERS).document(uid).collection(CONTACTS)

        // Delete old then upload new
        contactsRef.get().addOnSuccessListener { snapshot ->
            val batch = firestore.batch()
            for (doc in snapshot.documents) {
                batch.delete(doc.reference)
            }
            
            for (contact in contacts) {
                val newDoc = contactsRef.document()
                batch.set(newDoc, contact)
            }

            batch.commit().addOnSuccessListener {
                trySend(ResultState.Success("Contacts Synced Successfully"))
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

    override fun deleteCloudContacts(): Flow<ResultState<String>> = callbackFlow {
        trySend(ResultState.Loading)
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(ResultState.Error("User not logged in"))
            close()
            return@callbackFlow
        }

        val contactsRef = firestore.collection(USERS).document(uid).collection(CONTACTS)
        contactsRef.get().addOnSuccessListener { snapshot ->
            val batch = firestore.batch()
            for (doc in snapshot.documents) {
                batch.delete(doc.reference)
            }
            batch.commit().addOnSuccessListener {
                trySend(ResultState.Success("Cloud Contacts Deleted Successfully"))
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
}
