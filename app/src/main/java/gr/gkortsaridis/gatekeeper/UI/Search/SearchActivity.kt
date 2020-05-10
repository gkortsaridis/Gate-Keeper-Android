package gr.gkortsaridis.gatekeeper.UI.Search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import gr.gkortsaridis.gatekeeper.Database.MainViewModel
import gr.gkortsaridis.gatekeeper.Entities.*
import gr.gkortsaridis.gatekeeper.R
import gr.gkortsaridis.gatekeeper.UI.RecyclerViewAdapters.SearchResultsRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

    private var adapter: SearchResultsRecyclerViewAdapter? = null

    private var allLogins: ArrayList<Login> = ArrayList()
    private var allCards: ArrayList<CreditCard> = ArrayList()
    private var allNotes: ArrayList<Note> = ArrayList()
    private var allVaults: ArrayList<Vault> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        adapter = SearchResultsRecyclerViewAdapter(this, ArrayList(), null)
        search_results_rv.layoutManager = LinearLayoutManager(this)
        search_results_rv.adapter = adapter

        val viewModel: MainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.appLogins.observe(this, Observer { this.allLogins = ArrayList(it) })
        viewModel.appCards.observe(this, Observer { this.allCards = ArrayList(it) })
        viewModel.appNotes.observe(this, Observer { this.allNotes = ArrayList(it) })
        viewModel.appVaults.observe(this, Observer { this.allVaults = ArrayList(it) })


        search_et.addTextChangedListener { updateUI(it.toString()) }
        updateUI("")
    }

    private fun updateUI(searchStr: String) {
        if (searchStr.isNotBlank()) {
            search_results_rv.visibility = View.VISIBLE
            search_counter_container.visibility = View.VISIBLE

            val filteredLogins = allLogins.filter {
                (it.name.contains(searchStr, ignoreCase = true)
                        || it.username.contains(searchStr, ignoreCase = true)
                        || it.password.contains(searchStr, ignoreCase = true)
                        || it.notes?.contains(searchStr, ignoreCase = true) == true) }

            val filteredCards = allCards.filter {
                (it.cardName.contains(searchStr, ignoreCase = true)
                        || it.number.trim().contains(searchStr.trim())
                        || it.cardholderName.contains(searchStr, ignoreCase = true)) }

            val filteredNotes = allNotes.filter {
                (it.title.contains(searchStr, ignoreCase = true)
                        || it.body.contains(searchStr, ignoreCase = true)) }

            val filteredVaults = allVaults.filter {
                it.name.contains(searchStr, ignoreCase = true) }

            val searchResults = ArrayList<SearchResult>()

            for (vault in filteredVaults) { searchResults.add(SearchResult(SearchResultType.VAULT, vault = vault)) }
            for (login in filteredLogins) { searchResults.add(SearchResult(SearchResultType.LOGIN, login = login)) }
            for (card  in filteredCards)  { searchResults.add(SearchResult(SearchResultType.CARD, card = card))}
            for (note  in filteredNotes)  { searchResults.add(SearchResult(SearchResultType.NOTE, note = note))}

            search_results_cnt.text = searchResults.size.toString()

            adapter?.updateSearchResults(searchStr, searchResults)
        } else {
            search_results_rv.visibility = View.GONE
            search_counter_container.visibility = View.GONE
        }
    }
}
