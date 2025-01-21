package cz.mendelu.bookwatchman.di

import cz.mendelu.bookwatchman.communication.BooksAPI
import cz.mendelu.bookwatchman.communication.BooksRemoteRepositoryImpl
import cz.mendelu.bookwatchman.communication.IBooksRemoteRepository
import cz.mendelu.bookwatchman.database.BookRepositoryImpl
import cz.mendelu.bookwatchman.database.BooksDao
import cz.mendelu.bookwatchman.database.IBookRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideBookRepository(booksDao: BooksDao): IBookRepository {
        return BookRepositoryImpl(booksDao)
    }

    @Provides
    @Singleton
    fun provideBooksApiRepository(booksAPI: BooksAPI): IBooksRemoteRepository {
        return BooksRemoteRepositoryImpl(booksAPI)
    }
}