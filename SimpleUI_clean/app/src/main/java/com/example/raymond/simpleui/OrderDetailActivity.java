package com.example.raymond.simpleui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderDetailActivity extends AppCompatActivity {

    TextView note;
    TextView storeInfo;
    TextView menu;
    ImageView photo;
    ImageView staticMapImageView;
    WebView webView;
    MapFragment mapFregment;
    GoogleMap map;

    Switch switchMapPic;
    Switch switchMapWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        note = (TextView)findViewById(R.id.noteView);
        storeInfo = (TextView)findViewById(R.id.storeInfoView);
        menu = (TextView)findViewById(R.id.menuView);
        photo = (ImageView)findViewById(R.id.photoView);
        staticMapImageView = (ImageView)findViewById(R.id.staticMapImageView);
        webView = (WebView)findViewById(R.id.webView);
        mapFregment = (MapFragment)getFragmentManager().findFragmentById(R.id.mapFragment);
        mapFregment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
            }
        });

        /* For switch */
        switchMapPic = (Switch)findViewById(R.id.switchMapPic);
        switchMapWeb = (Switch)findViewById(R.id.switchMapWeb);

        switchMapPic.setChecked(false);
        switchMapPic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    staticMapImageView.setVisibility(View.VISIBLE);
                } else {
                    staticMapImageView.setVisibility(View.GONE);
                }
            }
        });
        switchMapWeb.setChecked(false);
        switchMapWeb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    webView.setVisibility(View.VISIBLE);
                }
                else
                {
                    webView.setVisibility(View.GONE);
                }
            }
        });

        /* For GeoCoding */
        String storeInformation = getIntent().getStringExtra("storeInfo");

        note.setText(getIntent().getStringExtra("note"));
        storeInfo.setText(getIntent().getStringExtra("storeInfo"));
        String menuResult = getIntent().getStringExtra("menu");

        try{
            JSONArray array = new JSONArray(menuResult);
            String text = "";

            for(int i=0; i<array.length(); i++)
            {
                JSONObject order = array.getJSONObject(i);

                String name = order.getString("name");
                String lNumber = order.getString("lNumber");
                String mNumber = order.getString("mNumber");

                text = text + name + " L: " + lNumber + " M: " + mNumber + "\n";
            }
            menu.setText(text);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        /* For photo loading */
        String url = getIntent().getStringExtra("photoURL");
        if (url != null)
        {
            Picasso.with(this).load(url).into(photo);
        }

        /* For GeoCoding */
        String address = storeInformation.split(",")[1];
        Log.d("debug", address);
        new GeoCodingTask().execute(address);

    }

    /* For GeoCoding */
    class GeoCodingTask extends AsyncTask<String, Void, byte[]>
    {
        private double[] latlng;
        private String url;

        @Override
        protected byte[] doInBackground(String... params) {

            String address = params[0];
            latlng = Utils.addressToLatLng(address);
            url = Utils.getStaticMapUrl(latlng, 17);
            return Utils.urlToBytes(url);
            //return new byte[0];
        }

        @Override
        protected void onPostExecute(byte[] bytes) {

            webView.loadUrl(url);

            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            staticMapImageView.setImageBitmap(bmp);
            //super.onPostExecute(bytes);

            /* Set Location @GoogleMap*/
            LatLng location = new LatLng(latlng[0], latlng[1]);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 17));

            String[] storeInfos = getIntent().getStringExtra("storeInfo").split(",");
            map.addMarker(new MarkerOptions()
                    .title(storeInfos[0])
                    .snippet(storeInfos[1])
                    .position(location));
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Toast.makeText(OrderDetailActivity.this, marker.getTitle(), Toast.LENGTH_LONG).show();
                    return false;
                }
            });

            /* For switch: default invisible */
            if (switchMapPic.isChecked() != true){staticMapImageView.setVisibility(View.GONE);}
            if (switchMapWeb.isChecked() != true){webView.setVisibility(View.GONE);}

        }
    }

}
