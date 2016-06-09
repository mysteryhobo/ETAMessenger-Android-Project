package com.etamessenger.etamessengerproject;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.nfc.Tag;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.drive.query.internal.LogicalFilter;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

/**
 * Created by peter on 31/05/16.
 */
public class DistanceMatrixClient {
    private static final String TAG = DistanceMatrixClient.class.getSimpleName();
    private static DistanceMatrixClient instance;
    OnResultListener listener = null;
    LocationServiceClient GPSclient;
    private Context context;


    public static DistanceMatrixClient getInstance(Context context) {
        if (instance == null) {
            instance = new DistanceMatrixClient();
            instance.context = context;
            instance.GPSclient = LocationServiceClient.getInstance(context);
        }
        return instance;
    }
    private DistanceMatrixClient(){}

    public interface OnResultListener {
        void onTraveltimeResult(int timeInSeconds);
    }

    public void setCallBack(OnResultListener listener) {
        this.listener = listener;
    }

    public void connect() {
        GPSclient.connect();
    }

    public void disconnect() {
        GPSclient.disconnect();
    }

    public LatLng getCurrentLocation() {
        LatLng currentLocation = null;
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        } else {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(GPSclient.getGoogleApi());
            if (mLastLocation != null) {
                Log.i(TAG, "getCurrentLocation Lat: " + String.valueOf(mLastLocation.getLatitude()));
                Log.i(TAG, "getCurrentLocation Long: " + String.valueOf(mLastLocation.getLongitude()));
                currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            }
        }
        return currentLocation;
    }

    public void getTravelTime(final LatLng destination, Context context, String transportMode) {
        RequestQueue queue = Volley.newRequestQueue(context);
        final LatLng currentLocation = getCurrentLocation();
        //// TODO: 31/05/16 get travel settings and use them here (possibly pass them as parameters)
        if (currentLocation == null) System.out.println("11111");
        if (destination == null) System.out.println("2222222");
        if (context == null) System.out.println("3333333");
        if (transportMode == null) System.out.println("4444444");
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="
                + currentLocation.latitude + ","
                + currentLocation.longitude
                + "&destinations="
                + destination.latitude + ","
                + destination.longitude
                + "&mode=" + transportMode
                + "&key=";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //yo dawg so I heard you like JSONs
                            Log.i(TAG, "onResponse: " + response.toString());
                            int totalResult = (int) ((JSONObject) ((JSONObject) ((JSONObject) response.getJSONArray("rows").getJSONObject(0)).getJSONArray("elements").get(0)).get("duration")).get("value");
                            logResponse(currentLocation, destination, response, totalResult);
                            if (listener != null) listener.onTraveltimeResult(totalResult);
                        } catch (Exception e) {
                            // TODO: 31/05/16 handle error
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //// TODO: 31/05/16 handle error
                        error.printStackTrace();
                    }
                });

        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(jsObjRequest);
    }

    private void logResponse(LatLng currentPosition, LatLng destination, JSONObject response, int timeInSeconds) {
        Log.i(TAG, "Distance Matrix Response: \n" +
                "Current Location: " + currentPosition.toString() + "\n" +
                "Destination Location: " + destination.toString() + "\n" +
                "Response: " + response.toString() + "\n" +
                "Time: " + timeInSeconds);
    }
}
