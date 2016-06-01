package com.etamessenger.etamessengerproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DistanceMatrixClient.OnResultListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    static final int REQUEST_CONTACT = 1;
    TextView mTextView;
    LocationServiceClient GPSclient;
    final Context context = this;
    private static LatLng destination;
    private ProgressBar spinner;
    DistanceMatrixClient distanceClient;
    private String travelMode = "driving";
    private Place currentDestination;
    private ImageButton drivingButton;
    private ImageButton bikingButton;
    private ImageButton walkingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GPSclient = LocationServiceClient.getInstance(this);
        mTextView = (TextView) findViewById(R.id.responseHolder);

        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint("Enter Destination:");
        autocompleteFragment.setHasOptionsMenu(true);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                enableTravelModeButtons(false);
                spinner.setVisibility(View.VISIBLE);
                currentDestination = place;
                destination = place.getLatLng();
                distanceClient.getTravelTime(getCurrentLocation(), destination, context, travelMode);
                Log.i(TAG, "Place: " + place.getName() + ", latLong: " + destination.toString());
                Log.i(TAG, "response");
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
        
        TransitButtonListener transitButtonListener = new TransitButtonListener();
        drivingButton = (ImageButton) findViewById(R.id.btn_driving);
        assert drivingButton != null;
        drivingButton.setOnClickListener(transitButtonListener);
        bikingButton = (ImageButton) findViewById(R.id.btn_bicycling);
        assert bikingButton != null;
        bikingButton.setOnClickListener(transitButtonListener);
        walkingButton = (ImageButton) findViewById(R.id.btn_walking);
        assert walkingButton != null;
        walkingButton.setOnClickListener(transitButtonListener);

        distanceClient = DistanceMatrixClient.getInstance();
        distanceClient.setCallBack(this);
        Button MapsBtn = (Button) findViewById(R.id.btn_maps);
        assert MapsBtn != null;
        MapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
//                startActivity(intent);
                distanceClient.getTravelTime(getCurrentLocation(), getCurrentLocation(), context, travelMode);
            }
        });

        Button contactButton = (Button) findViewById(R.id.btn_selectContact);
        assert contactButton != null;
        contactButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, 1);
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
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CONTACT) {
            if (resultCode == RESULT_OK) {
                Uri contactData = data.getData();
                Cursor cursor = managedQuery(contactData, null, null, null, null);
                cursor.moveToFirst();

                String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                //contactName.setText(name);
                mTextView.setText("name: " + name + "num: " + number);
                //contactEmail.setText(email);
            }
        }
    }

    private void enableTravelModeButtons(boolean enable) {
        drivingButton.setEnabled(enable);
        bikingButton.setEnabled(enable);
        walkingButton.setEnabled(enable);
    }
    
    private class TransitButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String newTrasitMode = null;
            switch (v.getId()) {
                //// TODO: 01/06/16 add button highlighting
                case R.id.btn_driving:
                    newTrasitMode = "driving";
                    break;
                case R.id.btn_bicycling:
                    newTrasitMode = "bicycling";
                    break;
                case R.id.btn_walking:
                    newTrasitMode = "walking";
                    break;
                default:
                    Log.i(TAG, "onClick: Invalid btn press handled");
            }
            if (!travelMode.equals(newTrasitMode)) {
                spinner.setVisibility(View.VISIBLE);
                travelMode = newTrasitMode;
                enableTravelModeButtons(false);
                distanceClient.getTravelTime(getCurrentLocation(), destination, context, travelMode);
            }
        }
    }

    @Override
    public void onTraveltimeResult(int timeInSeconds) {
        Log.i(TAG, "onTraveltimeResult: Received Result:" + timeInSeconds);
        mTextView.setText("Time Results: " + timeInSeconds + " Mins: " + timeInSeconds/60);
        spinner.setVisibility(View.GONE);
        enableTravelModeButtons(true);
    }

    public LatLng getCurrentLocation() {
        LatLng currentLocation = null;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    protected void onStart() {
        GPSclient.connect();
        super.onStart();
    }

    protected void onStop() {
        GPSclient.disconnect();
        super.onStop();
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
}
