package gr.gkortsaridis.gatekeeper.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import gr.gkortsaridis.gatekeeper.Entities.*


@Database(entities = [EncryptedDBItem::class, Login::class, Vault::class, CreditCard::class, Note::class], version = 3, exportSchema = false)
@TypeConverters(DBConverters::class)
abstract class GatekeeperDatabase : RoomDatabase() {
    abstract fun dao(): GateKeeperDAO
}