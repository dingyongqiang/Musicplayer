package cn.itcast.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView mIvMusic;
    private static SeekBar mSb;
    private static TextView mTvProgress;
    private static TextView mTvTotal;
    private Button mBtnPlay;
    private Button mBtnPause;
    private Button mBtnContinuePlay;
    private Button mBtnExit;
    private MyConn myConn;
    private MusicService.Agent mAgent;
    private ObjectAnimator animator;

    public static Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            int duration = bundle.getInt("duration");
            int currentPosition = bundle.getInt("currentPosition");

            mSb.setMax(duration);
            mSb.setProgress(currentPosition);

            int minute = duration/1000/60;
            int second = duration/1000%60;
            String resMinute;
            String resSecond;
            if(minute<10){
                resMinute = "0" + minute;
            }else {
                resMinute = minute + "";
            }
            if(second<10){
                resSecond = "0" + second;
            }else {
                resSecond = second + "";
            }

            mTvTotal.setText(resMinute+":" +resSecond);

            minute = currentPosition/1000/60;
            second = currentPosition/1000%60;
            if(minute<10){
                resMinute = "0" + minute;
            }else {
                resMinute = minute + "";
            }
            if(second<10){
                resSecond = "0" + second;
            }else {
                resSecond = second + "";
            }
            mTvProgress.setText(resMinute+":" +resSecond);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        Intent intent = new Intent(this, MusicService.class);
        if(myConn == null){
            myConn = new MyConn();
        }
        bindService(intent,myConn,BIND_AUTO_CREATE);

        mSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress == seekBar.getMax()){
                    animator.pause();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();

                mAgent.aSeekToPlay(progress);
            }
        });
    }

    class MyConn implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            mAgent = (MusicService.Agent)iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {}
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_play:
                //播放
                mAgent.aPlay();
                animator.start();
                break;
            case R.id.btn_pause:
                //暂停
                mAgent.aPause();
                animator.pause();
                break;
            case R.id.btn_continue_play:
                //继续
                mAgent.aContinue();
                animator.start();
                break;
            case R.id.btn_exit:
                //退出
                if(myConn !=null){
                    unbindService(myConn);
                    myConn = null;
                }
                finish();
                break;
        }

    }
    private void initView() {
        mIvMusic = (ImageView) findViewById(R.id.iv_music);
        mSb = (SeekBar) findViewById(R.id.sb);
        mTvProgress =(TextView) findViewById(R.id.tv_progress);
        mTvTotal = (TextView) findViewById(R.id.tv_total);
        mBtnPlay = (Button) findViewById(R.id.btn_play);
        mBtnPause = (Button) findViewById(R.id.btn_pause);
        mBtnContinuePlay = (Button)  findViewById(R.id.btn_continue_play);
        mBtnExit =(Button) findViewById(R.id.btn_exit);

        animator = ObjectAnimator.ofFloat(mIvMusic,"rotation",0.0f,360.0f);

        animator.setDuration(10*1000);

        animator.setRepeatCount(-1);
        animator.setInterpolator(new LinearInterpolator());
    }

    private void initListener() {
        mBtnPlay.setOnClickListener(this);
        mBtnPause.setOnClickListener(this);
        mBtnContinuePlay.setOnClickListener(this);
        mBtnExit.setOnClickListener(this);
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(myConn !=null){
            unbindService(myConn);
            myConn = null;
        }

    }


}