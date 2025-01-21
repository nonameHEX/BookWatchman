package cz.mendelu.bookwatchman.di

import cz.mendelu.bookwatchman.database.BooksDao
import cz.mendelu.bookwatchman.database.BookshelfDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    fun provideDao(database: BookshelfDatabase): BooksDao {
        return database.booksDao()
    }
}