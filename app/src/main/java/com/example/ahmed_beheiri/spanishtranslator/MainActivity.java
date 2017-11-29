package com.example.ahmed_beheiri.spanishtranslator;


import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.ahmed_beheiri.spanishtranslator.TranslatorBackgroundTask;

import java.net.URL;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private EditText fromet, toet;
    private Button translatebutton;
    private ImageButton soundtranslate;
    Context context=this;

    //TTS object
    private TextToSpeech myTTS;
    //status check code
    private int MY_DATA_CHECK_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fromet = (EditText) findViewById(R.id.fromet);
        toet = (EditText) findViewById(R.id.toet);
        translatebutton = (Button) findViewById(R.id.translatebtn);
        soundtranslate=(ImageButton)findViewById(R.id.readtranslate);


        //check for TTS data
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

        translatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text=fromet.getText().toString();
                if(text.isEmpty()){
                    Toast.makeText(context,"Please enter text To translate",Toast.LENGTH_SHORT).show();
                }else {
                    toet.setText(Translate(text, "es"));
                }
            }
        });
        soundtranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String words = toet.getText().toString();
                speakWords(words);
            }
        });
    }

    public String Translate(String textToBeTranslated,String languagePair){
        TranslatorBackgroundTask translatorBackgroundTask=new TranslatorBackgroundTask(context);
        String translationResult = null; // Returns the translated text as a String
        try {
            translationResult = translatorBackgroundTask.execute(textToBeTranslated,languagePair).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.d("Translation Result",translationResult); // Logs the result in Android Monitor
        return translationResult;
    }


    //speak the user text
    private void speakWords(String speech) {

        //speak straight away
        myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
    }
    //act on result of TTS data check
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //the user has the necessary data - create the TTS
                myTTS = new TextToSpeech(this, this);
            }
            else {
                //no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }
    @Override
    public void onInit(int initStatus) {
        //check for successful instantiation
        if (initStatus == TextToSpeech.SUCCESS) {
            if(myTTS.isLanguageAvailable(Locale.US)==TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.US);
        }
        else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }
}
