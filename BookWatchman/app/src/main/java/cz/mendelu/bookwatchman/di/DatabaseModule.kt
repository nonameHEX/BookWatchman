package cz.mendelu.bookwatchman.di

import cz.mendelu.bookwatchman.BookWatchmanApplication
import cz.mendelu.bookwatchman.database.BookshelfDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(): BookshelfDatabase = BookshelfDatabase.getDatabase(BookWatchmanApplication.appContext)
}