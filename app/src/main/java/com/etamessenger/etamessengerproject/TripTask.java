package com.etamessenger.etamessengerproject;

import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.widget.TextView;

import java.net.URL;

/**
 * Created by peter on 07/06/16.
 */

//parameters: message times, and messages, numbers
public class TripTask extends AsyncTask<Trip, Void, Boolean> implements DistanceMatrixClient.OnResultListener {
    private DistanceMatrixClient distanceClient;


    @Override
    protected Boolean doInBackground(Trip... params) {
        //        distanceClient = DistanceMatrixClient.getInstance();
//        distanceClient.setCallBack(this);
//        distanceClient.getTravelTime(getCurrentLocation(), destination, context, travelMode);
        Trip trip = params[0];
        final TextView mTextField = trip.getmTextView();
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                mTextField.setText("next check: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                mTextField.setText("done!");
            }
        }.start();
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }

    @Override
    public void onTraveltimeResult(int timeInSeconds) {

    }


}
