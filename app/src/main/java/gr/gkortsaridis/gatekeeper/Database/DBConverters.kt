package gr.gkortsaridis.gatekeeper.Database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import gr.gkortsaridis.gatekeeper.Entities.*
import java.sql.Timestamp
import java.util.*

class DBConverters {

    @TypeConverter
    fun fromStringToTimestamp(value: String?): Timestamp? {
        val listType = object : TypeToken<Timestamp>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromTimestamp(timestamp: Timestamp?): String {
        val gson = Gson()
        return gson.toJson(timestamp)
    }

    @TypeConverter
    fun fromStringToVaultColor(value: String?): VaultColor {
        return when(value) {
            VaultColor.Blue.value -> VaultColor.Blue
            VaultColor.White.value -> VaultColor.White
            VaultColor.Yellow.value -> VaultColor.Yellow
            VaultColor.Coral.value -> VaultColor.Coral
            VaultColor.Green.value -> VaultColor.Green
            VaultColor.Red.value -> VaultColor.Red
            else -> VaultColor.Blue
        }
    }

    @TypeConverter
    fun fromVaultColor(color: VaultColor): String {
        return color.value
    }

    @TypeConverter
    fun fromStringToCardType(value: String?): CardType {
        return when(value) {
            CardType.DinersClub.value -> CardType.DinersClub
            CardType.JCB.value -> CardType.JCB
            CardType.DiscoverCard.value -> CardType.DiscoverCard
            CardType.DinersClub.value -> CardType.DinersClub
            CardType.Amex.value -> CardType.Amex
            CardType.Mastercard.value -> CardType.Mastercard
            CardType.Visa.value -> CardType.Visa
            else -> CardType.Unknown
        }
    }

    @TypeConverter
    fun fromCardType(type: CardType): String {
        return type.value
    }

    @TypeConverter
    fun fromStringToNoteColor(value: String?): NoteColor {
        return when(value) {
            NoteColor.Blue.value -> NoteColor.Blue
            NoteColor.Cream.value -> NoteColor.Cream
            NoteColor.Green.value -> NoteColor.Green
            NoteColor.Orange.value -> NoteColor.Orange
            NoteColor.Pink.value -> NoteColor.Pink
            NoteColor.Red.value -> NoteColor.Red
            NoteColor.White.value -> NoteColor.White
            NoteColor.Yellow.value -> NoteColor.Yellow
            else -> NoteColor.White
        }
    }

    @TypeConverter
    fun fromNoteColor(color: NoteColor): String {
        return color.value
    }
}