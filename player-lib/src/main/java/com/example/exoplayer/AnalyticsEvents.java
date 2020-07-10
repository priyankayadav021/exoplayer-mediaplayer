package com.example.exoplayer;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.util.Assertions;

public class AnalyticsEvents implements AnalyticsListener {
    private static final String TAG = PlayerActivity.class.getName();
    private final SimpleExoPlayer player;
    private final TextView textView;
    private int counter = 0;

    public AnalyticsEvents(SimpleExoPlayer player, TextView textView) {
        Assertions.checkArgument(player.getApplicationLooper() == Looper.getMainLooper());
        this.player = player;
        this.textView = textView;
    }

    @Override
    public void onVideoSizeChanged(
            EventTime eventTime,
            int width,
            int height,
            int unappliedRotationDegrees,
            float pixelWidthHeightRatio) {
        Log.d(TAG, "ANALYTICS EVENT: " + "videoSize: " + width + ", " + height);
        counter++;
        textView.setText("Counter, Width, Height: " + counter + "," + width + ", " + height);

    }
   public void onBandwidthMeasure(
           EventTime eventTime,
           int estimatetime){
        Log.d(TAG, "Bandwidth:" + eventTime + "estimatetime:" + estimatetime);
        textView.setText("Bandwidth" + eventTime);

   }


}
