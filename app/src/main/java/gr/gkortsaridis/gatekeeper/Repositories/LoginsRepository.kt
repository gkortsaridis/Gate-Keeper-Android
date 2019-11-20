package gr.gkortsaridis.gatekeeper.Repositories

import com.google.firebase.firestore.FirebaseFirestore
import gr.gkortsaridis.gatekeeper.Entities.Login
import gr.gkortsaridis.gatekeeper.Interfaces.LoginRetrieveListener

object LoginsRepository {

    fun retrieveLoginsByAccountID(accountID: String, retrieveListener: LoginRetrieveListener) {

        val db = FirebaseFirestore.getInstance()
        db.collection("logins")
            .whereEqualTo("account_id",accountID)
            .get().addOnSuccessListener { result ->
                val loginsResult = ArrayList<Login>()
                for (document in result) {
                    loginsResult.add(Login(document))
                }

                retrieveListener.onLoginsRetrieveSuccess(loginsResult)
            }
            .addOnFailureListener { exception -> retrieveListener.onLoginsRetrieveError(exception) }

    }

}