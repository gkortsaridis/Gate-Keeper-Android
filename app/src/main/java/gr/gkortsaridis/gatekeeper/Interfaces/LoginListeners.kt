package gr.gkortsaridis.gatekeeper.Interfaces

import gr.gkortsaridis.gatekeeper.Entities.Login
import java.lang.Exception

interface LoginRetrieveListener {
    fun onLoginsRetrieveSuccess(logins: ArrayList<Login>)
    fun onLoginsRetrieveError(e: Exception)
}

interface LoginCreateListener {
    fun onLoginCreated(login: Login)
    fun onLoginCreateError(errorCode: Int, errorMsg: String)
}

interface LoginUpdateListener {
    fun onLoginUpdated(login: Login)
    fun onLoginUpdateError(errorCode: Int, errorMsg: String)
}

interface LoginDeleteListener {
    fun onLoginDeleted()
    fun onLoginDeleteError(errorCode: Int, errorMsg: String)
}