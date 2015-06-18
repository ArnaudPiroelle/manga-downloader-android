package com.arnaudpiroelle.manga.ui.manga.add;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.arnaudpiroelle.manga.R;
import com.arnaudpiroelle.manga.event.MangaSelectedEvent;
import com.arnaudpiroelle.manga.event.ProviderSelectedEvent;
import com.arnaudpiroelle.manga.service.DownloadService;
import com.arnaudpiroelle.manga.ui.manga.add.manga.ProviderMangaListingFragment;
import com.arnaudpiroelle.manga.ui.manga.add.provider.ProviderListingFragment;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

import static com.arnaudpiroelle.manga.MangaApplication.GRAPH;
import static com.arnaudpiroelle.manga.ui.manga.add.manga.ProviderMangaListingFragment.PROVIDER_NAME;

public class AddMangaActivity extends AppCompatActivity {

    @Inject
    EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_manga);

        GRAPH.inject(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        displayProviders();
    }

    @Override
    protected void onResume() {
        super.onResume();

        eventBus.register(this);
    }

    @Override
    protected void onPause() {
        eventBus.unregister(this);

        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void replace(int contentId, Fragment fragment, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction()
                .replace(contentId, fragment, fragment.getTag());

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getTag());
        }

        fragmentTransaction.commit();
    }

    private void displayProviders() {
        replace(R.id.add_content, new ProviderListingFragment(), false);
    }

    private void displayProviderMangas(String provider) {
        final Bundle bundle = new Bundle();
        bundle.putString(PROVIDER_NAME, provider);

        ProviderMangaListingFragment fragment = new ProviderMangaListingFragment();
        fragment.setArguments(bundle);

        replace(R.id.add_content, fragment, true);
    }

    public void onEventMainThread(ProviderSelectedEvent event){
        displayProviderMangas(event.provider);
    }

    public void onEventMainThread(MangaSelectedEvent event){
        startService(new Intent(this, DownloadService.class));
        finish();
    }
}
