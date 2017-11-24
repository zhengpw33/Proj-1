package com.example.vince.proj.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
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
import java.util.ArrayList;


public class MusicService extends Service {
    private static final String TAG = MusicService.class.getSimpleName();

    public static final String ACTION_UPDATE_PROGRESS = "UPDATE_PROGRESS";
    public static final String ACTION_UPDATE_DURATION = "UPDATE_DURATION";
    public static final String ACTION_UPDATE_CURRENT_MUSIC = "UPDATE_CURRENT_MUSIC";


    private int currentMode = 1; //default all loop
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

    private ArrayList<File> mMusicFilesList = new ArrayList<File>();


    private final IBinder musicBinder = new MusicBinder();

    public MusicService() {
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case updateProgress:
                    toUpdateProgress();
                    break;
                case updateDuration:
                    toUpdateDuration();
                    break;
                case updateCurrentMusic:
                    toUpdateCurrentMusic();
                    break;
            }
        }
    };

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
        mMusicFilesList = (ArrayList<File>)intent.getExtras().get("musicFileList");
        currentIndex = 0;
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
                handler.sendEmptyMessage(updateDuration);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (isPlaying) {
                    switch (currentMode) {
                        case MODE_ONE_LOOP:
                            mediaPlayer.start();
                            break;
                        case MODE_ALL_LOOP:
                            play((currentIndex + 1) % mMusicFilesList.size(), 0);
                            break;
                        case MODE_RANDOM:
                            play(getRandomPosition(), 0);
                            break;
                        default:
                            break;
                    }
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
        currentPosition = pCurrentPosition;
        setCurrentMusic(curIndex);
        mediaPlayer.reset();
        if ((0 <= currentIndex) && (currentIndex < mMusicFilesList.size())) {
            try {
                mediaPlayer.setDataSource(mMusicFilesList.get(currentIndex).getAbsolutePath());
                mediaPlayer.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.sendEmptyMessage(updateProgress);
            isPlaying = true;
        } else {
            Log.e(TAG, "music index out of bounds.");
        }
    }


    private void setCurrentMusic(int pCurrentMusicIndex) {
        currentIndex = pCurrentMusicIndex;
        handler.sendEmptyMessage(updateCurrentMusic);
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

    private void toUpdateProgress(){
        if (mediaPlayer != null && isPlaying) {
            int progress = mediaPlayer.getCurrentPosition();
            //Log.e(TAG,"current: " + progress);
            Intent intent = new Intent();
            intent.setAction(ACTION_UPDATE_PROGRESS);
            intent.putExtra(ACTION_UPDATE_PROGRESS, progress);
            sendBroadcast(intent);
            handler.sendEmptyMessageDelayed(updateProgress, 40);
        }
    }

    private void toUpdateDuration() {
        if (mediaPlayer != null) {
            int duration = mediaPlayer.getDuration();
            //Log.e(TAG,"duration=" + duration);
            Intent intent = new Intent();
            intent.setAction(ACTION_UPDATE_DURATION);
            intent.putExtra(ACTION_UPDATE_DURATION, duration);
            sendBroadcast(intent);
        }
    }

    private void toUpdateCurrentMusic() {
        Intent intent = new Intent();
        intent.setAction(ACTION_UPDATE_CURRENT_MUSIC);
        intent.putExtra(ACTION_UPDATE_CURRENT_MUSIC, currentIndex);
        sendBroadcast(intent);
    }

    class MusicBinder extends Binder {
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
