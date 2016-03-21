package com.example.raymond.simpleui;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    /* btn_Done was triggered -> getData() & Back to MainActivity with data for onActivityResult */
    public void doneOnClick(View view)
    {
        //return order list
        JSONArray array = getData();
        Intent data = new Intent();
        data.putExtra("result", array.toString());
        setResult(RESULT_OK, data);
        finish();
//        //Just return text: Order Done!
//        Intent data = new Intent();
//        data.putExtra("result", "Order Done!");
//        setResult(RESULT_OK, data);
//        finish();
    }

    /* btn_Cancel was triggered -> Back to MainActivity without data for onActivityResult */
    public void cancelOnClick(View view)
    {
        finish();
    }

    /* btn_Done -> getData() */
    public JSONArray getData()
    {
        LinearLayout rootLinearLayout = (LinearLayout)findViewById(R.id.root);
        int count = rootLinearLayout.getChildCount();

        JSONArray array = new JSONArray(); //Mainly return

        for(int i=0; i < count-1; i++) //Last array was not included
        {
            LinearLayout ll = (LinearLayout)rootLinearLayout.getChildAt(i);
            TextView drinkNameTextView = (TextView)ll.getChildAt(0);
            Button lButton = (Button)ll.getChildAt(1);
            Button mButton = (Button)ll.getChildAt(2);

            String drinkName = drinkNameTextView.getText().toString();
            int lNumber = Integer.parseInt(lButton.getText().toString());
            int mNumber = Integer.parseInt(mButton.getText().toString());

            try
            {
                JSONObject object = new JSONObject();

                object.put("name", drinkName);
                object.put("lNumber", lNumber);
                object.put("mNumber", mNumber);
                array.put(object);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array;
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
