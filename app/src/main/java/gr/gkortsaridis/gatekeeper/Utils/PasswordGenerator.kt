package gr.gkortsaridis.gatekeeper.Utils

import java.security.SecureRandom

object PasswordGenerator {

    fun randomString(len: Int, letters: Boolean, capitals: Boolean, numbers: Boolean, symbols: Boolean): String {
        val secureRandom = SecureRandom()

        val sb = StringBuilder(len)
        var data = ""
        if (letters) { data += "abcdefghijklmnopqrstuvwxyz" }
        if (capitals) { data += "ABCDEFGHIJKLMNOPQRSTUVWXYZ" }
        if (numbers) { data += "0123456789" }
        if (symbols) { data += "!@£$%^&*()_-+=<>?/.,{}[]"}

        for (i in 0 until len) {
            sb.append(data[secureRandom.nextInt(data.length)])
        }
        return sb.toString()
    }

}