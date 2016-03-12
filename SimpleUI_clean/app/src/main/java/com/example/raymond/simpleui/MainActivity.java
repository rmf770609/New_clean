package com.example.raymond.simpleui;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    /* Declare variables */
    TextView textView;
    EditText editText;
    CheckBox hidecheckBox;
    Button button_restore;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Path:res/layout/activity_main.xml

        /* Put value into variables */
        textView = (TextView)findViewById(R.id.textView);
        editText = (EditText)findViewById(R.id.editText);
        hidecheckBox = (CheckBox)findViewById(R.id.hide_checkBox);

        /* Setup button_restore & related triggered event */
        button_restore = (Button)findViewById(R.id.button_restore);
        button_restore.setOnClickListener(button_restoreOnClick);
        /* Setup Shared Preference */
        sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = sp.edit();
        /* Record checkbox status */
        hidecheckBox.setChecked(sp.getBoolean("hideCheckBox" , false));
        hidecheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("hideCheckBox" , hidecheckBox.isChecked());
                editor.commit();
            }
        });

        /* Keyboard detected */
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    submit(v);
                    return true;
                }
                return false;
            }
        });
        /* Virtual keyboard detected */
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    submit(v);
                    return true;
                }
                return false;
            }
        });
    }

    /* onClick event @button */
    public void submit(View view)
    {
        String text = editText.getText().toString(); //get string from edit text
        if (hidecheckBox.isChecked())
        {
            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
            textView.setText("**********");
            editText.setText("**********");
        }
        else
        {
            textView.setText(text);
        }
        /* Put into SP */
        editor.putString("editText" , editText.getText().toString());
        editor.apply();
        /* Clean editText */
        editText.setText("");
    }

    /* onClick event @button_restore */
    private Button.OnClickListener button_restoreOnClick = new Button.OnClickListener()
    {
        public void onClick(View v)
        {
            editText.setText(sp.getString("editText" , ""));
            //editText.setText("RESTORE");
        }
    };
}
