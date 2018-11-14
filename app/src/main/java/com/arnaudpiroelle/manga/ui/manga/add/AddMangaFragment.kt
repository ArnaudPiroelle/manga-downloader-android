package com.arnaudpiroelle.manga.ui.manga.add

import android.app.SearchManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.api.model.Manga
import kotlinx.android.synthetic.main.fragment_add_manga.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class AddMangaFragment : Fragment(), SearchView.OnQueryTextListener, ProviderMangaAdapter.Callback {
    private val viewModel: AddMangaViewModel by viewModel()

    private val searchManager: SearchManager by inject()
    private val providerAdapter by lazy { ProviderSpinnerAdapter() }

    private val mangaAdapter by lazy { ProviderMangaAdapter(this) }
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
        list_provider_mangas.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        provider_spinner.adapter = providerAdapter
        provider_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val provider = providerAdapter.getItem(position)
                viewModel.selectProvider(provider)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        toolbar.setNavigationOnClickListener { activity?.onNavigateUp() }
        toolbar.inflateMenu(R.menu.menu_provider_mangas)

        val menu = toolbar.menu
        val searchView = menu.findItem(R.id.action_search).actionView as? SearchView
        searchView?.apply {
            setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
            queryHint = resources.getString(R.string.search_title)
            setOnCloseListener {
                searchView.onActionViewCollapsed()
                false
            }
            setOnSearchClickListener {}
            isIconified = true
            setOnQueryTextListener(this@AddMangaFragment)
            requestFocus()
        }

        viewModel.getMangas().observe(this, Observer {
            list_provider_mangas.scrollToPosition(0)
            mangaAdapter.submitList(it)
        })

        viewModel.getProviders().observe(this, Observer {
            providerAdapter.update(it)
        })

        viewModel.wizardStatus.observe(this, Observer {
            Navigation.findNavController(view).navigateUp()
        })

        viewModel.loadProviders()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(name: String): Boolean {
        viewModel.filter(name)
        return true
    }

    override fun onMangaSelected(manga: Manga) {
        viewModel.selectManga(manga)
    }

}
