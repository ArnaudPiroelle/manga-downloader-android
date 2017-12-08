package com.arnaudpiroelle.manga.ui.manga.list

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.*
import android.widget.AdapterView
import com.arnaudpiroelle.manga.MangaApplication.Companion.GRAPH
import com.arnaudpiroelle.manga.R
import com.arnaudpiroelle.manga.core.adapter.BaseAdapter
import com.arnaudpiroelle.manga.core.permission.PermissionHelper
import com.arnaudpiroelle.manga.core.provider.ProviderRegistry
import com.arnaudpiroelle.manga.core.ui.presenter.Presenter
import com.arnaudpiroelle.manga.model.Manga
import com.arnaudpiroelle.manga.service.DownloadService
import com.arnaudpiroelle.manga.ui.manga.add.AddMangaActivity
import com.arnaudpiroelle.manga.ui.manga.list.view.MangaView
import com.arnaudpiroelle.manga.ui.manga.modify.ModifyMangaDialogFragment
import kotlinx.android.synthetic.main.fragment_listing_manga.*
import javax.inject.Inject

class MangaListingFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, MangaListingPresenter.MangaListingCallback {

    @Inject lateinit var providerRegistry: ProviderRegistry

    private val mPresenter: Presenter<Manga> by lazy { MangaListingPresenter(this) }
    private val mAdapter: BaseAdapter<Manga, MangaView> by lazy { BaseAdapter<Manga, MangaView>(activity, R.layout.item_view_manga) }
    private var mPermissionHelper: PermissionHelper = PermissionHelper(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var updateReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            mPresenter.list()
        }
    }

    companion object {
        val MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 1
        public var UPDATE_RECEIVER_ACTION: String = "UPDATE_RECEIVER_ACTION"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        GRAPH.inject(this)

        setHasOptionsMenu(true)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_listing_manga, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        list_manga.adapter = mAdapter
        list_manga.emptyView = manga_empty
        list_manga.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, l ->
            modifyManga(mAdapter.getItem(position))
        }

        list_manga.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
            removeManga(mAdapter.getItem(position))
            true
        }

        swipe_refresh.setOnRefreshListener(this)
        action_add_manga.setOnClickListener { addManga() }

    }

    override fun onResume() {
        super.onResume()

        activity.setTitle(R.string.title_mymangas)

        mPresenter.list()

        activity.registerReceiver(updateReceiver, IntentFilter(UPDATE_RECEIVER_ACTION))
    }

    override fun onPause() {
        super.onPause()

        activity.unregisterReceiver(updateReceiver)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater!!.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_download -> {
                manualDownload()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onListingLoading() {
        swipe_refresh.isRefreshing = true
    }

    override fun onListingLoaded(mangas: List<Manga>) {
        swipe_refresh.isRefreshing = false
        mAdapter.setData(mangas)
    }

    override fun onRefresh() {
        mPresenter.list()
    }

    fun addManga() {
        activity.startActivity(Intent(activity, AddMangaActivity::class.java))
    }

    fun manualDownload() {
        mPermissionHelper.checkAndRequest(activity, MY_PERMISSIONS_REQUEST_WRITE_STORAGE) {
            startDownloadService()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_STORAGE -> {
                startDownloadService()
            }
        }
    }

    private fun startDownloadService() {
        val manualDownload = Intent(activity, DownloadService::class.java)
        manualDownload.setAction(DownloadService.MANUAL_DOWNLOAD)

        activity.startService(manualDownload)
    }

    /*
    private fun hideAddButton() {
        action_add_manga.animate().y(view!!.bottom.toFloat()).start()
    }
    */

    private fun showAddButton() {
        val lp = action_add_manga.layoutParams as ViewGroup.MarginLayoutParams
        action_add_manga.animate().y(view!!.bottom - lp.bottomMargin - action_add_manga.height.toFloat()).start()
    }

    private fun modifyManga(manga: Manga) {
        val modifyMangaDialogFragment = ModifyMangaDialogFragment()
        modifyMangaDialogFragment.manga = manga
        modifyMangaDialogFragment.onChapterChoosenListener = {
            manga.lastChapter = it.chapterNumber
            manga.save()

            mPresenter.list()
        }

        modifyMangaDialogFragment.show(childFragmentManager, null)
    }

    private fun removeManga(manga: Manga) {
        manga.delete()
        mPresenter.list()
        showAddButton()
    }

}
