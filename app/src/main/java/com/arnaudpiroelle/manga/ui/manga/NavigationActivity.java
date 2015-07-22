package com.arnaudpiroelle.manga.ui.manga;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.arnaudpiroelle.manga.R;
import com.arnaudpiroelle.manga.ui.history.HistoryFragment;
import com.arnaudpiroelle.manga.ui.manga.list.MangaListingFragment;
import com.arnaudpiroelle.manga.ui.settings.SettingsActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.arnaudpiroelle.manga.MangaApplication.GRAPH;

public class NavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String NAV_ITEM_ID = "NAV_ITEM_ID";

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private int mNavItemId = R.id.nav_mymangas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_navigation);

        GRAPH.inject(this);
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            mNavItemId = R.id.nav_mymangas;
        } else {
            mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
        }


        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(this);

        navigate(mNavItemId);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(NAV_ITEM_ID, mNavItemId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void navigate(int navIdRes) {
        Fragment fragment = null;
        switch (navIdRes) {
            case R.id.nav_mymangas:
                mNavItemId = navIdRes;
                fragment = new MangaListingFragment();
                replaceFragment(fragment, false);
                break;

            case R.id.nav_history:
                mNavItemId = navIdRes;
                fragment = new HistoryFragment();
                replaceFragment(fragment, false);
                break;

            case R.id.nav_settings:
                goSettings();
                break;
        }

        navigationView.getMenu().findItem(mNavItemId).setChecked(true);

    }


    private void replaceFragment(Fragment fragment, Boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_nav, fragment, fragment.getTag());

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(fragment.getTag());
        }

        fragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        if (mNavItemId != menuItem.getItemId()){
            navigate(menuItem.getItemId());
        }

        mDrawerLayout.closeDrawers();
        return true;
    }
}
