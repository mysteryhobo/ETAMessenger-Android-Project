package com.etamessenger.etamessengerproject;

import android.widget.TextView;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;

/**
 * Created by peter on 08/06/16.
 */
public class Trip {
    Place destination;
    ArrayList<Message> messages = new ArrayList<>();
    ArrayList<Contact> contacts = new ArrayList<>();
    TextView mTextView = null;

    public Trip(Place destination) {
        this.destination = destination;
    }

    public Trip(Place destination, ArrayList<Message> messages, ArrayList<Contact> contacts) {
        this.destination = destination;
        this.messages = messages;
        this.contacts = contacts;
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
