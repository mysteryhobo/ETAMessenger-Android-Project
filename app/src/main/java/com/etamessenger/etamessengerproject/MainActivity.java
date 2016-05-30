package com.etamessenger.etamessengerproject;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    TextView mTextView;
//    GoogleApiClient mGoogleApiClient;
    LocationServiceClient GPSclient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GPSclient = LocationServiceClient.getInstance(this);

//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();


        mTextView = (TextView) findViewById(R.id.responseHolder);

        Button MapsBtn = (Button) findViewById(R.id.btn_maps);
        assert MapsBtn != null;
        MapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
//                startActivity(intent);
                startRequest();
                getCurrentLocation();
            }
        });

        final EditText number = (EditText) findViewById(R.id.editText_number);
        final EditText message = (EditText) findViewById(R.id.editText_message);
        Button MsgBtn = (Button) findViewById(R.id.btn_sendMsg);
        assert MsgBtn != null;
        assert number != null;
        assert message != null;
        MsgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("+1" + number.getText().toString(), null, message.getText().toString(), null, null);
            }
        });
    }

    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(GPSclient.getGoogleApi());
        if (mLastLocation != null) {
            Log.i(TAG, "getCurrentLocation Lat: " + String.valueOf(mLastLocation.getLatitude()));
            Log.i(TAG, "getCurrentLocation Long: " + String.valueOf(mLastLocation.getLongitude()));
        }
    }

    protected void onStart() {
//        mGoogleApiClient.connect();
        GPSclient.connect();
        super.onStart();
    }

    protected void onStop() {
//        mGoogleApiClient.disconnect();
        GPSclient.disconnect();
        super.onStop();
    }



    public void startRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=2022+Cedarwood+Court+Pickering+ON&destinations=1099+Glenbourne+drive+Oshawa+ON&key=";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i(TAG, "onResponse: " + response.toString());
                            mTextView.setText(response.toString());

                            //yo dawg so I heard you like JSONs
                            int totalResult = (int) ((JSONObject) ((JSONObject) ((JSONObject) response.getJSONArray("rows").getJSONObject(0)).getJSONArray("elements").get(0)).get("duration")).get("value");

//                            JSONArray resultsHolder = response.getJSONArray("destination_addresses");
//                            JSONArray resultsHolder2 = response.getJSONArray("rows");
//                            JSONObject resultsHolder3 = (JSONObject) resultsHolder2.get(0);
//                            JSONArray resultsHolder4 = resultsHolder3.getJSONArray("elements");
//                            JSONObject resultsHolder5 = (JSONObject) resultsHolder4.get(0);
//                            JSONObject resultsHolder6 = (JSONObject) resultsHolder5.get("duration");
//                            int resultsHolder7 = (int) resultsHolder6.get("value");



                            Log.i(TAG, "onResponse: " + totalResult);
//                            mTextView.setText(response.toString());
//                            JSONArray results = resultsHolder.getJSONArray("results");
//                            JSONObject result1 = (JSONObject) resultsHolder.get(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

//        UMLSConnection.getInstance(context).addToRequestQueue(jsObjRequest);
        queue.add(jsObjRequest);


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

//    public void startRequest() {
//        RequestQueue queue = Volley.newRequestQueue(this);
////        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=2022+Cedarwood+Court+Pickering+ON&destination=1099+Glenbourne+drive+Oshawa+ON&key=";
//        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=2022+Cedarwood+Court+Pickering+ON&destinations=1099+Glenbourne+drive+Oshawa+ON&key=";
//        JsonObjectRequest request = new JsonObjectRequest
//                (Request.Method.GET, url, (String) null, new Response.Listener<JSONObject>() {
//
//        })
//
//
//
//
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                mTextView.setText("Response is: " + response);
//                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//                Document doc = Jsoup.parse(response);
//                Elements elements = doc.select("origin_addresses");
//                JSONArray resultsHolder = response.getJSONArray("result");
//                Log.i(TAG, "onResponse: " + elements.toString());
////                elements = doc.select("elements");
////                tgt = elements.attr("action");
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.i(TAG, "onErrorResponse: " + error.toString());
//                error.printStackTrace();
////                mTextView.setText("That didn't work!");
//            }
//        });
//        queue.add(stringRequest);
//    }
}
