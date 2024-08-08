package com.baraa.training.ecommerce.di

import android.app.Application
import com.baraa.training.ecommerce.data.datasource.datastore.AppPreferencesDataSource
import com.baraa.training.ecommerce.data.models.user.UserDetailsModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    fun fakeUserData(): UserDetailsModel {
        return UserDetailsModel(
            id = "1236", email = "eslam@mail.com"
        )
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideAppPreferencesDataSource(context: Application): AppPreferencesDataSource {
        return AppPreferencesDataSource(context)
    }
}