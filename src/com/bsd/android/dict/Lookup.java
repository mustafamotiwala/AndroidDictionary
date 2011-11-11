package com.bsd.android.dict;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

public class Lookup extends Activity
{
    Button lookupButton;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        lookupButton = (Button) findViewById(R.id.button_lookup);
        lookupButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view) {
                EditText inputWord = (EditText)findViewById(R.id.input_word);
                WordLookupTask lookupTask = new WordLookupTask((TextView)findViewById(R.id.meanings_view));
                lookupTask.execute(getString(R.string.dictionary_url),getString(R.string.vid),inputWord.getText().toString());
            }
        });
    }
}
