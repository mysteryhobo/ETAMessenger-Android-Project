package com.etamessenger.etamessengerproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.AbsSeekBar;
import android.widget.SeekBar;

/**
 * Created by peter on 05/07/16.
 */
public class CustomSeekBar extends SeekBar {
    private Context context;

    private int mThumbSize;
    private TextPaint mTextPaint;

    public CustomSeekBar(Context context) {
        this(context, null);
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.seekBarStyle);
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;


        mThumbSize = getResources().getDimensionPixelSize(R.dimen.thumb_size);

        mTextPaint = new TextPaint();
        mTextPaint.setColor(ContextCompat.getColor(context, R.color.whitemain));
        mTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.thumb_text_size));
        mTextPaint.setTypeface(Typeface.DEFAULT);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int progress = getProgress();
        String progressText;
        if (progress == 0) progressText = context.getResources().getString(R.string.arival);
        else if (progress == getMax()) progressText = context.getResources().getString(R.string.departure);
        else {
            if ((progress / 60) == 1) progressText = context.getResources().getString(R.string.hour_time);
            else if ((progress / 60) > 1) {
                if (progress % 60 == 0) progressText = String.format(context.getResources().getString(R.string.hours_time), progress / 60);
                else progressText = String.format(context.getResources().getString(R.string.hrs_and_min_time), progress / 60, progress % 60); //todo add option for 1 hour and x mins
            } else progressText = String.format(context.getResources().getString(R.string.min_time), progress % 60);
        }

        Rect bounds = new Rect();
        mTextPaint.getTextBounds(progressText, 0, progressText.length(), bounds);
        int leftPadding = getPaddingLeft() - getThumbOffset();
        int rightPadding = getPaddingRight() - getThumbOffset();
        int width = getWidth() - leftPadding - rightPadding;
        float progressRatio = (float) getProgress() / getMax();
        float thumbOffset = mThumbSize * (.5f - progressRatio);
        float thumbX = progressRatio * width + leftPadding + thumbOffset;
        float thumbY = getHeight() / 2f + bounds.height() / 2f;
        canvas.drawText(progressText, thumbX, thumbY, mTextPaint);
    }
}
