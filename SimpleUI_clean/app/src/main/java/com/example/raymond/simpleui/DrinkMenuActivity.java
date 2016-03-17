package com.example.raymond.simpleui;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class DrinkMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_menu);
    }

    /* Function: add_amount: for button was triggered to add amount of drinks */
    public void add_amount(View view)
    {
        Button button = (Button) view;
        int number = Integer.parseInt(button.getText().toString());
        number++;
        button.setText(String.valueOf(number));
    }

    /* btn_Done was triggered -> Back to MainActivity with data for onActivityResult */
    public void doneOnClick(View view)
    {
        Intent data = new Intent();
        data.putExtra("result", "Order Done!");
        setResult(RESULT_OK, data);
        finish();
    }
    /* btn_Cancel was triggered -> Back to MainActivity without data for onActivityResult */
    public void cancelOnClick(View view)
    {
        finish();
    }

    /* Debug message for LifeCycle */
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Log.d("debug", "DrinkMenu activity onCreate");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("debug", "DrinkMenu activity onRestart");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("debug", "DrinkMenu  activity onStart");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debug", "DrinkMenu  activity onResume");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("debug", "DrinkMenu  activity onPause");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("debug", "DrinkMenu  activity onStop");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("debug", "DrinkMenu  activity onDestroy");
    }
}
