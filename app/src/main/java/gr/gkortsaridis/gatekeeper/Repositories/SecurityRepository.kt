package gr.gkortsaridis.gatekeeper.Repositories

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.google.gson.Gson
import gr.gkortsaridis.gatekeeper.Entities.EncryptedData
import gr.gkortsaridis.gatekeeper.Entities.Network.ReqBodyEncryptedData
import gr.gkortsaridis.gatekeeper.Entities.Network.ReqBodyExtraDataUpdate
import gr.gkortsaridis.gatekeeper.Entities.Network.ReqBodyUsernameHash
import gr.gkortsaridis.gatekeeper.Entities.UserExtraData
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Utils.CryptLib
import gr.gkortsaridis.gatekeeper.Utils.pbkdf2_lib
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object SecurityRepository {

    private val androidKeystore = "AndroidKeyStore"
    private val appAlias = "GateKeeperKeystore"
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

    //Encryption / Decryption operations based on Android Keystore secret key
    fun encryptWithKeystore(decryptedData: String): EncryptedData? {
        return try{
            val secretKey = loadKeystoreSecretKey()
            val cipher = Cipher.getInstance(cipherText)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val iv = cipher.iv
            val encrypted = cipher.doFinal(decryptedData.toByteArray(Charset.forName(stringCharset)))
            val ctBase64 = Base64.encodeToString(encrypted, Base64.DEFAULT)
            val ivBase64 = Base64.encodeToString(iv, Base64.DEFAULT)
            EncryptedData(encryptedData =  ctBase64,iv = ivBase64)
        }catch(e: java.lang.Exception) {
            null
        }
    }

    fun decryptWithKeystore(encryptedData: String, ivStr: String):String {

        val ct = Base64.decode(encryptedData, Base64.DEFAULT)
        val iv = Base64.decode(ivStr, Base64.DEFAULT)

        val secretKey = loadKeystoreSecretKey()
        val cipher = Cipher.getInstance(cipherText)
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        val decodedData = cipher.doFinal(ct)
        return String(decodedData, Charset.forName(stringCharset))
    }

    //Encryption / Decryption operations on User Credentials secret key
    fun encryptWithUserCredentials(decryptedData: String): EncryptedData? {
        return try {
            val cryptLib = CryptLib()
            val credentials = AuthRepository.loadCredentials()
            if (credentials != null) {
                val userPassword = AuthRepository.getUserID()
                val key = CryptLib.SHA256(userPassword, 32) //32 bytes = 256 bit
                val iv = CryptLib.generateRandomIV(16) //16 bytes = 128 bit
                val encryption =  cryptLib.encrypt(decryptedData, key, iv)
                EncryptedData(encryptedData = encryption,iv = iv)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun decryptWithUserCredentials(encryptedData: EncryptedData):String? {
        return try {
            val cryptLib = CryptLib()
            val credentials = AuthRepository.loadCredentials()
            if (credentials != null) {
                val userPassword = AuthRepository.getUserID()
                val key = CryptLib.SHA256(userPassword, 32) //32 bytes = 256 bit
                cryptLib.decrypt(encryptedData.encryptedData, key, encryptedData.iv)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun encryptObjectWithUserCredentials(obj: Any) : String? {
        val decrypted = Gson().toJson(obj)
        val encData = encryptWithUserCredentials(decrypted)
        return Gson().toJson(encData)
    }

    fun encryptObjectWithUserCreds(obj: Any) : EncryptedData? {
        val decrypted = Gson().toJson(obj)
        return encryptWithUserCredentials(decrypted)
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

    fun decryptEncryptedDataToObjectWithUserCredentials(obj: EncryptedData, objType: Class<out Any>): Any? {
        return try {
            val decryptedString = decryptWithUserCredentials(obj)
            Gson().fromJson(decryptedString, objType)
        } catch (e: Exception) {
            null
        }
    }

    fun createUsernameHashRequestBody(): ReqBodyUsernameHash? {
        val loadedCredentials = AuthRepository.loadCredentials()

        return if (loadedCredentials != null) {
            val hash = pbkdf2_lib.createHash(loadedCredentials.password, loadedCredentials.email)
            val device = DeviceRepository.getCurrentDevice(GateKeeperApplication.instance)
            val encDevice = encryptObjectWithUserCreds(device)
            ReqBodyUsernameHash(
                username = loadedCredentials.email,
                hash = hash,
                deviceEncryptedData = encDevice!!.encryptedData,
                deviceIv = encDevice.iv,
                deviceUid = device.UID)
        } else {
            null
        }
    }

    fun createEncryptedDataRequestBody(obj: Any, id: String? = null): ReqBodyEncryptedData? {
        val enc = encryptObjectWithUserCreds(obj)
        val loadedCredentials = AuthRepository.loadCredentials()

        return if (enc != null && loadedCredentials != null) {
            val hash = pbkdf2_lib.createHash(loadedCredentials.password, loadedCredentials.email)
            val device = DeviceRepository.getCurrentDevice(GateKeeperApplication.instance)
            val encDevice = encryptObjectWithUserCreds(device)
            ReqBodyEncryptedData(
                id = id?.toLong(),
                userId = AuthRepository.getUserID(),
                encryptedData = enc.encryptedData,
                iv = enc.iv,
                username = loadedCredentials.email,
                hash = hash,
                deviceEncryptedData = encDevice!!.encryptedData,
                deviceIv = encDevice.iv,
                deviceUid = device.UID)
        }else {
            null
        }
    }

    fun createExtraDataUpdateRequestBody(fullName: String? = null, imgUrl: String? = null): ReqBodyExtraDataUpdate? {
        var extraData = GateKeeperApplication.extraData
        if (fullName != null) extraData.userFullName = fullName
        if (imgUrl != null) extraData.userImg = imgUrl

        val enc = encryptObjectWithUserCreds(extraData)
        val loadedCredentials = AuthRepository.loadCredentials()
        return if (enc != null && loadedCredentials != null) {
            val hash = pbkdf2_lib.createHash(loadedCredentials.password, loadedCredentials.email)
            val device = DeviceRepository.getCurrentDevice(GateKeeperApplication.instance)
            val encDevice = encryptObjectWithUserCreds(device)
            ReqBodyExtraDataUpdate(
                username = loadedCredentials.email,
                hash = hash,
                extrasEncryptedData = enc.encryptedData,
                extrasIv = enc.iv,
                deviceEncryptedData = encDevice!!.encryptedData,
                deviceIv = encDevice.iv,
                deviceUid = device.UID
            )
        }else {
            null
        }
    }

    fun getUserExtraData(email: String, data: String, iv: String): UserExtraData {
        val userData = decryptEncryptedDataToObjectWithUserCredentials(EncryptedData(encryptedData = data, iv = iv), UserExtraData::class.java) as UserExtraData?
        return userData ?: UserExtraData(userEmail = email, userFullName = null, userImg = null)
    }

}