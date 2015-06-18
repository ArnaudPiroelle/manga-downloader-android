package com.arnaudpiroelle.manga.ui.manga.add.provider;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.arnaudpiroelle.manga.core.provider.MangaProvider;
import com.arnaudpiroelle.manga.model.Manga;

import java.util.ArrayList;
import java.util.List;

public class ProviderListingAdapter extends BaseAdapter {

    private Context context;
    private List<MangaProvider> providers;

    public ProviderListingAdapter(Activity context) {
        this.context = context;
        providers = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return providers.size();
    }

    @Override
    public MangaProvider getItem(int position) {
        return providers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, null);
        } else {
            view = convertView;
        }

        TextView title = (TextView) view.findViewById(android.R.id.text1);

        MangaProvider mangaProvider = getItem(position);

        title.setText(mangaProvider.getName());

        return view;
    }

    public void setData(List<MangaProvider> providers){
        this.providers = providers;

        notifyDataSetChanged();
    }
}
