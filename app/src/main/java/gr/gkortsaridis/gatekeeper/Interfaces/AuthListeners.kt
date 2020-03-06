package gr.gkortsaridis.gatekeeper.Interfaces

interface SignInListener {
    fun onSignInComplete (userId: String)
    fun onSignInError (errorCode: Int, errorMsg: String)
}

interface SignUpListener {
    fun onSignUpComplete (user: String)
    fun onSignUpError (errorCode: Int, errorMsg: String)
}