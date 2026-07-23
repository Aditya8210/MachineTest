package com.example.machinetest.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)

object DiModule {

    @Provides

    fun provideFireStore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()

    }


    @Provides

    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

}