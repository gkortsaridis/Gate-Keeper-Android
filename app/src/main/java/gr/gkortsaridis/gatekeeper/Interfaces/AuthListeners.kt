package gr.gkortsaridis.gatekeeper.Interfaces

import gr.gkortsaridis.gatekeeper.Entities.FirebaseSignInResult

interface SignInListener {
    fun onSignInComplete (userId: String)
    fun onSignInError (errorCode: Int, errorMsg: String)

    fun onSignInError (success: Boolean, user: FirebaseSignInResult){}
    fun onRegistrationNeeded (email: String){}
}

interface SignUpListener {
    fun onSignUpComplete (user: String)
    fun onSignUpError (errorCode: Int, errorMsg: String)
}