package com.arnaudpiroelle.manga.ui.manga.add

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.NavUtils
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.api.model.Manga
import com.arnaudpiroelle.manga.api.provider.MangaProvider
import com.arnaudpiroelle.manga.core.inject.inject
import com.arnaudpiroelle.manga.data.core.db.dao.MangaDao
import com.arnaudpiroelle.manga.data.core.db.dao.TaskDao
import com.arnaudpiroelle.manga.ui.manga.modify.ModifyMangaDialogFragment
import kotlinx.android.synthetic.main.activity_add_manga.*
import javax.inject.Inject
import kotlin.properties.Delegates.notNull

class AddMangaActivity : AppCompatActivity(), AddMangaContract.View, SearchView.OnQueryTextListener {

    @Inject
    lateinit var taskDao: TaskDao
    @Inject
    lateinit var mangaDao: MangaDao

    private val userActionsListener: AddMangaContract.UserActionsListener by lazy { AddMangaPresenter(this, ProviderRegistry, taskDao, mangaDao) }
    private var searchManager by notNull<SearchManager>()
    private val providerAdapter by lazy { ProviderSpinnerAdapter(this) }
    private val mangaAdapter by lazy { ProviderMangaAdapter(this, userActionsListener) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_manga)

        inject()

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        initProvidersUI()

        val linearLayoutManager = LinearLayoutManager(this)
        list_manga.addItemDecoration(DividerItemDecoration(list_manga.context, linearLayoutManager.orientation))

        list_manga.layoutManager = linearLayoutManager
        list_manga.adapter = mangaAdapter


    }

    private fun initProvidersUI() {
        supportActionBar?.let {
            val spinnerContainer: View = LayoutInflater.from(this).inflate(R.layout.toolbar_spinner, null, false)
            val lp = ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            it.setDisplayShowCustomEnabled(true)
            it.setCustomView(spinnerContainer, lp)
            it.setDisplayShowTitleEnabled(false)

            val spinner: Spinner = spinnerContainer.findViewById(R.id.toolbar_spinner) as Spinner
            spinner.adapter = providerAdapter
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val provider = providerAdapter.getItem(position)
                    userActionsListener.findMangas(provider)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)

        menuInflater.inflate(R.menu.menu_provider_mangas, menu)

        val searchView = menu?.findItem(R.id.action_search)?.actionView as? SearchView
        searchView?.let {
            it.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            it.queryHint = resources.getString(R.string.search_title)
            it.setOnCloseListener {
                searchView.onActionViewCollapsed()
                supportActionBar?.setDisplayShowCustomEnabled(true)
                false
            }
            it.setOnSearchClickListener {
                supportActionBar?.setDisplayShowCustomEnabled(false)
            }
            it.isIconified = true
            it.setOnQueryTextListener(this)
            it.requestFocus()
        }

        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()

        userActionsListener.fillProviders()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(name: String): Boolean {
        userActionsListener.filterBy(name)
        return true
    }

    override fun displayProviders(providers: Map<String, MangaProvider>) {
        providerAdapter.update(providers)
    }

    override fun displayMangas(mangas: List<Manga>) {
        list_manga.scrollToPosition(0)
        mangaAdapter.update(mangas)
    }

    override fun filterMangasBy(name: String) {
        list_manga.scrollToPosition(0)
        mangaAdapter.filter(name)
    }

    override fun displayChapters(manga: Manga) {
        val chapterChooser = ModifyMangaDialogFragment.newInstance(manga.provider, manga.name, manga.alias, "", true)
        chapterChooser.show(supportFragmentManager, null)
    }

    override fun closeWizard() {
        finish()
    }
}
