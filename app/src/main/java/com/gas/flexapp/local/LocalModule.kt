package com.gas.flexapp.local

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalModule {
    @Singleton
    @Binds
    abstract fun bindCipherService(
        cipherServiceImpl: CipherServiceImpl
    ): CipherService
}