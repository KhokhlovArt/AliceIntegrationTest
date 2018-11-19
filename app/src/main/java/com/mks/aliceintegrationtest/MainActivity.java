package com.mks.aliceintegrationtest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

//import ru.yandex.speechkit.BaseSpeechKit.LibraryInitializationException;
import java.util.Random;
import java.util.UUID;


import ru.yandex.speechkit.Emotion;
import ru.yandex.speechkit.Error;
import ru.yandex.speechkit.Language;
import ru.yandex.speechkit.OnlineModel;
import ru.yandex.speechkit.OnlineRecognizer;
import ru.yandex.speechkit.OnlineVocalizer;
import ru.yandex.speechkit.Recognition;
import ru.yandex.speechkit.Recognizer;
import ru.yandex.speechkit.RecognizerListener;
import ru.yandex.speechkit.SpeechKit;
import ru.yandex.speechkit.Synthesis;
import ru.yandex.speechkit.Track;
import ru.yandex.speechkit.Vocalizer;
import ru.yandex.speechkit.VocalizerListener;
import ru.yandex.speechkit.Voice;
import ru.yandex.speechkit.gui.RecognizerActivity;


public class MainActivity extends AppCompatActivity {
    int REQUEST_UI_CODE = 1;
    static String text;
    static VocalizerListener vl;
    static RecognizerListener rl;
    static OnlineVocalizer vocalizer;
    //static boolean isStart
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        text = "Повторять нечего";
        setContentView(R.layout.activity_main);
        Button b1 = findViewById(R.id.button);
        Button b2 = findViewById(R.id.button2);
//        SpeechKit.getInstance().configure(getApplicationContext(), "edcee376-a18b-4846-b0da-36d7b647bd76");

        try {
            SpeechKit.getInstance().init(getApplicationContext(), "edcee376-a18b-4846-b0da-36d7b647bd76");
        } catch (SpeechKit.LibraryInitializationException e) {
            e.printStackTrace();
        }
        SpeechKit.getInstance().setUuid("0270e19d-51eb-4d52-91f0-e6d53e952f9f");
        SpeechKit.getInstance().setLogLevel(SpeechKit.LogLevel.LOG_DEBUG);

        vl = new VocalizerListener() {
            @Override
            public void onSynthesisDone(@NonNull Vocalizer vocalizer) {

            }

            @Override
            public void onPartialSynthesis(@NonNull Vocalizer vocalizer, @NonNull Synthesis synthesis) {

            }

            @Override
            public void onPlayingBegin(@NonNull Vocalizer vocalizer) {

            }

            @Override
            public void onPlayingDone(@NonNull Vocalizer vocalizer) {

            }

            @Override
            public void onVocalizerError(@NonNull Vocalizer vocalizer, @NonNull Error error) {

            }
        };

