package com.mks.aliceintegrationtest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

//import ru.yandex.speechkit.BaseSpeechKit.LibraryInitializationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;


import ru.yandex.speechkit.Emotion;
import ru.yandex.speechkit.Error;
import ru.yandex.speechkit.Language;
import ru.yandex.speechkit.OnlineModel;
import ru.yandex.speechkit.OnlineRecognizer;
import ru.yandex.speechkit.OnlineVocalizer;
import ru.yandex.speechkit.PhraseSpotter;
import ru.yandex.speechkit.PhraseSpotterListener;
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
    private static final String CONTACT_ID = ContactsContract.Contacts._ID;
    private static final String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    private static final String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    private static final String PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    private static final String PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;

    enum action { SLEEP, PLAY, CALL, STARTAPP }

    static action State = action.SLEEP; //Состояние 0 - слушает команды; 1 - играет в игру Камень/ножницы/бумага; 2 - в режиме "Позвонить ХХХ"
    static String text;
    static VocalizerListener vl;
    static RecognizerListener rl;
    static OnlineVocalizer vocalizer;
    static boolean isListening;
    My_PhraseSpotterListener spotter;
    Button b1;
    Button b2;
    OnlineRecognizer recognizer;
    //static boolean isStart

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        State = action.SLEEP;
        isListening = false;
        text = "Повторять нечего";

        setContentView(R.layout.activity_main);
        b1 = findViewById(R.id.button);
        b2 = findViewById(R.id.button2);
//        SpeechKit.getInstance().configure(getApplicationContext(), "edcee376-a18b-4846-b0da-36d7b647bd76");


////        PhraseSpotter phraseSpotter = new PhraseSpotter.Builder("phrase-spotter/commands", spotter).build(); // 1
//        PhraseSpotter phraseSpotter = new PhraseSpotter.Builder("spotter_model/alisa", spotter).build(); // 1
//        phraseSpotter.prepare(); // 2
//        phraseSpotter.start(); // 3


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
                changeLiseningState();
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
                changeLiseningState();
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
                    String text = recognition.getBestResultText().toString().toLowerCase();
                    text = text.replace(".", "");
                    text = text.replace(",", "");
                    text = text.replace("?", "");
                    text = text.replace("-", "");
                    text = text.replace("!", "");
                    text = text.replace(":", "");

                    Log.e("!!!", "onPartialResults r : " + recognition.getBestResultText().toString() + "   -  " + b + " State = " + State);
                    if(State == action.SLEEP) {
                        if (text.contains("играть")) {
                            State = action.PLAY;
                            say("Давай играть в Камень Ножницы бумага. Загадывай");
                        }
                        else if (text.contains("позвони"))
                        {
                            State = action.CALL;
                            say("Кому мне позвонить?");
                        }
                        else if (text.contains("запусти"))
                        {
                            State = action.STARTAPP;
                            say("Что запустить?");
                        }
                        else
                        {
                            say("---");
                        }


                    } else {
                        if ((text.contains("хватит")))
                        {
                            State = action.SLEEP;
                            say("Хорошо");
                        }else {
                            if (State == action.PLAY) {
                                play(text);
                            }
                            if (State == action.CALL)
                            {
                                call(text);
                            }
                            if (State == action.STARTAPP)
                            {
                                startApp(text);
                            }
                        }
                    }
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


        final OnlineRecognizer recognizer = new OnlineRecognizer.Builder(Language.RUSSIAN, OnlineModel.QUERIES, rl)
                .setDisableAntimat(false)
                .setEnablePunctuation(true)
                .build(); // 1
        recognizer.prepare(); // 2


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                recognizer.startRecording(); // 3


