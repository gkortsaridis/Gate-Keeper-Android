package gr.gkortsaridis.gatekeeper.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import gr.gkortsaridis.gatekeeper.BuildConfig
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Note
import gr.gkortsaridis.gatekeeper.Entities.Vault
import gr.gkortsaridis.gatekeeper.Repositories.AuthRepository
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory




@Database(entities = [Login::class, Vault::class, CreditCard::class, Note::class], version = 1, exportSchema = false)
@TypeConverters(DBConverters::class)
abstract class GatekeeperDatabase : RoomDatabase() {
    abstract fun dao(): GateKeeperDAO

    companion object {
        private val LOCK = Any()
        private const val databaseName = "GateKeeper"
        private var sInstance: GatekeeperDatabase? = null
        fun getInstance(context: Context): GatekeeperDatabase {
            if (sInstance == null) {
                synchronized(LOCK) {
                    val credentials = AuthRepository.loadCredentials()
                    var factory: SupportFactory? = null
                    if (credentials != null && !BuildConfig.DEBUG) {
                        val passphrase: ByteArray = SQLiteDatabase.getBytes(credentials.password.toCharArray())
                        factory = SupportFactory(passphrase)
                    }

                    sInstance =
                        Room.databaseBuilder(
                            context.applicationContext,
                            GatekeeperDatabase::class.java,
                            databaseName
                        )
                            .openHelperFactory(factory)
                            .allowMainThreadQueries()
                            .build()
                }
            }
            return sInstance!!
        }
    }
}