package gr.gkortsaridis.gatekeeper

import android.app.assist.AssistStructure
import android.app.assist.AssistStructure.ViewNode
import android.os.Build
import android.os.CancellationSignal
import android.service.autofill.*
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import gr.gkortsaridis.gatekeeper.Repositories.LoginsRepository


@RequiresApi(Build.VERSION_CODES.O)
class GateKeeperAutoFillServiceL: AutofillService() {

    private val TAG = "_AUTOFILL_GATEKEEPER_"

    private var usernameAutofillId: AutofillId? = null
    private var passwordAutofillId: AutofillId? = null

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        // Get the structure from the request
        val context: List<FillContext> = request.fillContexts
        val structure: AssistStructure = context[context.size - 1].structure

        // Traverse the structure looking for nodes to fill out.
        traverseStructure(structure)

        // Fetch user data that matches the fields.
        val userData = fetchUserData(structure)

        if (userData.size > 0 && usernameAutofillId != null && passwordAutofillId != null) {
            // Build the presentation of the datasets
            val usernamePresentation = RemoteViews(packageName, R.layout.autofill_item)
            usernamePresentation.setTextViewText(R.id.login_name, userData[0].name)
            val passwordPresentation = RemoteViews(packageName, R.layout.autofill_item)
            passwordPresentation.setTextViewText(R.id.login_name, userData[0].name)

            // Add a dataset to the response
            val fillResponse: FillResponse = FillResponse.Builder()
                .addDataset(Dataset.Builder()
                    .setValue(
                        usernameAutofillId!!,
                        AutofillValue.forText(userData[0].username),
                        usernamePresentation
                    )
                    .setValue(
                        passwordAutofillId!!,
                        AutofillValue.forText(userData[0].password),
                        passwordPresentation
                    )
                    .build())
                .build()

            // If there are no errors, call onSuccess() and pass the response
            callback.onSuccess(fillResponse)
        }

    }

    private fun traverseStructure(structure: AssistStructure) {
        val windowNodes: List<AssistStructure.WindowNode> =
            structure.run {
                (0 until windowNodeCount).map { getWindowNodeAt(it) }
            }

        windowNodes.forEach { windowNode: AssistStructure.WindowNode ->
            val viewNode: ViewNode? = windowNode.rootViewNode
            traverseNode(viewNode)
        }
    }

    private fun traverseNode(viewNode: ViewNode?) {

        if (viewNode != null && (viewNode.className?.contains("EditText") == true) ) {

            if (viewNode.autofillHints != null && viewNode.autofillHints?.isNotEmpty() == true) {
                //If the View has any autofill hints in place

                for (hint in viewNode.autofillHints!!) {
                    if ( hint.toLowerCase().contains("user")
                        || hint.toLowerCase().contains("email") ) {
                        if (viewNode.autofillId != null) { usernameAutofillId = viewNode.autofillId!! }
                        return
                    }else if ( hint.toLowerCase().contains("pass")
                        || hint.toLowerCase().contains("secret") ) {
                        if (viewNode.autofillId != null) { passwordAutofillId = viewNode.autofillId!! }
                        return
                    }
                }
            }else {
                // Use custom heuristics to determine if view is Username/Password field
                // using view text or hint

                val hint = (viewNode.hint ?: "").toLowerCase()
                val text = viewNode.text
                val idEntry = viewNode.idEntry

                if (hint.contains("email") || hint.contains("user")
                    || text.contains("email") || text.contains("user")
                    || idEntry.contains("email") || idEntry.contains("user") ) {

                    if (viewNode.autofillId != null) usernameAutofillId = viewNode.autofillId!!

                }else if (hint.contains("pass") || hint.contains("secret")
                    || text.contains("pass") || text.contains("secret")
                    || idEntry.contains("pass") || idEntry.contains("secret")) {
                    if (viewNode.autofillId != null) passwordAutofillId = viewNode.autofillId!!
                }

            }

        }

        val children: List<ViewNode>? =
            viewNode?.run {
                (0 until childCount).map { getChildAt(it) }
            }

        children?.forEach { childNode: ViewNode ->
            traverseNode(childNode)
        }


    }

    private fun fetchUserData(structure: AssistStructure): ArrayList<UserData> {
        val activeApplication = structure.activityComponent.packageName
        val decryptedLogins = LoginsRepository.getSavedLogins()

        val userData = ArrayList<UserData>()

        for (login in decryptedLogins) {
            if (activeApplication == login.url) {
                userData.add(UserData(name = login.name, username = login.username, password = login.password))
            }
        }

        return userData
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {

    }

    private data class UserData(var name: String, var username: String, var password: String)
}