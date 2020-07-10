/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.exoplayer;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;


/**
 * A fullscreen activity to play audio or video streams.
 */
public class PlayerActivity extends AppCompatActivity {

  private PlayerView playerView;
  private SimpleExoPlayer player;
  private boolean playWhenReady = true;
  private int currentWindow = 0;
  private long playbackPosition = 0;
  private PlaybackStateListener playbackStateListener;
  private static final String TAG = PlayerActivity.class.getName();
  private DataSource.Factory dataSourceFactory;
  private TextView titleTextView;
  private TextView debugTextView;
  private AnalyticsTextViewHelper analyticsViewHelper;
  private DefaultTrackSelector.Parameters trackSelectorParameters;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_player);
    playbackStateListener = new PlaybackStateListener();
    playerView = findViewById(R.id.video_view);

    titleTextView = findViewById(R.id.title_text_view);
    debugTextView = findViewById(R.id.debug_text_view);

  }


  @Override
  protected void onResume() {
    super.onResume();
    hideSystemUi();
    if ((Util.SDK_INT < 24 || player == null)) {
      initializePlayer();
    }
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (Util.SDK_INT < 24) {
      releasePlayer();
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (Util.SDK_INT >= 24) {
      releasePlayer();
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (Util.SDK_INT >= 24) {
      initializePlayer();
    }
  }

  private void initializePlayer() {
    if (player == null) {
      TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory();
      DefaultTrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);
      trackSelectorParameters = trackSelector.getParameters();
      trackSelector.setParameters(trackSelectorParameters);
      player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

      playerView.setPlayer(player);
      Uri uri = Uri.parse(getString(R.string.media_url_dash));
      //Uri uri1 = Uri.parse(getString(R.string.media_url_hls));
      MediaSource mediaSource = buildMediaSourceDash(uri);
      //MediaSource mediaSourcehls = buildMediaSourceHls(uri1);
      player.setPlayWhenReady(playWhenReady);
      player.seekTo(currentWindow, playbackPosition);
      player.addListener(playbackStateListener);
      player.addAnalyticsListener(new EventLogger(trackSelector));
      player.addAnalyticsListener(new AnalyticsEvents(player, titleTextView));
      //debugViewHelper = new DebugTextViewHelper(player, debugTextView);
      analyticsViewHelper = new AnalyticsTextViewHelper(player, debugTextView);
      analyticsViewHelper.start();
      player.prepare(mediaSource, false, false);
      //player.prepare(mediaSourcehls, false, false);
    }

  }

  private MediaSource buildMediaSourceDash(Uri uri) {
    DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, "exoplayer-codelab");
    DashMediaSource.Factory mediaSourceFactory = new DashMediaSource.Factory(dataSourceFactory);
    // MediaSource mediaSource1 = mediaSourceFactory.createMediaSource(uri);
    // Uri audioUri = Uri.parse(getString(R.string.media_url_mp3));
    // MediaSource mediaSource = mediaSoureFactory.createMediaSource(audioUri));
    // return new ConcatenatingMediaSource(mediaSource1.mediaSource2);
    return mediaSourceFactory.createMediaSource(uri);
  }
    //private MediaSource buildMediaSourceHls(Uri uri1) {
       // DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, "exoplayer-codelab");
        //HlsMediaSource.Factory mediaSourceFactory = new HlsMediaSource.Factory(dataSourceFactory);
       // return mediaSourceFactory.createMediaSource(uri1);
   // }


  private class PlaybackStateListener implements Player.EventListener {
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
      String stateString;
      switch (playbackState) {
        case ExoPlayer.STATE_IDLE:
          stateString = "ExoPlayer.STATE_IDLE      -";
          break;
        case ExoPlayer.STATE_BUFFERING:
          stateString = "ExoPlayer.STATE_BUFFERING -";
          break;
        case ExoPlayer.STATE_READY:
          stateString = "ExoPlayer.STATE_READY     -";
          break;
        case ExoPlayer.STATE_ENDED:
          stateString = "ExoPlayer.STATE_ENDED     -";
          break;
        default:
          stateString = "UNKNOWN_STATE             -";
          break;
      }
      Log.d(TAG, "changed state to " + stateString + " playWhenReady: " + playWhenReady);
    }
  }

  private void releasePlayer() {
    if (player != null) {
      playWhenReady = player.getPlayWhenReady();
      playbackPosition = player.getCurrentPosition();
      currentWindow = player.getCurrentWindowIndex();
      analyticsViewHelper.stop();
      player.removeListener(playbackStateListener);
      player.release();
      player = null;
    }

  }


  @SuppressLint("InlinedApi")
  private void hideSystemUi() {
    playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
  }

}