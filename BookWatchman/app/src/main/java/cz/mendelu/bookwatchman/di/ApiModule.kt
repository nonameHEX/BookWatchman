package cz.mendelu.bookwatchman.di

import cz.mendelu.bookwatchman.communication.BooksAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun providesBooksAPI(retrofit: Retrofit): BooksAPI {
        return retrofit.create(BooksAPI::class.java)
    }
}