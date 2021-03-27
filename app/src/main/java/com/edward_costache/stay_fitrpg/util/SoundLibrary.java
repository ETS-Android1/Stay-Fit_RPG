package com.edward_costache.stay_fitrpg.util;

import android.content.Context;
import android.media.MediaPlayer;


public abstract class SoundLibrary {

    private static MediaPlayer mediaPlayer;
    private static int count = 0;

    public static void playSound(Context context, int resource)
    {
        if(mediaPlayer == null)
        {
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

    public static void stopSound()
    {
        if(mediaPlayer != null)
        {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static void playLoopSound(Context context, int resource, int loopAmount)
    {
        if(mediaPlayer == null)
        {
            mediaPlayer = MediaPlayer.create(context, resource);
            mediaPlayer.setLooping(true);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    count++;
                    if(count > loopAmount)
                    {
                        mediaPlayer.setLooping(false);
                        stopSound();
                    }
                }
            });
        }
        mediaPlayer.start();
    }
}
