package gr.gkortsaridis.gatekeeper.Repos

import gr.gkortsaridis.gatekeeper.Entities.Network.ReqBodyUsernameHash
import gr.gkortsaridis.gatekeeper.Entities.Network.RespAuthentication
import gr.gkortsaridis.gatekeeper.GateKeeperApplication
import gr.gkortsaridis.gatekeeper.Repositories.DeviceRepository
import gr.gkortsaridis.gatekeeper.Repositories.SecurityRepository
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperAPI
import gr.gkortsaridis.gatekeeper.Utils.pbkdf2_lib
import io.reactivex.Observable
import javax.inject.Inject

class AuthenticationRepository @Inject constructor(private val api: GateKeeperAPI.GateKeeperInterface)  {

    fun signIn(email: String, password: String): Observable<RespAuthentication> {
        val hash = pbkdf2_lib.createHash(password, email)
        val device = DeviceRepository.getCurrentDevice(GateKeeperApplication.instance)
        val encDevice = SecurityRepository.encryptObjToEncDataWithUserCredentials(device)
        val body = ReqBodyUsernameHash(
            username = email,
            hash = hash,
            deviceEncryptedData = encDevice?.encryptedData ?: "",
            deviceIv = encDevice?.iv ?: "",
            deviceUid = device.UID)

        return api.signIn(body = body)
    }
}