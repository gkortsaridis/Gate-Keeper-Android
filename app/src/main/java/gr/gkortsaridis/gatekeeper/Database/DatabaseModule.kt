package gr.gkortsaridis.gatekeeper.Database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import gr.gkortsaridis.gatekeeper.BuildConfig
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideChannelDao(appDatabase: GatekeeperDatabase): GateKeeperDAO { return appDatabase.dao() }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): GatekeeperDatabase {
        val databaseName = "GateKeeper"
        val credentials = AuthRepository.loadCredentials()
        var factory: SupportFactory? = null
        if (credentials != null && !BuildConfig.DEBUG) {
            val passphrase: ByteArray = SQLiteDatabase.getBytes(credentials.password.toCharArray())
            factory = SupportFactory(passphrase)
        }

        return Room.databaseBuilder(
                appContext,
                GatekeeperDatabase::class.java,
                databaseName
            )
                .openHelperFactory(factory)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
    }
}