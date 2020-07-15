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
    @Override
    public void onBandwidthEstimate(EventTime eventTime, int totalLoadTimeMs, long totalBytesLoaded, long bitrateEstimate) {
        Log.d(TAG, "ANALYTICS BANDWIDTH EVENT:"  + eventTime + "elapsedMs:" + totalLoadTimeMs + "Bytes:" +totalBytesLoaded +"Bitrate:" + bitrateEstimate);
        textView.setText("Bandwidth,Bitrate,Bytes:" + (bitrateEstimate/1024) + "," + bitrateEstimate + "," + totalBytesLoaded);

   }


}
