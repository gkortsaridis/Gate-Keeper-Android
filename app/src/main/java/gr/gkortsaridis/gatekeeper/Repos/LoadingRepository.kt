package gr.gkortsaridis.gatekeeper.Repos

import gr.gkortsaridis.gatekeeper.Entities.Network.RespAllData
import gr.gkortsaridis.gatekeeper.Utils.GateKeeperAPI
import io.reactivex.Observable
import javax.inject.Inject

class LoadingRepository @Inject constructor(private val api: GateKeeperAPI.GateKeeperInterface) {

    fun getAllData(userId: String): Observable<RespAllData> {
        return api.getAllData(userId = userId)
    }

}