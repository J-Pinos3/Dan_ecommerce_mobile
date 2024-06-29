package com.example.kelineyt.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.example.kelineyt.firebase.FirebaseCommon
import com.example.kelineyt.util.Constants
import com.example.kelineyt.util.Constants.INTRODUCTION_SHARED_PREFERENCES
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun  provideFirebaseAuth() = FirebaseAuth.getInstance()


    @Provides
    @Singleton
    fun provideFirebaseFirestoreDatabase() = Firebase.firestore

    @Provides
    fun provideIntroductionSharedPreferences(
        application: Application
    )  = application.getSharedPreferences(INTRODUCTION_SHARED_PREFERENCES,MODE_PRIVATE)


    @Provides
    @Singleton
    fun provideFirebaseCommon(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ) = FirebaseCommon(firestore, firebaseAuth)
}