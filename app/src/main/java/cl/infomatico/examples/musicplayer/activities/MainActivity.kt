package cl.infomatico.examples.musicplayer.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cl.infomatico.examples.musicplayer.R
import cl.infomatico.examples.musicplayer.adapters.ResultAdapter
import cl.infomatico.examples.musicplayer.models.Result
import cl.infomatico.examples.musicplayer.network.ITunesApi
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.ceil

class MainActivity : AppCompatActivity() {

    // AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportPostponeEnterTransition()
        rvProducts.setHasFixedSize(true)

        if (resources.configuration.fontScale > 1.2) {
            val gridLayoutManager = rvProducts.layoutManager as GridLayoutManager
            gridLayoutManager.spanCount = 1
        }

        // Pull To Refresh

        srlProducts.setOnRefreshListener { productsRefresh() }

        // Pagination

        rvProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0) {
                    val layoutManager = recyclerView.layoutManager as GridLayoutManager

                    val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount

                    if (!isLoading && (pastVisibleItems + visibleItemCount) >= totalItemCount) {
                        productsAdd()
                    }
                }
            }
        })

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        setIntent(intent)
        handleIntent(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        // SearchItem

        val searchItem = menu.findItem(R.id.action_search)

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                text = null
                goMain()
                return true
            }

            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }
        })

        // SearchView

        searchView = searchItem.actionView as SearchView

        // SearchManager

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        return true
    }

    // MainActivity

    companion object {
        const val pageItems = 20
    }

    private var page: Int = 1
    private var pages: Int = 0
    private var isLoading: Boolean = false

    // Search

    private lateinit var searchView: SearchView

    private var text: String? = null

    private var iTunesApi: ITunesApi = ITunesApi()
    private lateinit var resultAdapter: ResultAdapter

    private fun handleIntent(intent: Intent?) {
        if (intent != null && Intent.ACTION_SEARCH == intent.action) {
            text = intent.getStringExtra(SearchManager.QUERY)

            if (searchView.query.isEmpty())
                searchView.setQuery(text, false)

            searchView.clearFocus()
        }

        resultsLoad()
    }

    // Results

    private var results: ArrayList<Result> = ArrayList()

    private fun resultsLoad() {

        if (text == null)
            return

        iTunesApi.search(object : ITunesApi.Listener {
            override fun load(results: List<Result>) {
                if (results.isEmpty()) {
                    llNoResultProducts.visibility = View.VISIBLE
                    srlProducts.visibility = View.GONE
                    searchView.clearFocus()
                } else {
                    llNoResultProducts.visibility = View.GONE
                    srlProducts.visibility = View.VISIBLE
                }

                pages = ceil(results.size / pageItems.toDouble()).toInt()
                this@MainActivity.results = ArrayList(results)
                val subList = results.subList(0, pageItems)

                resultAdapter = ResultAdapter()
                resultAdapter.setResults(ArrayList(subList))
                rvProducts.adapter = resultAdapter

                supportStartPostponedEnterTransition()

                resultAdapter.onListener = object : ResultAdapter.OnListener {
                    override fun onClick(
                        result: Result
                        , viewHolder: ResultAdapter.ViewHolder
                    ) {
                        goAlbum(result)
                    }
                }
            }
        }, "music", "song", text!!, 200)
    }

    private fun productsRefresh() {
        iTunesApi.search(object : ITunesApi.Listener {
            override fun load(results: List<Result>) {

                pages = ceil(results.size / pageItems.toDouble()).toInt()
                this@MainActivity.results = ArrayList(results)
                val subList = results.subList(0, pageItems)

                resultAdapter.setResults(ArrayList(subList))
                page = 1
                srlProducts.isRefreshing = false
            }

            override fun error() {
                srlProducts.isRefreshing = false
            }
        }, "music", "song", text!!, 200)
    }

    private fun productsAdd() {

        if (page >= pages)
            return

        isLoading = true
        page++
        llProgressBarProducts.visibility = View.VISIBLE

        val begin = (page - 1) * pageItems
        var end = begin + pageItems

        if (results.size < end)
            end = results.size

        val subList = results.subList(begin, end)

        resultAdapter.addResults(ArrayList(subList))
        isLoading = false
        llProgressBarProducts.visibility = View.INVISIBLE
    }


    // Activities

    private fun goActivity(activity: Class<out Any>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }

    fun goMain() {
        goActivity(MainActivity::class.java)
    }

    // Albums

    fun goAlbum(result: Result) {
        val intent = Intent(this, AlbumActivity::class.java)
        intent.putExtra(Result.ID, result.collectionId)
        startActivity(intent)
    }
}