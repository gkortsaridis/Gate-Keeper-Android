package gr.gkortsaridis.gatekeeper.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class SignInViewModel(application: Application) : AndroidViewModel(application) {

    var emailStr: String = ""
    var passwordStr: String = ""
    var rememberPassword: Boolean = false

}