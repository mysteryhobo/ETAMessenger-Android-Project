package com.etamessenger.etamessengerproject;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peter on 30/06/16.
 */
public class TripItemAdapter extends RecyclerView.Adapter<TripItemAdapter.TripViewHolder> implements DistanceMatrixClient.OnResultListener {
    private List<Trip> tripList;
    private Context context;
    private static final String TAG = "TripItemAdapter";
    ArrayList<TripItemAdapter.TripViewHolder> holders = new ArrayList<>();


    public TripItemAdapter(List<Trip> tripList, Context context) {
        this.tripList = tripList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder (final TripItemAdapter.TripViewHolder holder, int position) {
        holders.add(holder);
        Trip currTrip = tripList.get(position);
        holder.tripText.setText(currTrip.getName());
        String travelMode = currTrip.getTravelmode();
        if (travelMode.equals("driving")) holder.travelModeIndicator.setImageResource(R.drawable.ic_car_primary);
        else if (travelMode.equals("walking")) holder.travelModeIndicator.setImageResource(R.drawable.ic_walk_primary);
        else holder.travelModeIndicator.setImageResource(R.drawable.ic_bike_primary);

//        LocationServiceClient GPSClient = LocationServiceClient.getInstance(context);
        currTrip.setContext(context);
        currTrip.startLocation = getCurrentLocation();
        Log.i(TAG, "onBindViewHolder: " + currTrip.startLocation);
        DistanceMatrixClient GPSClient = DistanceMatrixClient.getInstance(context);
        GPSClient.getTravelTime(currTrip, new DistanceMatrixClient.OnResultListener() {
            @Override
            public void onTraveltimeResult(int timeInSeconds) {
                holder.travelTime.setText(timeInSeconds + "");
            }
        });

        holder.travelTime.setText("(9 mins)"); //todo connect with distance matrix client
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX" + currTrip);
        for (Contact currContact : currTrip.getContacts()) {
            holder.contactsHolder.addView(new ContactView(context, currContact.getName().substring(0,1).toUpperCase() + currContact.getName().substring(1)));
        }
        holder.sizeText.setText(holder.contactsHolder.getMeasuredWidth() + "");
//        holder.msgsText.setText(context.getString(R.string.num_of_msgs, currTrip.getMessages().size()));
    }


    public LatLng getCurrentLocation() {
        LatLng currentLocation = null;
        try {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(LocationServiceClient.getInstance(context).getGoogleApi());
            if (mLastLocation != null) {
                Log.i(TAG, "getCurrentLocation Lat: " + String.valueOf(mLastLocation.getLatitude()));
                Log.i(TAG, "getCurrentLocation Long: " + String.valueOf(mLastLocation.getLongitude()));
                currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            }
            return currentLocation;
        } catch (SecurityException noPermssion) {
            noPermssion.printStackTrace();
            //todo handle properly
        }
        return null;
    }

    public class DistanceCallBack implements DistanceMatrixClient.OnResultListener {
        private TextView textView;
        public DistanceCallBack(TextView textView) {
            this.textView = textView;
        }

        @Override
        public void onTraveltimeResult(int timeInSeconds) {
            textView.setText(timeInSeconds + "");
        }
    }

    @Override
    public TripItemAdapter.TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(itemView);
    }

    public void fixSize() {
        System.out.println("BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
        for (TripViewHolder currHolder : holders) {
            System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC");
            currHolder.sizeText.setText(currHolder.contactsHolder.getMeasuredWidth() + "");
        }
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    @Override
    public void onTraveltimeResult(int timeInSeconds) {

    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        protected TextView tripText;
        protected ImageView travelModeIndicator;
        protected TextView travelTime;
        protected FlexboxLayout contactsHolder;
        protected TextView msgsText;
        protected TextView sizeText;

        public TripViewHolder(View v) {
            super(v);
            tripText = (TextView) v.findViewById(R.id.tripText);
            travelModeIndicator = (ImageView) v.findViewById(R.id.image_view_travelMode);
            travelTime = (TextView) v.findViewById(R.id.textView_travelTime);
            contactsHolder = (FlexboxLayout) v.findViewById(R.id.linearLayout_contactsViewHolder);
//            msgsText = (TextView) v.findViewById(R.id.textView_numOfMessages);
            sizeText = (TextView) v.findViewById(R.id.textView_size);
        }
    }
}
