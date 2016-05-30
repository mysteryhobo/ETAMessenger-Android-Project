package com.etamessenger.etamessengerproject;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by peter on 30/05/16.
 */
public class LocationServiceClient implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient mGoogleApiClient;
    static LocationServiceClient instance;
    Context context;

    static LocationServiceClient getInstance(Context context) {
        if (instance == null) {
            instance = new LocationServiceClient();
            instance.mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(instance)
                    .addOnConnectionFailedListener(instance)
                    .addApi(LocationServices.API)
                    .build();
        }
        instance.context = context;
        return instance;
    }

    private LocationServiceClient() {}

    public GoogleApiClient getGoogleApi() {
        return mGoogleApiClient;
    }

    public void disconnect() {
        mGoogleApiClient.disconnect();
    }

    public void connect() {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(context, "Connection Suspended", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(context, "Connection Failure : " + connectionResult.getErrorCode(),Toast.LENGTH_SHORT).show();
    }
}
