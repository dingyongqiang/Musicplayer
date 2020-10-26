package cn.itcast.musicplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    private Timer timer;
    public MusicService() {}

    public void onCreate(){
        super.onCreate();
        mediaPlayer = new MediaPlayer();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return new Agent();
    }

    public class Agent extends Binder{
        public  void aPlay(){
            sPlay();
        }

        public  void aPause(){
            sPause();
        }

        public void aContinue(){
            sContinue();
        }

        public void aSeekToPlay(int progress){
            sSeekToPlay(progress);
        }
    }

    private void addTimer(){
            if(timer == null){
                timer = new Timer();
            }
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(mediaPlayer == null) return;
                int duration = mediaPlayer.getDuration();
                int currentPosition = mediaPlayer.getCurrentPosition();
                Message msg = MainActivity.handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt("duration",duration);
                bundle.putInt("currentPosition",currentPosition);
                msg.setData(bundle);
                MainActivity.handler.sendMessage(msg);
            }
        };

            timer.schedule(task,0,1000);
    }

    private void sPlay(){
        mediaPlayer.reset();

        mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.music);
        mediaPlayer.start();

        addTimer();
    }

    private void sPause(){
        mediaPlayer.pause();
    }

    private void sContinue(){
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }


    private void sSeekToPlay(int progress){
            mediaPlayer.seekTo(progress);
    }
    public boolean onUnbind(Intent intent){
        if(mediaPlayer.isPlaying()) mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        Log.i("--MusicService--","unbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
