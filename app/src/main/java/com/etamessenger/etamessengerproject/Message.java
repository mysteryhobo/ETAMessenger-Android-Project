package com.etamessenger.etamessengerproject;

/**
 * Created by peter on 03/06/16.
 */
public class Message {
    private String messageText;
    private int messageTime;

    public Message(String messageText, int messageTime) {
        this.messageText = messageText;
        this.messageTime = messageTime;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public int getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(int messageTime) {
        this.messageTime = messageTime;
    }
}
