package com.etamessenger.etamessengerproject;

import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
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

        System.out.println("SENDING MESSAGE!!!!");
        System.out.println(trip.getMessages().toString());
        SmsManager smsManager = SmsManager.getDefault();
        for (Contact currContact : trip.getContacts()) {
            smsManager.sendTextMessage("+1" + currContact.getNumber(), null, trip.getMessages().get(0).getMessageText(), null, null);
        }
        trip.getMessages().remove(0);
        System.out.println(trip.getMessages().toString());

        distanceClient = DistanceMatrixClient.getInstance(trip.getContext());
        distanceClient.connect();
        distanceClient.setCallBack(this);
        distanceClient.getTravelTime(trip);
        return false;
    }

    @Override
    public void onTraveltimeResult(int timeInSeconds) {
        int timeTillNextMessage = timeInSeconds - (messages.get(0).getMessageTime() * MINUTES_1);
        if (timeTillNextMessage <= 0) {
            System.out.println("SENDING MESSAGE!!!!");
            System.out.println(trip.getMessages().toString());
            SmsManager smsManager = SmsManager.getDefault();
            for (Contact currContact : trip.getContacts()) {
                smsManager.sendTextMessage("+1" + currContact.getNumber(), null, trip.getMessages().get(0).getMessageText(), null, null);
            }
            trip.getMessages().remove(0);
            System.out.println(trip.getMessages().toString());
        }
        int countDownTime = 0;
            if (timeTillNextMessage > MINUTES_5) countDownTime = (timeTillNextMessage / 3);
            else if (timeTillNextMessage > MINUTES_2) countDownTime = (MINUTES_1);
            else if (timeTillNextMessage > MINUTES_1) countDownTime = (30);
            else countDownTime = (10);

            new CountDownTimer(1000 * countDownTime, 1000) {

                public void onTick(long millisUntilFinished) {
                    trip.getmTextView().setText("next check: " + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    trip.getmTextView().setText("DONE!");
                    distanceClient.getTravelTime(trip);
                }
            }.start();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }


}