// Запуск со стандарной GUI яндекса
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
                say("Ты лучший!");
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




    private void play(String text)
    {
        Random r = new Random();
        int i = r.nextInt(2);
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
        if ( (text.toString().toLowerCase().contains("бумага")))
        {
            switch (i)
            {
                case 0:
                    res += " ничья!";
                    break;
                case 1:
                    res += " я победила!";
                    break;
                case 2:
                    res += " я проиграла!";
                    break;
                default: res = "Фак!";
            }
        }
        else if ( (text.toString().toLowerCase().contains("ножницы")))
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
        else if ( (text.toString().toLowerCase().contains("камень")))
        {
            switch (i)
            {
                case 0:
                    res += " я победила! Загадывай еще.";
                    break;
                case 1:
                    res += " я проиграла! Загадывай еще.";
                    break;
                case 2:
                    res += " ничья! Загадывай еще.";
                    break;
                default: res = "Фак!";
            }
        }
        else
        {
            res = "Ты сказал мне: " + text.toString() + "? Загадывай еще.";
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


    private void startApp(String text)
    {
        Intent sendIntent =   getPackageManager().getLaunchIntentForPackage("com.mks.delete_me");
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "!!! --- Видео звонок ---");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void call(String text)
    {
        ArrayList<Contact> contacts = getAll(getApplicationContext());
        boolean isContactFinde = false;
        for (Contact contact : contacts) {
//            Log.e("!!!", "" + contact.toString() + " ? " + contact.getName().toLowerCase().contains(text));
            if(contact.getName().toLowerCase().contains(text)) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + contact.getPhone()));
                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    say("Не даны разрешения!");
                    State = action.SLEEP;
                    return;
                } else {
                    State = action.SLEEP;
                    isContactFinde = true;
                    startActivity(callIntent);
                }
                break;
            }
        }
        if(!isContactFinde) {
            say("Такой контакт не найден, пожалуйста, повторите еще раз.");
        }
    }



    public static ArrayList<Contact> getAll(Context context) {
        ContentResolver cr = context.getContentResolver();

        Cursor pCur = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{PHONE_NUMBER, PHONE_CONTACT_ID},
                null,
                null,
                null
        );
        if (pCur != null) {
            if (pCur.getCount() > 0) {
                HashMap<Integer, ArrayList<String>> phones = new HashMap<>();
                while (pCur.moveToNext()) {
                    Integer contactId = pCur.getInt(pCur.getColumnIndex(PHONE_CONTACT_ID));

                    ArrayList<String> curPhones = new ArrayList<>();

                    if (phones.containsKey(contactId)) {
                        curPhones = phones.get(contactId);

                    }
                    curPhones.add(pCur.getString(0));

                    phones.put(contactId, curPhones);

                }
                Cursor cur = cr.query(
                        ContactsContract.Contacts.CONTENT_URI,
                        new String[]{CONTACT_ID, DISPLAY_NAME, HAS_PHONE_NUMBER},
                        HAS_PHONE_NUMBER + " > 0",
                        null,
                        DISPLAY_NAME + " ASC");
                if (cur != null) {
                    if (cur.getCount() > 0) {
                        ArrayList<Contact> contacts = new ArrayList<>();
                        while (cur.moveToNext()) {
                            int id = cur.getInt(cur.getColumnIndex(CONTACT_ID));
                            if (phones.containsKey(id)) {
                                Contact con = new Contact();
                                con.setMyId(id);
                                con.setName(cur.getString(cur.getColumnIndex(DISPLAY_NAME)));
                                con.setPhone(TextUtils.join(",", phones.get(id).toArray()));
                                contacts.add(con);
                            }
                        }
                        return contacts;
                    }
                    cur.close();
                }
            }
            pCur.close();
        }
        return null;
    }


    void changeLiseningState()
    {

        Log.e("!!!", " - " + isListening);
        isListening = (isListening ? false : true);
        Log.e("!!!", " - " + isListening);
        if (isListening)
        {
            b1.setVisibility(View.INVISIBLE);
        }else{
            b1.setVisibility(View.VISIBLE);
        }
    }
}
