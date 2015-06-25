package com.arnaudpiroelle.manga.ui.manga.modify;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.arnaudpiroelle.manga.R;
import com.arnaudpiroelle.manga.event.MangaUpdatedEvent;
import com.arnaudpiroelle.manga.model.Chapter;
import com.arnaudpiroelle.manga.model.Manga;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

import static com.arnaudpiroelle.manga.MangaApplication.GRAPH;

public class ModifyMangaDialogFragment extends DialogFragment {

    @Inject
    EventBus eventBus;

    private Manga manga;
    private List<String> chaptersNames;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        GRAPH.inject(this);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        dialogBuilder
                .setTitle(R.string.dialog_select_chapter)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int checkedItemPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();

                        String lastChapter = chaptersNames.get(checkedItemPosition);
                        manga.setLastChapter(lastChapter);

                        eventBus.post(new MangaUpdatedEvent(manga));
                    }
                });


        chaptersNames = new ArrayList<>();
        chaptersNames.add("all");

        for (Chapter chapter : manga.getChapters()) {
            chaptersNames.add(chapter.getChapterNumber());
        }
        CharSequence[] charSequences = chaptersNames.toArray(new CharSequence[chaptersNames.size()]);

        dialogBuilder.setSingleChoiceItems(charSequences, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        return dialogBuilder.create();
    }

    public void setManga(Manga manga) {
        this.manga = manga;
    }
}
