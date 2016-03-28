package com.example.raymond.simpleui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    /* Define requestCode for Intent */
    private static final int REQUEST_CODE_MENU_ACTIVITY = 0;
    private static final int REQUEST_CODE_CAMERA = 1;

    private Boolean hasPhoto = false;

    /* Declare variables */
    TextView textView;
    EditText editText;
    CheckBox hidecheckBox;
    Button button_restore;
    Button btnClean;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    ListView listView;
    Spinner spinner;
    String menuResult="";
    ImageView photoView;
    ProgressDialog progressDialog;
    ProgressBar progressBar;
    private List<ParseObject> queryResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Path:res/layout/activity_main.xml

        /* Put value into variables */
        textView = (TextView)findViewById(R.id.textView);
        editText = (EditText)findViewById(R.id.editText);
        hidecheckBox = (CheckBox)findViewById(R.id.hide_checkBox);
        listView = (ListView)findViewById(R.id.listView);
        spinner = (Spinner)findViewById(R.id.spinner);
        photoView = (ImageView)findViewById(R.id.imageView);
        progressDialog = new ProgressDialog(this);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        /* Setup button_restore & related triggered event */
        button_restore = (Button)findViewById(R.id.button_restore);
        button_restore.setOnClickListener(button_restoreOnClick);
        /* Setup btnClean & related clean SP event */
        btnClean = (Button)findViewById(R.id.btnClean);
        btnClean.setOnClickListener(btnCleanOnClick);

        /* Setup Shared Preference */
        sp = getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = sp.edit();

        /* Record checkbox status */
        hidecheckBox.setChecked(sp.getBoolean("hideCheckBox" , false));
        hidecheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("hideCheckBox", hidecheckBox.isChecked());
                editor.commit();

                if (isChecked)
                {
                    photoView.setVisibility(View.GONE);
                }
                else
                {
                    photoView.setVisibility(View.VISIBLE);
                }
            }
        });

        /* Keyboard detected */
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
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

        listView.setVisibility(View.GONE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToDetailOrder(position);
            }
        });

        setListView();
        setSpinner();
    }

    /* Set listView */
    private void setListView()
    {
        //ParseQuery form parse server
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Order");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null)
                {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                queryResult = objects;

                List<Map<String, String>> data = new ArrayList<Map<String, String>>();

                for (int i=0; i<queryResult.size(); i++)
                {
                    ParseObject object = queryResult.get(i);
                    String note = object.getString("note");
                    String storeInfo = object.getString("storeInfo");
                    String menu = object.getString("menu");

                    Map<String, String> item = new HashMap<String, String>();

                    item.put("note", note);
                    item.put("storeInfo", storeInfo);
                    item.put("drinkNum", getDrinkNumber(menu));

                    data.add(item);
                }

                String[] from = {"note", "storeInfo", "drinkNum"};
                int[] to = {R.id.noteView, R.id.storeInfoView, R.id.drinkNum};

                SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, data, R.layout.listitem_item, from, to);

                listView.setAdapter(adapter);

                progressBar.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
            }
        });

//        // Show local history list from history.txt
//        String[] data = Utils.readFile(this, "history.txt").split("\n"); //.split() NEED to check further
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
//        listView.setAdapter(adapter);
    }

    /* Count total amount from order menu */
    private String getDrinkNumber(String menu) {
        String menuNumber = "";
        int totalNumber = 0;

        try{
            JSONArray menuArray = new JSONArray(menu);

            for(int i=0; i<menuArray.length(); i++)
            {
                JSONObject order = menuArray.getJSONObject(i);
                totalNumber += order.getInt("lNumber") + order.getInt("mNumber");
                menuNumber = String.valueOf(totalNumber);
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        return menuNumber;
    }

    /* Set Spinner */
    private void setSpinner()
    {
        /* Get storeInfo from Parse server  */
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("StoreInfo");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e != null)
                {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                String[] stores = new String[objects.size()];
                for (int i=0; i<objects.size(); i++)
                {
                    ParseObject object = objects.get(i);
                    stores[i] = object.getString("name") + ", " + object.getString("address");
                }

                ArrayAdapter<String> storeAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, stores);
                spinner.setAdapter(storeAdapter);
            }
        });
