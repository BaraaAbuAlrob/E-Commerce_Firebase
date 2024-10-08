package com.baraa.training.ecommerce.di

import com.baraa.training.ecommerce.data.repository.auth.CountryRepository
import com.baraa.training.ecommerce.data.repository.auth.CountryRepositoryImpl
import com.baraa.training.ecommerce.data.repository.auth.FirebaseAuthRepository
import com.baraa.training.ecommerce.data.repository.auth.FirebaseAuthRepositoryImpl
import com.baraa.training.ecommerce.data.repository.categories.CategoriesRepository
import com.baraa.training.ecommerce.data.repository.categories.CategoriesRepositoryImpl
import com.baraa.training.ecommerce.data.repository.common.AppDataStoreRepositoryImpl
import com.baraa.training.ecommerce.data.repository.common.AppPreferenceRepository
import com.baraa.training.ecommerce.data.repository.home.SalesAdsRepository
import com.baraa.training.ecommerce.data.repository.home.SalesAdsRepositoryImpl
import com.baraa.training.ecommerce.data.repository.product.ProductsRepository
import com.baraa.training.ecommerce.data.repository.product.ProductsRepositoryImpl
import com.baraa.training.ecommerce.data.repository.special_sections.SpecialSectionsRepository
import com.baraa.training.ecommerce.data.repository.special_sections.SpecialSectionsRepositoryImpl
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

    @Binds
    @Singleton
    abstract fun provideProductsRepository(
        productsRepositoryImpl: ProductsRepositoryImpl
    ): ProductsRepository

    @Binds
    @Singleton
    abstract fun provideCountryRepositoryImpl(
        countryRepositoryImpl: CountryRepositoryImpl
    ): CountryRepository

    @Binds
    @Singleton
    abstract fun provideSpecialSectionsRepositoryImpl(
        specialSectionsRepository: SpecialSectionsRepositoryImpl
    ): SpecialSectionsRepository
}