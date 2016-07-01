package com.etamessenger.etamessengerproject;

import android.content.Context;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by peter on 08/06/16.
 */
public class Trip implements DBObject{
    private long id;
    String name;
    String latCoord;
    String longCoord;
    LatLng startLocation;
    ArrayList<Message> messages = new ArrayList<>();
    ArrayList<Contact> contacts = new ArrayList<>();
    TextView mTextView = null;
    Context context;
    String travelmode;
    int totalTravelTime;

    public String toString() {
        return name + " "
                + latCoord + " "
                + longCoord + " "
                + messages.toString() + " "
                + contacts.toString();
    }

    public Trip(Context context, String travelmode) {
        this.context = context;
        this.travelmode = travelmode;
    }

    public Trip(String name, String latCoord, String longCoord, ArrayList<Message> messages, ArrayList<Contact> contacts) {
        this.name = name;
        this.latCoord = latCoord;
        this.longCoord = longCoord;
        this.messages = messages;
        this.contacts = contacts;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getLatCoord() {
        return latCoord;
    }

    public void setLatCoord(String latCoord) {
        this.latCoord = latCoord;
    }

    public String getLongCoord() {
        return longCoord;
    }

    public void setLongCoord(String longCoord) {
        this.longCoord = longCoord;
    }


}
