package gr.gkortsaridis.gatekeeper.Repositories

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.google.gson.Gson
import com.pvryan.easycrypt.ECResultListener
import com.pvryan.easycrypt.symmetric.ECSymmetric
import gr.gkortsaridis.gatekeeper.Entities.EncryptedData
import java.nio.charset.Charset
import java.security.KeyStore
import java.util.concurrent.CompletableFuture
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object SecurityRepository {

    private val androidKeystore = "AndroidKeyStore"
    private val appAlias = "GateKeeperKeysotre"
    private val cipherText = "AES/GCM/NoPadding"
    private val stringCharset = "UTF-8"

    private fun loadSecretKey(): SecretKey {
        return try {
            loadSavedKeyFromKeystore()
        }catch(e : java.lang.Exception) {
            generateNewKey()
        }
    }

    private fun generateNewKey(): SecretKey {
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

    fun decrypt(encryptedData: ByteArray, iv: ByteArray):String {
        val secretKey = loadSecretKey()

        val cipher = Cipher.getInstance(cipherText)

        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        val decodedData = cipher.doFinal(encryptedData)
        return String(decodedData, Charset.forName(stringCharset))
    }

    fun encrypt(decryptedData: String): EncryptedData? {
        return try{
            val secretKey = loadSecretKey()
            val cipher = Cipher.getInstance(cipherText)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val iv = cipher.iv
            val encryptedString = cipher.doFinal(decryptedData.toByteArray(Charset.forName(stringCharset)))
            EncryptedData(encryptedString, iv)
        }catch(e: java.lang.Exception) {
            null
        }
    }

    fun encryptObject(obj: Any) : String {
        val decrypted = Gson().toJson(obj)
        val response = CompletableFuture<String>()
        ECSymmetric().encrypt(decrypted, AuthRepository.getUserID(), object :
            ECResultListener {
            override fun onFailure(message: String, e: Exception) {
                response.complete("-1")
            }

            override fun <T> onSuccess(result: T) {
                response.complete(result as String)
            }
        })

        return response.get()
    }

}