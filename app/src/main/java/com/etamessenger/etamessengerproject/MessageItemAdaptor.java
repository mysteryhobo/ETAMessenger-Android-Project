package com.etamessenger.etamessengerproject;

import android.content.Context;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.List;

/**
 * Created by peter on 03/06/16.
 */
public class MessageItemAdaptor extends RecyclerView.Adapter<MessageItemAdaptor.MessageViewHolder> {

    private List<Message> messageList;
    private Context context;

    public MessageItemAdaptor(List<Message> messageList, Context context) {
        this.messageList = messageList;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(MessageItemAdaptor.MessageViewHolder holder, int position) {
        Message currMessage = messageList.get(position);
        holder.messageText.setText(currMessage.getMessageText());
        TextDrawable drawable = TextDrawable.builder()
                .buildRect(Integer.valueOf(currMessage.getMessageTime()).toString(), context.getResources().getColor(R.color.colorPrimary));
        holder.image.setImageDrawable(drawable);
    }

    @Override
    public MessageItemAdaptor.MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        protected TextView messageText;
        protected ImageView image;

        public MessageViewHolder(View v) {
            super(v);
            messageText =  (TextView) v.findViewById(R.id.textView_messageText);
            image = (ImageView) v.findViewById(R.id.image_view);

        }
    }
}
