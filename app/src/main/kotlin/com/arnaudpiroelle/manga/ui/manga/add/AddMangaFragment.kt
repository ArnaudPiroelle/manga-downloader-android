package com.arnaudpiroelle.manga.ui.manga.add

import android.app.SearchManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.MenuItemCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.*
import android.widget.AdapterView
import android.widget.Spinner
import com.arnaudpiroelle.manga.MangaApplication
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.provider.MangaProvider
import com.arnaudpiroelle.manga.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.model.Manga
import com.arnaudpiroelle.manga.service.DownloadService
import com.arnaudpiroelle.manga.ui.manga.modify.ModifyMangaDialogFragment
import kotlinx.android.synthetic.fragment_add_manga.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import javax.inject.Inject


class AddMangaFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    @Inject lateinit var providerRegistry: ProviderRegistry

    private val mAdapter: ProviderMangaListingAdapter by lazy { ProviderMangaListingAdapter(activity, R.layout.item_view_manga) }
    private val spinnerAdapter: ProviderSpinnerAdapter by lazy { ProviderSpinnerAdapter(activity) }
    private var mPosition = 0
    private var mangas: List<Manga> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MangaApplication.GRAPH.inject(this)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_manga, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        computeProviders()

        list_provider_mangas.adapter = mAdapter
        list_provider_mangas.isFastScrollEnabled = true
        list_provider_mangas.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->

            val modifyMangaDialogFragment = ModifyMangaDialogFragment()

            val manga = mAdapter.getItem(i)

            modifyMangaDialogFragment.manga = manga
            modifyMangaDialogFragment.onChapterChoosenListener = {
                manga.lastChapter = it.chapterNumber
                manga.save()

                activity.startService(Intent(activity, DownloadService::class.java));
                activity.finish()
            }

            modifyMangaDialogFragment.show(childFragmentManager, null)
        }
        swipe_refresh.setOnRefreshListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_provider_mangas, menu)

        val supportActionBar = (activity as AppCompatActivity).supportActionBar

        val searchManager = activity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        var searchView = MenuItemCompat.getActionView(menu.findItem(R.id.action_search)) as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.componentName))
        searchView.queryHint = resources.getString(R.string.search_title)
        searchView.setOnCloseListener {
            searchView.onActionViewCollapsed()
            supportActionBar.setDisplayShowCustomEnabled(true)
            false
        }
        searchView.setOnSearchClickListener {
            supportActionBar.setDisplayShowCustomEnabled(false)
        }

        searchView.isIconified = true
        searchView.setOnQueryTextListener(this)
        searchView.requestFocus()
    }

    private fun computeProviders() {
        val supportActionBar = (activity as AppCompatActivity).supportActionBar
        supportActionBar.setDisplayShowCustomEnabled(true)

        var spinnerContainer: View = LayoutInflater.from(activity).inflate(R.layout.toolbar_spinner, null);
        var lp = ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        supportActionBar.setCustomView(spinnerContainer, lp);
        supportActionBar.setDisplayShowTitleEnabled(false)

        spinnerAdapter.addItems(providerRegistry.list())

        var spinner: Spinner = spinnerContainer.findViewById(R.id.toolbar_spinner) as Spinner
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mPosition = position
                retrieveMangas(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        spinnerAdapter.notifyDataSetChanged()
    }

    override fun onRefresh() {
        retrieveMangas(mPosition)
    }

    private fun retrieveMangas(position: Int) {
        swipe_refresh.isRefreshing = true

        val provider: MangaProvider = spinnerAdapter.getItem(position)

        Observable.create<List<Manga>> { subscriber ->
            subscriber.onNext(provider.findMangas())
            subscriber.onCompleted()
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Snackbar.make(list_provider_mangas, "${it.size} mangas available", Snackbar.LENGTH_LONG).show()
                    mangas = it
                    mAdapter.setData(it)
                    mAdapter.notifyDataSetChanged()
                    swipe_refresh.isRefreshing = false
                    list_provider_mangas.setSelection(0)
                }
    }

    override fun onQueryTextChange(query: String): Boolean {
        mAdapter.filter(query)

        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

}