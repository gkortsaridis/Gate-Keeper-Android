package gr.gkortsaridis.gatekeeper.Interfaces

import gr.gkortsaridis.gatekeeper.Entities.Login

interface LoginSelectListener {
    fun onLoginClicked(login: Login)
}