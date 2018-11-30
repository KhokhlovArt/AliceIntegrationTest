package com.mks.aliceintegrationtest;

import android.support.annotation.NonNull;
import android.util.Log;

import ru.yandex.speechkit.Error;
import ru.yandex.speechkit.PhraseSpotter;
import ru.yandex.speechkit.PhraseSpotterListener;

public class My_PhraseSpotterListener implements PhraseSpotterListener {
    @Override
    public void onPhraseSpotted(@NonNull PhraseSpotter phraseSpotter, @NonNull String s, int i) {
        Log.e("!!!", "Phrase Spotted: " + s);
    }

    @Override
    public void onPhraseSpotterStarted(@NonNull PhraseSpotter phraseSpotter) {
        Log.e("!!!", "Start spotter ->");
    }

    @Override
    public void onPhraseSpotterError(@NonNull PhraseSpotter phraseSpotter, @NonNull Error error) {
        Log.e("!!!", "<- End spotter");
    }
}
