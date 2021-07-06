package gr.gkortsaridis.gatekeeper.Utils

import java.math.BigInteger
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object pbkdf2_lib {

    private val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"

    // The following constants may be changed without breaking existing hashes.
    private val HASH_BYTES = 32
    private val PBKDF2_ITERATIONS = 1000

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    fun createHash(password: String, username: String): String {
        val salt = username.toByteArray()
        return toHex(pbkdf2(password.toCharArray(), salt, PBKDF2_ITERATIONS, HASH_BYTES))
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    private fun pbkdf2(
        password: CharArray,
        salt: ByteArray,
        iterations: Int,
        bytes: Int
    ): ByteArray {
        val spec = PBEKeySpec(password, salt, iterations, bytes * 8)
        val skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM)
        return skf.generateSecret(spec).encoded
    }

    fun toHex(array: ByteArray): String {
        val bi = BigInteger(1, array)
        val hex = bi.toString(16)
        val paddingLength = array.size * 2 - hex.length
        return if (paddingLength > 0)
            String.format("%0" + paddingLength + "d", 0) + hex
        else
            hex
    }

}