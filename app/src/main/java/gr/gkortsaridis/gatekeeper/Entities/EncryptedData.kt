package gr.gkortsaridis.gatekeeper.Entities

data class EncryptedData(val encryptedData: ByteArray, val iv: ByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptedData

        if (!encryptedData.contentEquals(other.encryptedData)) return false
        if (iv != other.iv) return false

        return true
    }

    override fun hashCode(): Int {
        var result = encryptedData.contentHashCode()
        result = 31 * result + iv.hashCode()
        return result
    }
}