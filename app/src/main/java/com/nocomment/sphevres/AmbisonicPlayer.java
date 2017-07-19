package com.nocomment.sphevres;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.gearvrf.GVRTransform;

public class AmbisonicPlayer {
    private SimpleExoPlayer   player;
    private GvrAudioProcessor gvrAudioProcessor;

    AmbisonicPlayer(Context context) {
        gvrAudioProcessor = new GvrAudioProcessor();

        RenderersFactory renderersFactory = new DefaultRenderersFactory(context) {
            @Override
            public AudioProcessor[] buildAudioProcessors() {
                return new AudioProcessor[] {
                        gvrAudioProcessor
                };
            }
        };
        TrackSelector trackSelector = new DefaultTrackSelector();

        player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector);

        String userAgent = Util.getUserAgent(context, "SpheVRes"); // $$$$

        MediaSource source = new ExtractorMediaSource(
                Uri.parse("asset:///woman_walking.wav"),
                new DefaultDataSourceFactory(context, userAgent),
                new DefaultExtractorsFactory(), null, null
        );

        player.prepare(source);
    }

    void start() {
        player.setPlayWhenReady(true);
    }

    void applyTransform(GVRTransform transform) {
        float headX = transform.getRotationX();
        float headY = transform.getRotationY();
        float headZ = transform.getRotationZ();
        float headW = transform.getRotationW();

        gvrAudioProcessor.updateOrientation(headW, headX, headY, headZ);
    }
}
