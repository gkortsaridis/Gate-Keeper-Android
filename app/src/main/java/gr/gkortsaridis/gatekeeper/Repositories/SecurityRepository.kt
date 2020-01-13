package gr.gkortsaridis.gatekeeper.Repositories

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.google.gson.Gson
import gr.gkortsaridis.gatekeeper.Entities.EncryptedData
import gr.gkortsaridis.gatekeeper.Utils.pbkdf2_lib
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object SecurityRepository {

    private val androidKeystore = "AndroidKeyStore"
    private val appAlias = "GateKeeperKeysotre"
    private val cipherText = "AES/GCM/NoPadding"
    private val stringCharset = "UTF-8"

    //Operations for private Android Keystore Related Key
    private fun loadKeystoreSecretKey(): SecretKey {
        return try {
            loadSavedKeyFromKeystore()
        }catch(e : java.lang.Exception) {
            generateNewKeystoreKey()
        }
    }

    private fun generateNewKeystoreKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            androidKeystore
        )

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(appAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    private fun loadSavedKeyFromKeystore(): SecretKey {
        val keyStore = KeyStore.getInstance(androidKeystore);
        keyStore.load(null);

        val secretKeyEntry = keyStore.getEntry(appAlias, null) as KeyStore.SecretKeyEntry
        val savedSecretKey = secretKeyEntry.secretKey
        return savedSecretKey
    }

    //Operation for User Credential secret key creation
    private fun createUserCredentialsSecretKey(): SecretKey? {
        return try {
            //Retrieve User Credentials & Create Encryption Decrytion Key
            val loadedCredentials = AuthRepository.loadCredentials()
            val ek = pbkdf2_lib.createHash(loadedCredentials!!.email, AuthRepository.getUserID())
            SecretKeySpec(ek, "AES")
        } catch (e: java.lang.Exception) {
            null
        }

    }

    //Encryption / Decryption operations based on Android Keystore secret key
    fun encryptWithKeystore(decryptedData: String): EncryptedData? {
        return try{
            val secretKey = loadKeystoreSecretKey()
            val cipher = Cipher.getInstance(cipherText)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val iv = cipher.iv
            val encryptedString = cipher.doFinal(decryptedData.toByteArray(Charset.forName(stringCharset)))
            EncryptedData(encryptedString, iv)
        }catch(e: java.lang.Exception) {
            null
        }
    }

    fun decryptWithKeystore(encryptedData: ByteArray, iv: ByteArray):String {
        val secretKey = loadKeystoreSecretKey()

        val cipher = Cipher.getInstance(cipherText)

        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        val decodedData = cipher.doFinal(encryptedData)
        return String(decodedData, Charset.forName(stringCharset))
    }

    //Encryption / Decryption operations on User Credentials secret key
    fun encryptWithUserCredentials(decryptedData: String): EncryptedData? {
        return try {
            val userKey = createUserCredentialsSecretKey()
            if (userKey != null) {
                val cipher = Cipher.getInstance(cipherText)
                cipher.init(Cipher.ENCRYPT_MODE, userKey)
                val iv = cipher.iv
                val encryptedBytes = cipher.doFinal(decryptedData.toByteArray(Charset.forName(stringCharset)))
                EncryptedData(encryptedBytes, iv)
            } else { null }
        }catch(e: java.lang.Exception) { null }
    }

    fun decryptWithUserCredentials(encryptedData: EncryptedData):String? {
        return try {
            val userKey = createUserCredentialsSecretKey()
            if (userKey != null) {
                val cipher = Cipher.getInstance(cipherText)
                val spec = GCMParameterSpec(128, encryptedData.iv)
                cipher.init(Cipher.DECRYPT_MODE, userKey, spec)
                val decodedData = cipher.doFinal(encryptedData.encryptedData)
                String(decodedData, Charset.forName(stringCharset))
            } else { null }
        } catch (e: java.lang.Exception) { null }

    }

    fun encryptObjectWithUserCredentials(obj: Any) : String? {
        val decrypted = Gson().toJson(obj)
        val encData = encryptWithUserCredentials(decrypted)
        return Gson().toJson(encData)
    }

    fun decryptStringToObjectWithUserCredentials(str: String, objType: Class<out Any>): Any? {
        return try {
            val encData = Gson().fromJson(str, EncryptedData::class.java)
            val decryptedString = decryptWithUserCredentials(encData)
            Gson().fromJson(decryptedString, objType)
        } catch (e: Exception) {
            null
        }
    }

}