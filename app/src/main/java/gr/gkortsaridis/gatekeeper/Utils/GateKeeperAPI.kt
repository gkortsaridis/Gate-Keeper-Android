package gr.gkortsaridis.gatekeeper.Utils

import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import gr.gkortsaridis.gatekeeper.Entities.Network.*
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import io.reactivex.Observable
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.security.cert.CertificateException
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object GateKeeperAPI {

    private const val TAG = "GateKeeperAPI"

    private const val NETWORK_TIMEOUT_SECONDS = 30L

    private fun getUnsafeOkHttpClient(): OkHttpClient.Builder {
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf()
                }
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory

            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier(HostnameVerifier { _, _ -> true })

            return builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    /**
     * Uses the default trusted certificates stored on the device.
     * If there is need to trust all certificates, call getUnsafeOkHttpClient()
     * instead of OkHttpClient.Builder()
     */
    private val jet2Client = OkHttpClient.Builder()
        .addInterceptor(FlipperOkhttpInterceptor(GateKeeperApplication.networkFlipperPlugin))
        .connectTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addNetworkInterceptor {
            it.proceed(it.request().newBuilder()
                .header("User-Agent", gateKeeperUserAgent()).build())
        }
        .build()

    private fun gateKeeperUserAgent(): String {
        var packageName = ""
        var versionName = ""
        var versionCode = -1

        try {
            packageName = GateKeeperApplication.instance.packageName
            var pInfo = GateKeeperApplication.instance.packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)
            versionName = pInfo.versionName
            versionCode = pInfo.versionCode
        } catch (nameNotFoundException: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Package name not found")
        }

        return "$packageName/$versionName($versionCode); Android ${Build.VERSION.RELEASE}; (gzip)"
    }

    private val retrofit = Retrofit.Builder()
        .client(jet2Client)
        .baseUrl("http://10.202.73.36:8080")
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(GateKeeperInterface::class.java)

    interface GateKeeperInterface {

        //AUTHENTICATION
        @POST("/auth/signIn")
        fun signIn(@Body body: ReqBodyUsernameHash): Observable<RespAuthentication>

        @POST("/auth/signUp")
        fun signUp(@Body body: ReqBodySignUp): Observable<RespAuthentication>

        @GET("/alldata/{user_id}")
        fun getAllData(@Path(value = "user_id", encoded = true) userId: String): Observable<RespAllData>

        //VAULTS
        @POST("/vaults/")
        fun createVault(@Body body: ReqBodyEncryptedData?): Observable<RespEncryptedData>

        //LOGINS
        @POST("/logins/")
        fun createLogin(@Body body: ReqBodyEncryptedData?): Observable<RespEncryptedData>

    }

}