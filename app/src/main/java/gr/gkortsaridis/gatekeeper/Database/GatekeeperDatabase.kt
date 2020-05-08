package gr.gkortsaridis.gatekeeper.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import gr.gkortsaridis.gatekeeper.Entities.CreditCard
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Entities.Note
import gr.gkortsaridis.gatekeeper.Entities.Vault

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
                    sInstance =
                        Room.databaseBuilder(
                            context.applicationContext,
                            GatekeeperDatabase::class.java,
                            databaseName
                        )
                            .allowMainThreadQueries()
                            .build()
                }
            }
            return sInstance!!
        }
    }
}