        rl = new RecognizerListener() {
            @Override
            public void onRecordingBegin(@NonNull Recognizer recognizer) {
                Log.e("!!!", "Start!!!!");
            }

            @Override
            public void onSpeechDetected(@NonNull Recognizer recognizer) {

            }

            @Override
            public void onSpeechEnds(@NonNull Recognizer recognizer) {

            }

            @Override
            public void onRecordingDone(@NonNull Recognizer recognizer) {
                Log.e("!!!", "Stop!!!");
                recognizer.stopRecording();
            }

            @Override
            public void onPowerUpdated(@NonNull Recognizer recognizer, float v) {
//                Log.e("!!!", "onPowerUpdated : " + recognizer.toString());
            }

            @Override
            public void onPartialResults(@NonNull Recognizer recognizer, @NonNull Recognition recognition, boolean b) {
                //Log.e("!!!", "onPartialResults : " + recognizer.toString());
                if(b) {
                    //Log.e("!!!", "onPartialResults : " + recognition.toString());

                    Random r = new Random();
                    int i = r.nextInt(2);

                    Log.e("!!!", "onPartialResults r : " + recognition.getBestResultText().toString() + "   -  " + b + " / " + i);

                    String res;
                    switch (i)
                    {
                        case 0:
                            res = "бумага!";
                            break;
                        case 1:
                            res = "ножницы!";
                            break;
                        case 2:
                            res = "камень!";
                            break;
                        default: res = "Фак!";
                    }
                    if ( (recognition.getBestResultText().toString().toLowerCase().contains("бумага")))
                    {
                        switch (i)
                        {
                            case 0:
                                res += " ничья!";
                                break;
                            case 1:
                                res += " я проиграла!";
                                break;
                            case 2:
                                res += " я победила!";
                                break;
                            default: res = "Фак!";
                        }
                    }
                    else if ( (recognition.getBestResultText().toString().toLowerCase().contains("ножницы")))
                    {
                        switch (i)
                        {
                            case 0:
                                res += " я проиграла!";
                                break;
                            case 1:
                                res += " ничья!";
                                break;
                            case 2:
                                res += " я победила!";
                                break;
                            default: res = "Фак!";
                        }
                    }
                    else if ( (recognition.getBestResultText().toString().toLowerCase().contains("камень")))
                    {
                        switch (i)
                        {
                            case 0:
                                res += " я победила!";
                                break;
                            case 1:
                                res += " я проиграла!";
                                break;
                            case 2:
                                res += " ничья!";
                                break;
                            default: res = "Фак!";
                        }
                    }
                    else
                    {
                        res = "Э! Ты чё сказал мне: " + recognition.getBestResultText().toString() + "?";
                    }

                    Log.e("!!!", "" + res);

                    final String finalRes = res;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            say(finalRes);
                        }
                    });




                }

            }

            @Override
            public void onRecognitionDone(@NonNull Recognizer recognizer) {

            }

            @Override
            public void onRecognizerError(@NonNull Recognizer recognizer, @NonNull Error error) {

            }

            @Override
            public void onMusicResults(@NonNull Recognizer recognizer, @NonNull Track track) {

            }
        };

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnlineRecognizer recognizer = new OnlineRecognizer.Builder(Language.RUSSIAN, OnlineModel.QUERIES, rl)
                        .setDisableAntimat(false)
                        .setEnablePunctuation(true)
                        .build(); // 1
                recognizer.prepare(); // 2

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                recognizer.startRecording(); // 3



//                Intent intent = new Intent(getApplicationContext(), RecognizerActivity.class); // 1
//                intent.putExtra(RecognizerActivity.EXTRA_LANGUAGE, Language.RUSSIAN.getValue()); // 2
//                intent.putExtra(RecognizerActivity.EXTRA_MODEL, OnlineModel.QUERIES.getName()); // 2
//                intent.putExtra(RecognizerActivity.EXTRA_SHOW_PARTIAL_RESULTS, true); // 3
//                intent.putExtra(RecognizerActivity.EXTRA_SHOW_HYPOTHESES, true); // 3
//                intent.putExtra(RecognizerActivity.EXTRA_NIGHT_THEME, false); // 3
//                startActivityForResult(intent, REQUEST_UI_CODE); // 4
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //say("Я люблю сво+ю работу! Я приду туда в субботу!");
                //say("остр+ота - - - - - - - - - острот+а");
                say("Тест");
            }
        });


    }

    public void say(String s)
    {
        if (vocalizer == null) {
            vocalizer = new OnlineVocalizer.Builder(Language.RUSSIAN, vl)
                    .setEmotion(Emotion.EVIL)
                    .setVoice(Voice.ALYSS)
                    // .setSpeed((float) 0.2)
                    .build(); // 1
            vocalizer.prepare(); // 2
        }
        vocalizer.synthesize(s, Vocalizer.TextSynthesizingMode.APPEND); // 3
    }





    //    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (data != null) {
//            if (requestCode == REQUEST_UI_CODE) {
//                if (resultCode == RecognizerActivity.RESULT_OK) {
//                    final String result = data.getStringExtra(RecognizerActivity.EXTRA_RESULT); // 1
//                    Log.e("!!!" , "res ok: " + result);
//
//                    say(result);
//                    if(result.toLowerCase().contains("закрой") || result.toLowerCase().contains("закрыть"))
//                    {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                            MainActivity.this.finishAffinity();
//                        }
//                    }
//
//                } else if (resultCode == RecognizerActivity.RESULT_CANCELED) {
//                    final String language = data.getStringExtra(RecognizerActivity.EXTRA_LANGUAGE); // 2
//                    Log.e("!!!" , "res extra lang: " + language);
//                } else if (resultCode == RecognizerActivity.RESULT_ERROR) {
//                    final Error error = (Error) data.getSerializableExtra(RecognizerActivity.EXTRA_ERROR); // 3
//                    Log.e("!!!" , "res error: " + error);
//                }
//            }
//        }
//    }
}
