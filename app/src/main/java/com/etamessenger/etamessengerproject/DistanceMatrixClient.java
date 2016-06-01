package com.etamessenger.etamessengerproject;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.drive.query.internal.LogicalFilter;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

/**
 * Created by peter on 31/05/16.
 */
public class DistanceMatrixClient {
    private static final String TAG = DistanceMatrixClient.class.getSimpleName();
    private static DistanceMatrixClient instance;
    OnResultListener listener = null;

    public static DistanceMatrixClient getInstance() {
        if (instance == null) {
            instance = new DistanceMatrixClient();
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

    public void getTravelTime(final LatLng currentLocation, final LatLng destination, Context context, String transportMode) {
        RequestQueue queue = Volley.newRequestQueue(context);
        //// TODO: 31/05/16 get travel settings and use them here (possibly pass them as parameters)
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
