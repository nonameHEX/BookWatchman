package cz.mendelu.bookwatchman.di

import cz.mendelu.bookwatchman.database.IBookRepository
import cz.mendelu.bookwatchman.fake.FakeBookRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
abstract class FakeBookRepositoryModule {
    @Binds
    abstract fun bindBookRepository(
        fakeBookRepositoryImpl: FakeBookRepositoryImpl
    ): IBookRepository
}