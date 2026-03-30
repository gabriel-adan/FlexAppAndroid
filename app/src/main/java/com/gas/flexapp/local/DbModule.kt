package com.gas.flexapp.local

import android.content.Context
import com.gas.flexapp.BuildConfig
import com.gas.orm.context.DbContext
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {
    @Provides
    @Singleton
    fun provideDbContext(
        @ApplicationContext appContext: Context
    ): DbContext {
        return DbContext.connect(appContext, BuildConfig.DATABASE_NAME, BuildConfig.DATABASE_VERSION.toInt())
    }
}