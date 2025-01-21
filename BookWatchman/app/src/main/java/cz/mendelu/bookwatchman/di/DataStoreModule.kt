package cz.mendelu.bookwatchman.di

import android.content.Context
import cz.mendelu.bookwatchman.datastore.DataStoreRepositoryImpl
import cz.mendelu.bookwatchman.datastore.IDataStoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): IDataStoreRepository {
        return DataStoreRepositoryImpl(context)
    }
}