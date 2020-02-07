package com.arnaudpiroelle.manga.ui.manga.add

import android.app.Activity
import android.app.SearchManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.api.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.api.model.Manga
import com.arnaudpiroelle.manga.core.utils.bind
import com.arnaudpiroelle.manga.core.utils.distinctUntilChanged
import com.arnaudpiroelle.manga.core.utils.map
import com.arnaudpiroelle.manga.ui.manga.add.AddMangaContext.Action.*
import com.arnaudpiroelle.manga.ui.manga.add.AddMangaContext.ActionError
import com.arnaudpiroelle.manga.ui.manga.add.AddMangaContext.WizardStatus
import com.arnaudpiroelle.manga.ui.manga.add.AddMangaContext.WizardStatus.FINISHED
import com.google.android.material.snackbar.Snackbar
import com.reddit.indicatorfastscroll.FastScrollItemIndicator
import com.reddit.indicatorfastscroll.FastScrollerView
import kotlinx.android.synthetic.main.fragment_add_manga.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.Normalizer


class AddMangaFragment : Fragment(), SearchView.OnQueryTextListener, ProviderMangaAdapter.Callback {
    private val viewModel: AddMangaViewModel by viewModel()

    private val searchManager: SearchManager by inject()
    private val providerAdapter by lazy { ProviderSpinnerAdapter() }

    private val mangaAdapter by lazy { ProviderMangaAdapter(this) }
    private val errorSnackbar by lazy { Snackbar.make(list_provider_mangas, R.string.error_occured, Snackbar.LENGTH_INDEFINITE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_manga, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list_provider_mangas.layoutManager = LinearLayoutManager(context)
        list_provider_mangas.adapter = mangaAdapter

        fastscroller.setupWithRecyclerView(list_provider_mangas, useDefaultScroller = false, getItemIndicator = { position ->
            val item = mangaAdapter.getItemBy(position)
            val firstLetter = Normalizer.normalize(item.name, Normalizer.Form.NFD)
                    .substring(0, 1)
                    .replace("\\p{InCombiningDiacriticalMarks}+", "")
            val letter = if (Character.isLetter(firstLetter.first())) {
                firstLetter.toUpperCase()
            } else {
                "#"
            }
            FastScrollItemIndicator.Text(letter)
        })
        fastscroller_thumb.setupWithFastScroller(fastscroller)
        fastscroller.itemIndicatorSelectedCallbacks += object : FastScrollerView.ItemIndicatorSelectedCallback {
            override fun onItemIndicatorSelected(indicator: FastScrollItemIndicator, indicatorCenterY: Int, itemPosition: Int) {
                val layoutManager = list_provider_mangas.layoutManager as LinearLayoutManager
                layoutManager.scrollToPositionWithOffset(itemPosition, 0)
            }
        }

        provider_spinner.adapter = providerAdapter
        provider_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val provider = providerAdapter.getItem(position)
                viewModel.handle(SelectProviderAction(provider))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        toolbar.inflateMenu(R.menu.menu_provider_mangas)

        val menu = toolbar.menu
        val searchView = menu.findItem(R.id.action_search).actionView as? SearchView
        searchView?.apply {
            setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
            queryHint = resources.getString(R.string.search_title)
            isIconified = true
            setOnQueryTextListener(this@AddMangaFragment)
            requestFocus()
        }

        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.state.map { it.isLoading }.distinctUntilChanged().bind(this, this::onIsLoadingChanged)
        viewModel.state.map { it.error }.distinctUntilChanged().bind(this, this::onHasErrorChanged)
        viewModel.state.map { it.status }.distinctUntilChanged().bind(this, this::onStatusChanged)
        viewModel.state.map { it.providers }.distinctUntilChanged().bind(this, this::onProvidersChanged)
        viewModel.state.map { it.getFilteredResults() }.distinctUntilChanged().bind(this, this::onResultsChanged)

        viewModel.handle(LoadProvidersAction)
    }

    override fun onDestroyView() {
        val menu = toolbar.menu
        val searchView = menu.findItem(R.id.action_search).actionView as? SearchView
        searchView?.isIconified = true
        list_provider_mangas.adapter = null
        super.onDestroyView()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(name: String): Boolean {
        viewModel.handle(FilterResultsAction(name))
        return true
    }

    override fun onMangaSelected(manga: Manga) {
        viewModel.handle(AddNewMangaAction(manga))
    }

    private fun onIsLoadingChanged(isLoading: Boolean) {
        list_provider_mangas.visibility = if (isLoading) GONE else VISIBLE
        empty_view.visibility = if (isLoading) GONE else VISIBLE
        loading.visibility = if (isLoading) VISIBLE else GONE
    }

    private fun onHasErrorChanged(error: ActionError?) {
        if (error != null) {
            if (error.retry != null) {
                errorSnackbar.setAction(R.string.retry) { viewModel.handle(error.retry) }
            }
            errorSnackbar.show()
        } else {
            if (errorSnackbar.isShown) {
                errorSnackbar.dismiss()
            }
        }
    }

    private fun onProvidersChanged(providers: List<ProviderRegistry.Provider>) {
        providerAdapter.update(providers)
    }

    private fun onResultsChanged(results: List<Manga>) {

        if (results.isEmpty()) {
            list_provider_mangas.visibility = GONE
            empty_view.visibility = VISIBLE
        } else {
            list_provider_mangas.scrollToPosition(0)
            list_provider_mangas.visibility = VISIBLE
            empty_view.visibility = GONE
        }

        mangaAdapter.submitList(results)

    }

    private fun onStatusChanged(status: WizardStatus) {
        when (status) {
            FINISHED -> {
                val menu = toolbar.menu
                val searchView = menu.findItem(R.id.action_search).actionView as? SearchView
                searchView?.isIconified = true
                searchView?.clearFocus()
                findNavController().navigateUp()
            }
            else -> {  }
        }
    }
}
