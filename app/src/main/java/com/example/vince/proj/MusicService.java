package com.example.vince.proj;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MusicService extends Service {
    private static final String TAG = MusicService.class.getSimpleName();

    public static final String action1 = "action1";
    public static final String action2 = "action2";
    public int lastposition=0;


    private int currentMode = 0; //default all loop
    public static final int MODE_ONE_LOOP = 0;
    public static final int MODE_ALL_LOOP = 1;
    public static final int MODE_RANDOM = 2;


    private static final int updateProgress = 1;
    private static final int updateCurrentMusic = 2;
    private static final int updateDuration = 3;


    private MediaPlayer mediaPlayer;
    private int currentIndex = 0;
    private int currentPosition = 0;
    private boolean isPlaying = false;

    private ArrayList<AssetFileDescriptor> mMusicFilesList = new ArrayList<AssetFileDescriptor>();


    private final IBinder musicBinder = new MusicBinder();

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return musicBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "service onUnbind.");
        super.onUnbind(intent);
        return true;
    }
    @Override
    public void onRebind(Intent intent) {
        Log.e(TAG, "service onRebind.");
        super.onRebind(intent);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate.");

        initMediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mMusicFilesList.clear();
        mMusicFilesList = new ArrayList<AssetFileDescriptor>();
        try{
            mMusicFilesList.add(getAssets().openFd("back1.mp3"));
            mMusicFilesList.add(getAssets().openFd("well_played1.mp3"));
            mMusicFilesList.add(getAssets().openFd("well_played2.mp3"));
            mMusicFilesList.add(getAssets().openFd("well_played3.mp3"));
            mMusicFilesList.add(getAssets().openFd("threaten.mp3"));
        }catch (IOException e){
            e.printStackTrace();
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
        Log.e(TAG, "onDestroy.");
    }



    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.seekTo(currentPosition);
                mediaPlayer.start();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (isPlaying) {
                    if(currentIndex!=0)
                        play(0, lastposition);
                    else
                        play(0,0);
                }
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
    }

    private void play(int curIndex, int pCurrentPosition){
        if(curIndex!=0) lastposition = mediaPlayer.getCurrentPosition();
        currentPosition = pCurrentPosition;

        mediaPlayer.reset();
        if(curIndex==1){
            curIndex += (int)(Math.random()*10)%3;
        }
        setCurrentMusic(curIndex);
        if ((0 <= currentIndex) && (currentIndex < mMusicFilesList.size())) {
            try {
                AssetFileDescriptor fd = mMusicFilesList.get(currentIndex);
                mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
                mediaPlayer.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }
            isPlaying = true;
        } else {
            Log.e(TAG, "music index out of bounds.");
        }
    }


    private void setCurrentMusic(int pCurrentMusicIndex) {
        currentIndex = pCurrentMusicIndex;
    }

    private void stop() {
        mediaPlayer.stop();
        isPlaying = false;
    }

    private void playNext() {
        switch (currentMode) {
            case MODE_ONE_LOOP:
                play(currentIndex, 0);
                break;
            case MODE_ALL_LOOP:
                if (currentIndex + 1 == mMusicFilesList.size()) {
                    currentIndex = 0;
                    play(currentIndex, 0);
                } else {
                    currentIndex += 1;
                    play(currentIndex, 0);
                }
                break;
            case MODE_RANDOM:
                play(getRandomPosition(), 0);
                break;
        }
    }

    private void playPrevious() {
        switch (currentMode) {
            case MODE_ONE_LOOP:
                play(currentIndex, 0);
                break;
            case MODE_ALL_LOOP:
                if (currentIndex - 1 < 0) {
                    currentIndex = mMusicFilesList.size() - 1;
                    play(currentIndex, 0);
                } else {
                    currentIndex -= 1;
                    play(currentIndex, 0);
                }
                break;
            case MODE_RANDOM:
                play(getRandomPosition(), 0);
                break;
        }
    }

    private int getRandomPosition() {
        int random = (int) (Math.random() * mMusicFilesList.size());
        return random;
    }


    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }

        //index是目标歌曲在musicFileList中的索引
        public void startPlay(int index, int currentPosition) {
            play(index, currentPosition);
        }

        public void stopPlay() {
            stop();
        }

        public void toNext() {
            playNext();
        }

        public void toPrevious() {
            playPrevious();
        }


        public void setMode(int mode) {
            currentMode = mode;
        }


        public int getCurrentMode() {
            return currentMode;
        }

        /**
         * The service is playing the music
         *
         * @return
         */
        public boolean isPlaying() {
            return isPlaying;
        }


        /**
         * Seekbar changes
         *
         * @param progress
         */
        public void changeProgress(int progress) {
            if (mediaPlayer != null) {
                //Log.e(TAG, "changeProgress.");
                currentPosition = progress * 1000;
                mediaPlayer.seekTo(currentPosition);
            }
        }
    }
}
