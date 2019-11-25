package gr.gkortsaridis.gatekeeper.Repositories

import android.app.Activity
import android.content.Intent
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import gr.gkortsaridis.gatekeeper.Entities.EncryptedData
import gr.gkortsaridis.gatekeeper.Entities.FirebaseSignInResult
import gr.gkortsaridis.gatekeeper.Entities.UserCredentials
import gr.gkortsaridis.gatekeeper.Entities.ViewDialog
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Interfaces.SignInListener
import gr.gkortsaridis.gatekeeper.Interfaces.SignUpListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.UI.Authentication.LoadingActivity
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec



object AuthRepository {

    private val TAG = "_Auth_Repository_"

    private val androidKeystore = "AndroidKeyStore"
    private val appAlias = "GateKeeperKeysotre"

    val auth = FirebaseAuth.getInstance()
    val RC_SIGN_IN : Int = 1


    fun signIn(activity:Activity, email: String, password: String, check: Boolean, listener: SignInListener) {
        val viewDialog = ViewDialog(activity)
        viewDialog.showDialog()
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                viewDialog.hideDialog()
                when {
                    it.isSuccessful -> {
                        listener.onSignInComplete(true, FirebaseSignInResult(it.result,null))
                    }
                    check -> {
                        listener.onRegistrationNeeded(email)
                    }
                    else -> {
                        listener.onSignInComplete(false, FirebaseSignInResult(null,it.exception))
                    }
                }
            }
    }

    fun googleSignIn(activity: Activity) {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(activity, gso)

        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun signUp(activity: Activity, email: String, password: String, listener: SignUpListener) {
        val viewDialog = ViewDialog(activity)
        viewDialog.showDialog()
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener { result: AuthResult ->
            viewDialog.hideDialog()
            listener.onSignUpComplete(true, FirebaseSignInResult(result, null))
        }.addOnFailureListener {e: java.lang.Exception ->
            viewDialog.hideDialog()
            listener.onSignUpComplete(false, FirebaseSignInResult(null, e))
        }
    }

    fun setApplicationUser(user: FirebaseUser) {
        GateKeeperApplication.user = user
    }

    fun proceedLoggedIn(activity: Activity) {
        activity.startActivity(Intent(activity, LoadingActivity::class.java))
    }

    fun saveCredentials(email: String, password: String): Boolean {

        val encryptionEmail = encrypt(email)
        val encryptionPassword = encrypt(password)

        return if (encryptionEmail != null && encryptionPassword != null) {
            DataRepository.userEmail = Gson().toJson(encryptionEmail)
            DataRepository.userPassword = Gson().toJson(encryptionPassword)
            true
        }else { false }
    }

    fun loadCredentials():UserCredentials? {
        val encryptedEmail = DataRepository.userEmail
        val encryptedPassword = DataRepository.userPassword


        return if (encryptedEmail != null
            && encryptedEmail != ""
            && encryptedPassword != null
            && encryptedPassword != ""
        ) {
            val encryptedEmailData = Gson().fromJson(encryptedEmail, EncryptedData::class.java)
            val decryptedEmail = decrypt(encryptedEmailData.encryptedData, encryptedEmailData.iv, loadSecretKey())

            val encryptedPasswordData = Gson().fromJson(encryptedPassword, EncryptedData::class.java)
            val decryptedPassword = decrypt(encryptedPasswordData.encryptedData, encryptedPasswordData.iv, loadSecretKey())

            UserCredentials(decryptedEmail, decryptedPassword)
        }else { null }

    }

    private fun loadSecretKey(): SecretKey {
        return try {
            loadSavedKeyFromKeystore()
        }catch(e : java.lang.Exception) {
            generateNewKey()
        }
    }

    private fun generateNewKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, androidKeystore)

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(appAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }

    private fun loadSavedKeyFromKeystore(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        val secretKeyEntry = keyStore.getEntry(appAlias, null) as KeyStore.SecretKeyEntry
        val savedSecretKey = secretKeyEntry.secretKey
        return savedSecretKey
    }

    private fun decrypt(encryptedData: ByteArray, iv: ByteArray, secretKey: SecretKey):String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        val decodedData = cipher.doFinal(encryptedData)
        return String(decodedData, Charset.forName("UTF-8"))
    }

    private fun encrypt(decryptedData: String): EncryptedData? {
        return try{
            val secretKey = loadSecretKey()
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val iv = cipher.iv
            val encryptedString = cipher.doFinal(decryptedData.toByteArray(Charset.forName("UTF-8")))
            EncryptedData(encryptedString, iv)
        }catch(e: java.lang.Exception) {
            null
        }
    }
}