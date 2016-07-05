package com.etamessenger.etamessengerproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by peter on 03/06/16.
 */
public class MessageItemAdaptor extends RecyclerView.Adapter<MessageItemAdaptor.MessageViewHolder> {

    private List<Message> messageList;
    private Context context;
    private View.OnClickListener lsnr;
    private Activity activity;
    private int totalTravelTime;

    public MessageItemAdaptor(List<Message> messageList, Context context, Activity activity, int totalTraveltime) {
        this.messageList = messageList;
        this.context = context;
        this.activity = activity;
        this.totalTravelTime = totalTraveltime;
    }

    @Override
    public void onBindViewHolder(MessageItemAdaptor.MessageViewHolder holder, int position) {
        Message currMessage = messageList.get(position);
        holder.messageText.setText(currMessage.getMessageText());
        String messageTimeText;
        int time = currMessage.getMessageTime();

        if (time == 0) messageTimeText = context.getResources().getString(R.string.arival);
        else {
            if ((time / 60) == 1) messageTimeText = context.getResources().getString(R.string.hour_time);
            else if ((time / 60) > 1) {
                if (time % 60 == 0) messageTimeText = String.format(context.getResources().getString(R.string.hours_time), time / 60);
                else messageTimeText = String.format(context.getResources().getString(R.string.hour_and_min_time), time / 60, time % 60);
            } else messageTimeText = String.format(context.getResources().getString(R.string.min_time), time % 60);
        }
        holder.messageTime.setText(messageTimeText);
        holder.messageTime.bringToFront();
        holder.messageTime.setOnClickListener(new View.OnClickListener() {
            Activity activity;
            Message currMessage;

            private View.OnClickListener init (Activity activity, Message currMessage) {
                this.activity = activity;
                this.currMessage = currMessage;
                return this;
            }

            @Override
            public void onClick(final View v) {
                Log.i("BALLLz", "onClick: AHHHHH I GOT CLICKED");
                SeekBar seekBar = (SeekBar) activity.findViewById(R.id.seekBar_messageTime);
                seekBar.setMax(totalTravelTime / 60);
                seekBar.setProgress(currMessage.getMessageTime());
                seekBar.setVisibility(View.VISIBLE);

                seekBar.setProgressDrawable(ContextCompat.getDrawable(context.getApplicationContext(),R.drawable.seekbar_progress));

//                seekBar.setThumb(ContextCompat.getDrawable(context.getApplicationContext(),R.drawable.ic_bike_primary));
//                seekBar.setThumb(ContextCompat.getDrawable(context.getApplicationContext(),R.layout.item_contact));

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    int currProgress;
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        ((TextView) v).setText(Integer.toString(progress));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                Log.i("Message adapter", "onClick: " + totalTravelTime + " || " + currMessage.getMessageTime());
            }
        }.init(activity, currMessage));

//        TextDrawable drawable = TextDrawable.builder()
//                .buildRect(messageTimeText, context.getResources().getColor(R.color.colorPrimary));
//        holder.image.setImageDrawable(drawable);
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
        protected TextView messageTime;

        public MessageViewHolder(View v) {
            super(v);
            messageText =  (TextView) v.findViewById(R.id.textView_messageText);
            messageTime = (TextView) v.findViewById(R.id.textView_messageTime);

        }
    }
}
