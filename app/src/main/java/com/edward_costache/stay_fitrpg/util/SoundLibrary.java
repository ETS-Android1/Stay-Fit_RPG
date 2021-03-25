package com.edward_costache.stay_fitrpg.util;

import android.content.Context;
import android.media.MediaPlayer;

import com.edward_costache.stay_fitrpg.R;

public abstract class SoundLibrary {

    private static MediaPlayer mediaPlayer;

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
}
