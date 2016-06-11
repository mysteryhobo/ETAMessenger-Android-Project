package com.etamessenger.etamessengerproject;

import android.content.Context;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by peter on 08/06/16.
 */
public class Trip {
    Place destination;
    LatLng startLocation;
    ArrayList<Message> messages = new ArrayList<>();
    ArrayList<Contact> contacts = new ArrayList<>();
    TextView mTextView = null;
    Context context;
    String travelmode;
    int totalTravelTime;

    public Trip(Context context, String travelmode) {
        this.context = context;
        this.travelmode = travelmode;
    }

    public LatLng getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(LatLng startLocation) {
        this.startLocation = startLocation;
    }

    public int getTotalTravelTime() {
        return totalTravelTime;
    }

    public void setTotalTravelTime(int totalTravelTime) {
        this.totalTravelTime = totalTravelTime;
    }

    public String getTravelmode() {
        return travelmode;
    }

    public void setTravelmode(String travelmode) {
        this.travelmode = travelmode;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public TextView getmTextView() {
        return mTextView;
    }

    public void setmTextView(TextView mTextView) {
        this.mTextView = mTextView;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public void addContact(Contact contact) {
        contacts.add(contact);
    }

    public Place getDestination() {
        return destination;
    }

    public void setDestination(Place destination) {
        this.destination = destination;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }
}
