package sys.tianr.test.scanwithdb.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import sys.tianr.test.scanwithdb.data.DatabaseManager
import sys.tianr.test.scanwithdb.data.repository.PersonRepository
import sys.tianr.test.scanwithdb.data.repository.PersonRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindPersonRepository(
        personRepositoryImpl: PersonRepositoryImpl
    ): PersonRepository

    companion object {
        @Provides
        @Singleton
        fun provideDatabaseManager(@ApplicationContext context: Context): DatabaseManager {
            return DatabaseManager(context)
        }
    }
}
