package com.etamessenger.etamessengerproject;

import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by peter on 07/06/16.
 */

//parameters: message times, and messages, numbers
public class TripTask extends AsyncTask<Trip, Void, Boolean> implements DistanceMatrixClient.OnResultListener{
    private final int MINUTES_5 = 300;
    private final int MINUTES_2 = 120;
    private final int MINUTES_1 = 60;
    private DistanceMatrixClient distanceClient;
    private Trip trip;
    private ArrayList<Message> messages;


    @Override
    protected Boolean doInBackground(Trip... params) {
        trip = params[0];
        messages = trip.getMessages();
        final TextView mTextField = trip.getmTextView();

        distanceClient = DistanceMatrixClient.getInstance(trip.getContext());
        distanceClient.connect();
        distanceClient.setCallBack(this);
        distanceClient.getTravelTime(trip);

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
    public void onTraveltimeResult(int timeInSeconds) {
        int timeTillNextMessage = timeInSeconds - (messages.get(0).getMessageTime() * MINUTES_1);
        int countDownTime = 0;
            if (timeTillNextMessage > MINUTES_5) countDownTime = (timeTillNextMessage / 3) * 2;
            else if (timeTillNextMessage > MINUTES_2) countDownTime = (MINUTES_1);
//            else ()


                if (messages.size() == 0) {
                    //todo end application here
                } else {
                    //todo start next message here
                }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }


}
