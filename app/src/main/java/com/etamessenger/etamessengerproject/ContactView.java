package com.etamessenger.etamessengerproject;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import org.w3c.dom.Text;

/**
 * Created by peter on 02/07/16.
 */
public class ContactView extends LinearLayout {
    String contactName;
    TextView nameTextView;
    ImageView pic;

    public ContactView(Context context, String contactName) {
        super(context);
        this.contactName = contactName;
        initializeViews(context);
    }

    public ContactView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public ContactView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeViews(context);

    }

    public void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_contact_view, this);
        nameTextView = (TextView) this.findViewById(R.id.textView_contactName);
        nameTextView.setText(contactName);

//        TextDrawable drawable = TextDrawable.builder()
//                .buildRoundRect(contactName.substring(0,1).toUpperCase() + contactName.substring(1), context.getResources().getColor(R.color.colorPrimary), 20);
//        ((ImageView) this.findViewById(R.id.imageView_contact)).setImageDrawable(drawable);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }
}

