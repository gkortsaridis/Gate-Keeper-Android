package gr.gkortsaridis.gatekeeper.Interfaces

import gr.gkortsaridis.gatekeeper.Entities.FirebaseSignInResult

interface SignInListener {
    fun onSignInComplete (success: Boolean, userId: String)
    fun onSignInComplete (success: Boolean, errorCode: Int, errorMsg: String)

    fun onSignInComplete (success: Boolean, user: FirebaseSignInResult){}
    fun onRegistrationNeeded (email: String)
}

interface SignUpListener {
    fun onSignUpComplete (success: Boolean, user: FirebaseSignInResult)
}