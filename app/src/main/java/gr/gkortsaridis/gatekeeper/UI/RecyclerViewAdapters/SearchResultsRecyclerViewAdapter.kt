package gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters

import android.content.Context
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import gr.gkortsaridis.gatekeeper.Entities.SearchResult
import gr.gkortsaridis.gatekeeper.Entities.SearchResultType
import gr.gkortsaridis.gatekeeper.Interfaces.SearchResultClickListener
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.Repositories.VaultRepository


class SearchResultsRecyclerViewAdapter(
    private val context: Context,
    private var searchResults: ArrayList<SearchResult>,
    private val listener: SearchResultClickListener): RecyclerView.Adapter<SearchResultsRecyclerViewAdapter.SearchResultsViewHolder>() {

    private var searchStr = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultsViewHolder {
        val inflatedView = LayoutInflater.from(context).inflate(R.layout.recycler_view_item_search_result, parent, false)
        return SearchResultsViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return searchResults.size
    }

    override fun onBindViewHolder(holder: SearchResultsViewHolder, position: Int) {
        val searchResultItem = searchResults[position]
        holder.bindSearchItem(searchStr, searchResultItem, listener)
    }

    fun updateSearchResults(searchStr: String, searchResults: ArrayList<SearchResult>) {
        this.searchResults = searchResults
        this.searchStr = searchStr
        notifyDataSetChanged()
    }

    class SearchResultsViewHolder(v: View): RecyclerView.ViewHolder(v) {

        private var view: View = v
        private var itemVault: View? = null
        private var itemType: ImageView? = null
        private var itemName: TextView? = null
        private var itemSubtitle: TextView? = null
        private var clickContainer: RelativeLayout? = null

        init {
            itemVault = view.findViewById(R.id.item_vault)
            itemType = view.findViewById(R.id.item_type)
            itemName = view.findViewById(R.id.item_name)
            itemSubtitle = view.findViewById(R.id.item_subtitle)
            clickContainer = view.findViewById(R.id.click_container)
        }

        fun bindSearchItem(searchStr: String, searchResult: SearchResult, listener: SearchResultClickListener){
            when(searchResult.itemType) {
                SearchResultType.LOGIN -> {
                    val login = searchResult.login
                    val vault = VaultRepository.getVaultByID(login?.vault_id ?: "")
                    itemType?.setImageResource(R.drawable.padlock)

                    itemName?.text = buildString(login?.name, searchStr)
                    itemSubtitle?.visibility = View.VISIBLE
                    itemSubtitle?.text = buildString(login?.username, searchStr)
                    itemVault?.setBackgroundResource( vault?.getVaultColorResource() ?: R.color.colorPrimaryDark )
                    clickContainer?.setOnClickListener { listener.onLoginClicked(login!!) }
                }
                SearchResultType.CARD -> {
                    val card = searchResult.card
                    val vault = VaultRepository.getVaultByID(card?.vaultId ?: "")

                    itemType?.setImageResource(R.drawable.card)
                    itemName?.text = buildString(card?.cardName, searchStr)
                    itemSubtitle?.visibility = View.VISIBLE
                    itemSubtitle?.text = buildString(card?.number, searchStr)
                    itemVault?.setBackgroundResource( vault?.getVaultColorResource() ?: R.color.colorPrimaryDark )
                    clickContainer?.setOnClickListener { listener.onCardClicked(card!!) }

                }
                SearchResultType.NOTE -> {
                    val note = searchResult.note
                    val vault = VaultRepository.getVaultByID(note?.vaultId ?: "")

                    itemType?.setImageResource(R.drawable.note)
                    itemName?.text = buildString(note?.title, searchStr)
                    itemSubtitle?.visibility = View.VISIBLE
                    itemSubtitle?.text = buildString(note?.body, searchStr)
                    itemVault?.setBackgroundResource( vault?.getVaultColorResource() ?: R.color.colorPrimaryDark )
                    clickContainer?.setOnClickListener { listener.onNoteClicked(note!!) }

                }
                SearchResultType.VAULT -> {
                    val vault = searchResult.vault
                    itemType?.setImageResource(R.drawable.vault)
                    itemName?.text = buildString(vault?.name, searchStr)
                    itemSubtitle?.visibility = View.GONE
                    itemVault?.setBackgroundResource( vault?.getVaultColorResource() ?: R.color.colorPrimaryDark )
                    clickContainer?.setOnClickListener { listener.onVaultClicked(vault!!) }

                }
            }

        }

        fun buildString(fullString: String?, hintString: String): Spanned {
            return Html.fromHtml(fullString?.replace(hintString, "<b>$hintString</b>", ignoreCase = true))
        }

    }

}