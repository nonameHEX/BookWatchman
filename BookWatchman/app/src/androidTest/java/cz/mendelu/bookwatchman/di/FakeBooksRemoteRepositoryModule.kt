package cz.mendelu.bookwatchman.di

import cz.mendelu.bookwatchman.communication.IBooksRemoteRepository
import cz.mendelu.bookwatchman.fake.FakeBooksRemoteRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
abstract class FakeBooksRemoteRepositoryModule {
    @Binds
    abstract fun bindBooksRemoteRepository(
        fakeBooksRemoteRepositoryImpl: FakeBooksRemoteRepositoryImpl
    ): IBooksRemoteRepository
}