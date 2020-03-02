package gr.gkortsaridis.gatekeeper.Interfaces

import gr.gkortsaridis.gatekeeper.Entities.Login
import java.lang.Exception

interface LoginRetrieveListener {
    fun onLoginsRetrieveSuccess(logins: ArrayList<Login>)
    fun onLoginsRetrieveError(e: Exception)
}

interface LoginCreateListener {
    fun onLoginCreated()
    fun onLoginCreateError(errorCode: Int, errorMsg: String)
}

interface LoginDeleteListener {
    fun onLoginDeleted()
}