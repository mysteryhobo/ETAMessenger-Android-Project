package com.etamessenger.etamessengerproject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DistanceMatrixClient.OnResultListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    static final int REQUEST_CONTACT = 1;
    TextView mTextView;
    LocationServiceClient GPSclient;
    final Context context = this;
//    private static LatLng destination;
    private ProgressBar spinner;
    private DistanceMatrixClient distanceClient;
//    private String travelMode = "driving";
//    private Place currentDestination;
    private ImageButton drivingButton;
    private ImageButton bikingButton;
    private ImageButton walkingButton;
    private RecyclerView messageList;
    private static final int AT_LOCATAION = 0;
    private ArrayList<Contact> contacts = new ArrayList<>();
    private MessageItemAdaptor msgAdaptor;
    private Button startButton;
    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(MainActivity.this, TripsActivity.class);
        startActivity(intent);

        /**
         * INITIALIZATION
         */
        GPSclient = LocationServiceClient.getInstance(this);
        mTextView = (TextView) findViewById(R.id.responseHolder);
        trip = new Trip(this, "driving"); //todo replace driving with preferneces
        trip.setmTextView(mTextView); //todo replace with map thin
        ArrayList<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact("emma", "2894046620"));
        trip.setContacts(contacts);
        distanceClient = DistanceMatrixClient.getInstance(this);
        distanceClient.setCallBack(this);


        /**
         * AUTOCOMPLETE DESTINATION SEARCH
         */
        final PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setHint("Enter Destination:");
        autocompleteFragment.setHasOptionsMenu(true);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            Trip trip;

            private PlaceSelectionListener init(Trip trip) {
                this.trip = trip;
                return this;
            }

            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                enableTravelModeButtons(false);
                spinner.setVisibility(View.VISIBLE);
                trip.setLatCoord(Double.toString(place.getLatLng().latitude));
                trip.setLongCoord(Double.toString(place.getLatLng().longitude));
                distanceClient.getTravelTime(trip);
//                Log.i(TAG, "Place: " + place.getName() + ", latLong: " + destination.toString());
//                Log.i(TAG, "response");
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        }.init(trip));

        /**
         * TRAVELMODE BUTTONS
         */
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
        setTravelModeButtonsState(null, trip.getTravelmode());

        /**
         * MESSAGE LIST VIEW
         */
        messageList = (RecyclerView) findViewById(R.id.RV_messageList);
        messageList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        messageList.setLayoutManager(llm);

        spinner = (ProgressBar) findViewById(R.id.progressBar1);

        /**
         * CONTACTS
         */
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

