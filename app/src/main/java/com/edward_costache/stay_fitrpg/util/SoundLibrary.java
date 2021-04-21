package com.edward_costache.stay_fitrpg.util;

import android.content.Context;
import android.media.MediaPlayer;

public abstract class SoundLibrary {

    private static MediaPlayer mediaPlayer;

    public static void playSound(Context context, int resource) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, resource);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    stopSound();
                }
            });
        }
        mediaPlayer.start();
    }

    public static void stopSound() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static void playLoopSound(Context context, int resource, int loopAmount) {
        if (mediaPlayer == null) {
            final int[] count = {0};
            mediaPlayer = MediaPlayer.create(context, resource);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    count[0]++;
                    if (count[0] < loopAmount) {
                        playSound(context, resource);
                    } else {
                        stopSound();
                    }
                }
            });
        }
        mediaPlayer.start();
    }
}