//        /* Get storeInfo from local array.xml */
//        String[] data = getResources().getStringArray(R.array.storeInfo);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
//        spinner.setAdapter(adapter);
    }

    /* onClick event @button */
    public void submit(View view)
    {
        String text = editText.getText().toString(); //get string from edit text
        Utils.writeFile(this, "history.txt", text + '\n');

        /* Submit data to Parse server */
        ParseObject orderObject = new ParseObject("Order");

        orderObject.put("note", text);
        orderObject.put("storeInfo", spinner.getSelectedItem());
        orderObject.put("menu", menuResult);

        progressDialog.setTitle("Loading...");
        progressDialog.show();

        orderObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                progressDialog.dismiss();

                if (e == null)
                {
                    Toast.makeText(MainActivity.this, "Submit OK", Toast.LENGTH_LONG);

                    hasPhoto = false;
                    photoView.setImageResource(0);
                    textView.setText("");
                    editText.setText("");
                    setListView();
                }

            }
        });

//        if (hidecheckBox.isChecked())
//        {
//            Toast.makeText(this, text, Toast.LENGTH_LONG).show();
//            textView.setText("**********");
//            editText.setText("**********");
//        }
//        else
//        {
//            textView.setText(text);
//        }
        /* Show in listview */
        setListView();
        /* Put into SP */
        editor.putString("editText" , editText.getText().toString());
        editor.apply();
        /* Clean editText */
        editText.setText("");
        /* Refresh textView */
        textView.setText(text);
    }

    /* onClick event @button_restore */
    private Button.OnClickListener button_restoreOnClick = new Button.OnClickListener()
    {
        public void onClick(View v)
        {
            editText.setText(sp.getString("editText", ""));
            textView.setText(sp.getString("order", ""));
            //editText.setText("RESTORE");
        }
    };

    /* onCLick event @btnClean */
    private Button.OnClickListener btnCleanOnClick = new Button.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            Utils.cleanFile(MainActivity.this, "history.txt", "");
            Toast.makeText(MainActivity.this, "Clean", Toast.LENGTH_LONG).show(); //WHY cannot this
            textView.setText("");
            editText.setText("");
            setListView();
        }
    };

    /* Debug message for LifeCycle */
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Log.d("debug", "Main activity onCreate");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("debug", "Main activity onRestart");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("debug", "Main activity onStart");
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("debug", "Main activity onResume");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("debug", "Main activity onPause");
    }
    @Override
    protected void onStop() {
        super.onStop();
        Log.d("debug", "Main activity onStop");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("debug", "Main activity onDestroy");
    }

    /* Intent to DrinkMenuActivity */
    public void goToMenu(View view)
    {
        Intent intent = new Intent();
        intent.setClass(this, DrinkMenuActivity.class);
        startActivityForResult(intent, REQUEST_CODE_MENU_ACTIVITY); // Safer than startActivity(intent);
    }
    /* Debug & confirm message for receiving data from DrinkMenuActivity */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("debug", "Main activity onActivityResult");

        if (requestCode == REQUEST_CODE_MENU_ACTIVITY)
        {
            if (resultCode == RESULT_OK)
            {
                Log.d("debug", "Main order done");

                //Get order menu from DrinkMenuActivity
                menuResult = data.getStringExtra("result");

                try
                {
                    JSONArray array = new JSONArray(menuResult);
                    String text="";

                    for(int i=0; i<array.length(); i++)
                    {
                        JSONObject order = array.getJSONObject(i);

                        String name = order.getString("name");
                        String lNumber = order.getString("lNumber");
                        String mNumber = order.getString("mNumber");

                        text = text + name + " L: " + lNumber + " M: " + mNumber + "\n";
                    }
                    textView.setText(text);
                    /* Shared Preference can reload menu*/
                    editor.putString("order", text);
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                //Just get text: "Order Done!" from DrinkMenuActivity
//                textView.setText(data.getStringExtra("result"));
            }
        }
        else if (requestCode == REQUEST_CODE_CAMERA)
        {
            Log.d("debug", "REQUEST_CODE_CAMERA");
            if (resultCode == RESULT_OK)
            {
                Log.d("debug", "CAMERA_RESULT_OK");
                photoView.setImageURI(Utils.getPhotoUri());
                hasPhoto = true;
            }
        }

    }

    /* Option list for Take Photo */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_take_photo)
        {
            Toast.makeText(this, "Take Photo", Toast.LENGTH_LONG).show();
            goToCamera();
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToCamera()
    {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Utils.getPhotoUri());
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    public void goToDetailOrder(int position)
    {
        ParseObject object = queryResult.get(position);

        Intent intent = new Intent();
        intent.setClass(this, OrderDetailActivity.class);

        intent.putExtra("note", object.getString("note"));
        intent.putExtra("storeInfo", object.getString("storeInfo"));
        intent.putExtra("menu", object.getString("menu"));

        if (object.getParseFile("photo") != null)
        {
            intent.putExtra("photoURL", object.getParseFile("photo").getUrl());
        }
        startActivity(intent);
    }

}