//        final EditText number = (EditText) findViewById(R.id.editText_number);
//        final EditText message = (EditText) findViewById(R.id.editText_message);
//        Button MsgBtn = (Button) findViewById(R.id.btn_sendMsg);
//        assert MsgBtn != null;
//        assert number != null;
//        assert message != null;
//        MsgBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SmsManager smsManager = SmsManager.getDefault();
//                smsManager.sendTextMessage("+1" + number.getText().toString(), null, message.getText().toString(), null, null);
//            }
//        });

        /**
         * START BUTTON
         */
        startButton = (Button) findViewById(R.id.btn_start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                TripTask task = new TripTask();
//                //todo check trip for null fields and prmpt user for fix
//                task.doInBackground(trip)
//                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                TripTask tripTask = new TripTask();
                tripTask.doInBackground(trip);

            }
        });
    }


    /**
     * MESSAGES METHODS
     */

    private ArrayList<Message> generateMessages(int travelTime) {
        int timeInMinutes = travelTime / 60;
        ArrayList<Message> messages = new ArrayList<>();
        messages.add(new Message(getResources().getString(R.string.leaving_now).replaceAll("%", String.valueOf(timeInMinutes)), timeInMinutes));
        if (timeInMinutes > 120) {
            messages.add(new Message(getResources().getString(R.string.aboutXAway).replaceAll("%", "60"), 60));
            messages.add(new Message(getResources().getString(R.string.illBeThere).replaceAll("%", "15"), 15));
        } else if (timeInMinutes > 60) {
            messages.add(new Message(getResources().getString(R.string.illBeThere).replaceAll("%", "15"), 15));
        } else if (timeInMinutes > 15) {
            messages.add(new Message(getResources().getString(R.string.illBeThere).replaceAll("%", "5"), 5));
        }
        messages.add(new Message(getResources().getString(R.string.here), AT_LOCATAION));
        return messages;
    }


    public void deleteMessage(List<Message> messages, int[] reverseSortedPositions, MessageItemAdaptor msgAdaptor) {
        Message deletedMessage = null;
        int deletedPosition = -1;
        for (int position : reverseSortedPositions) {
            deletedMessage = messages.get(position);
            deletedPosition = position;
            messages.remove(position);
            msgAdaptor.notifyItemRemoved(position);
        }
        msgAdaptor.notifyDataSetChanged();
        Snackbar snackbar = Snackbar
                .make(messageList, "Message deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {

                    Message msg;
                    int position;
                    List<Message> messages;
                    MessageItemAdaptor msgAdaptor;

                    private View.OnClickListener init(Message msg, int position, List<Message> messages, MessageItemAdaptor msgAdaptor) {
                        this.msg = msg;
                        this.position = position;
                        this.messages = messages;
                        this.msgAdaptor = msgAdaptor;
                        return this;
                    }

                    @Override
                    public void onClick(View v) {
                        if (msg != null && position != -1) {
                            messages.add(position, msg);
                            msgAdaptor.notifyDataSetChanged();
                        }
                    }
                }.init(deletedMessage, deletedPosition, messages, msgAdaptor));
        snackbar.show();
    }

    /**
     * TRAVEL BUTTON METHODS
     */

    @Override
    public void onTraveltimeResult(int timeInSeconds) {
        Log.i(TAG, "onTraveltimeResult: Received Result:" + timeInSeconds);
        mTextView.setText("Time Results: " + timeInSeconds + " Mins: " + timeInSeconds/60);

        final List<Message> messages = generateMessages(timeInSeconds);
        trip.setMessages((ArrayList<Message>) messages);
        msgAdaptor = new MessageItemAdaptor(messages, context);
        messageList.setAdapter(msgAdaptor);

        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(messageList,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {
                            @Override
                            public boolean canSwipeLeft(int position) {
                                return true;
                            }

                            @Override
                            public boolean canSwipeRight(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(final RecyclerView recyclerView, int[] reverseSortedPositions) {
                                deleteMessage(messages, reverseSortedPositions, msgAdaptor);

                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                deleteMessage(messages, reverseSortedPositions, msgAdaptor);
                            }
                        });

        messageList.addOnItemTouchListener(swipeTouchListener);
        spinner.setVisibility(View.GONE);
        enableTravelModeButtons(true);
    }


    private void enableTravelModeButtons(boolean enable) {
        drivingButton.setEnabled(enable);
        bikingButton.setEnabled(enable);
        walkingButton.setEnabled(enable);
    }

    private void setTravelModeButtonsState(String prevTravelMode, String travelMode) {
        if (prevTravelMode != null) {
            switch (prevTravelMode) {
                case "driving":
                    drivingButton.setImageResource(R.drawable.ic_car_primary);
                    drivingButton.setBackgroundResource(R.drawable.btn_white_roundcorner);
                    break;
                case "bicycling":
                    bikingButton.setImageResource(R.drawable.ic_bike_primary);
                    bikingButton.setBackgroundResource(R.drawable.btn_white_roundcorner);
                    break;
                case "walking":
                    walkingButton.setImageResource(R.drawable.ic_walk_primary);
                    walkingButton.setBackgroundResource(R.drawable.btn_white_roundcorner);
                    break;
                default: break;
            }
        }

        switch (travelMode) {
            case "driving":
                drivingButton.setImageResource(R.drawable.ic_car_white);
                drivingButton.setBackgroundResource(R.drawable.btn_blue_roundcorner);
                break;
            case "bicycling":
                bikingButton.setImageResource(R.drawable.ic_bike_white);
                bikingButton.setBackgroundResource(R.drawable.btn_blue_roundcorner);
                break;
            case "walking":
                walkingButton.setImageResource(R.drawable.ic_walk_white);
                walkingButton.setBackgroundResource(R.drawable.btn_blue_roundcorner);
                break;
        }
    }

    private class TransitButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String newTravelMode = null;
            switch (v.getId()) {
                //// TODO: 01/06/16 add button highlighting
                case R.id.btn_driving:
                    newTravelMode = "driving";
                    break;
                case R.id.btn_bicycling:
                    newTravelMode = "bicycling";
                    break;
                case R.id.btn_walking:
                    newTravelMode = "walking";
                    break;
                default:
                    Log.i(TAG, "onClick: Invalid btn press handled");
            }
            if (!trip.getTravelmode().equals(newTravelMode)) {
                setTravelModeButtonsState(trip.getTravelmode(), newTravelMode);
                spinner.setVisibility(View.VISIBLE);
                trip.setTravelmode(newTravelMode);
                enableTravelModeButtons(false);
                if (trip.getLatCoord() != null && trip.getLongCoord() != null) distanceClient.getTravelTime(trip);
            }
        }
    }

    /**
     * CONTACTS RESULTS
     */
    
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

                contacts.add(new Contact(name, number));
                mTextView.setText("Name: " + name + ", Num: " + number);
                //contactEmail.setText(email);
            }
        }
    }

    protected void onStart() {
        distanceClient.connect();
//        GPSclient.connect();
        super.onStart();
    }

    protected void onStop() {
        distanceClient.disconnect();
//        GPSclient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //TODO HANDLE THIS STUFF
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
