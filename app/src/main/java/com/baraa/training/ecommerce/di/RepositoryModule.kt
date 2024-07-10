package com.baraa.training.ecommerce.di

import com.baraa.training.ecommerce.data.repository.auth.FirebaseAuthRepository
import com.baraa.training.ecommerce.data.repository.auth.FirebaseAuthRepositoryImpl
import com.baraa.training.ecommerce.data.repository.categories.CategoriesRepository
import com.baraa.training.ecommerce.data.repository.categories.CategoriesRepositoryImpl
import com.baraa.training.ecommerce.data.repository.common.AppDataStoreRepositoryImpl
import com.baraa.training.ecommerce.data.repository.common.AppPreferenceRepository
import com.baraa.training.ecommerce.data.repository.home.SalesAdsRepository
import com.baraa.training.ecommerce.data.repository.home.SalesAdsRepositoryImpl
import com.baraa.training.ecommerce.data.repository.user.UserFirestoreRepository
import com.baraa.training.ecommerce.data.repository.user.UserFirestoreRepositoryImpl
import com.baraa.training.ecommerce.data.repository.user.UserPreferenceRepository
import com.baraa.training.ecommerce.data.repository.user.UserPreferenceRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun provideAuthRepository(
        firebaseAuthRepositoryImpl: FirebaseAuthRepositoryImpl
    ): FirebaseAuthRepository

    @Binds
    @Singleton
    abstract fun provideUserPreferenceRepository(
        userPreferenceRepositoryImpl: UserPreferenceRepositoryImpl
    ): UserPreferenceRepository

    @Binds
    @Singleton
    abstract fun provideAppPreferenceRepository(
        appPreferenceRepositoryImpl: AppDataStoreRepositoryImpl
    ): AppPreferenceRepository

    @Binds
    @Singleton
    abstract fun provideUserFirestoreRepository(
        userFirestoreRepositoryImpl: UserFirestoreRepositoryImpl
    ): UserFirestoreRepository

    @Binds
    @Singleton
    abstract fun provideSalesAdsRepository(
        salesAdsRepositoryImpl: SalesAdsRepositoryImpl
    ): SalesAdsRepository

    @Binds
    @Singleton
    abstract fun provideCategoriesRepository(
        categoriesRepositoryImpl: CategoriesRepositoryImpl
    ): CategoriesRepository
}