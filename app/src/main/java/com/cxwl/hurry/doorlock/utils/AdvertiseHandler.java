package com.cxwl.hurry.doorlock.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cxwl.hurry.doorlock.callback.AdverErrorCallBack;
import com.cxwl.hurry.doorlock.callback.AdverTongJiCallBack;
import com.cxwl.hurry.doorlock.db.AdTongJiBean;
import com.cxwl.hurry.doorlock.entity.GuangGaoBean;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cxwl.hurry.doorlock.ui.activity.MainActivity.MSG_ADVERTISE_IMAGE;

/**
 * Created by simon on 2016/7/30.
 */
public class AdvertiseHandler implements SurfaceHolder.Callback {
    SurfaceView videoView = null;
    SurfaceHolder surfaceHolder = null;
    ImageView imageView = null;
    TextView mTextView = null;
//    LinearLayout videoPane=null;
//    LinearLayout imagePane=null;

    private MediaPlayer mediaPlayer;
    private MediaPlayer voicePlayer;
    private String mediaPlayerSource;
    private List<GuangGaoBean> list = new ArrayList<>();
    private List<AdTongJiBean> listCount = new ArrayList<>();
    private int listIndex = 0;
    ImageDisplayThread imageDialpayThread = null;
    private JSONArray imageList = null;
    private int imageListIndex = 0;
    private int imagePeroid = 5000;
    private boolean surfaceViewCreate = false;
    //统计
    private List<AdTongJiBean> mTongJiBeanList;
    private AdTongJiBean mAdTongJiBean;
    private String start_time;
    private String endTime;
    private AdverTongJiCallBack mAdverTongJiCallBack;

    protected Messenger dialMessenger;
    private int position;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x01:
                    Log.i("xiao_", "检测SurfaceView是否被创建");
                    if (surfaceViewCreate) {
                        Log.i("xiao_", "检测到SurfaceView已经被创建");
                        mHandler.removeMessages(0x01);
                        handlerStart((AdverErrorCallBack) msg.obj);
                    } else {
                        Log.i("xiao_", "检测到SurfaceView未被创建，延时200ms");
                        sendHandlerMessage(0x01, msg.obj, 200);
                    }
                    break;
            }
        }
    };

    public AdvertiseHandler() {

    }

    /* public void init(SurfaceView videoView,ImageView imageView,LinearLayout videoPane,
    LinearLayout imagePane){
 //    public void init(SurfaceView videoView,ImageView imageView,LinearLayout videoPane,
 LinearLayout imagePane){
 //        this.videoView=videoView;
 //        this.imageView=imageView;
 //        this.videoPane=videoPane;
 //        this.imagePane=imagePane;
 //        prepareMediaView();
 //    }
     */
    public void init(TextView textView, SurfaceView videoView, ImageView imageView) {
        Log.d("AdvertiseHandler", "UpdateAdvertise: init");
        this.mTextView = textView;
        this.videoView = videoView;
        this.imageView = imageView;
        prepareMediaView();
    }

    public void prepareMediaView() {
        //给SurfaceView添加CallBack监听
        surfaceHolder = videoView.getHolder();
        surfaceHolder.addCallback(this);
        //为了可以播放视频或者使用Camera预览，我们需要指定其Buffer类型
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        // 当Surface尺寸等参数改变时触发
        Log.d("AdvertiseHandler", "UpdateAdvertise: surfaceChanged");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //创建
        Log.i("xiao_", "SurfaceView 创建成功");
        surfaceViewCreate = true;
        //必须在surface创建后才能初始化MediaPlayer,否则不会显示图像
        //startMediaPlay(mediaPlayerSource);
        // 当SurfaceView中的Surface被创建的时候被调用
        //在这里我们指定MediaPlayer在当前的Surface中进行播放setDisplay(holder)
        //在指定了MediaPlayer播放的容器后，我们就可以使用prepare或者prepareAsync来准备播放了player.prepareAsync()
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //销毁
        Log.i("xiao_", "SurfaceView 销毁成功");
        surfaceViewCreate = false;
    }

    public void initData(List<GuangGaoBean> rows, Messenger dialMessenger, boolean isOnVideo,
                         AdverErrorCallBack errorCallBack, AdverTongJiCallBack mCallBack) {
        this.dialMessenger = dialMessenger;
        list = rows;
        listIndex = 0;
        mAdverTongJiCallBack = mCallBack;
        //initScreen();
        initInterger();
        play();
        if (isOnVideo) {
            pause(errorCallBack);
        }

    }

    private void initInterger() {
        for (int i = 0; i < list.size(); i++) {
            AdTongJiBean tongJiBean = new AdTongJiBean();
            listCount.add(tongJiBean);
        }
    }

    public void next() {
        if (listIndex == list.size() - 1) {
            listIndex = 0;
        } else {
            listIndex++;
        }
        Log.e("AdvertiseHandler", list.size() + "----" + listIndex);
        play();
    }

    protected String getCurrentAdType() {
        String adType = "2";
        if (list != null && list.size() > 0) {
            GuangGaoBean item = list.get(listIndex);
            adType = item.getLeixing();
        }

        return adType;
    }

    public void play() {

        GuangGaoBean item = list.get(listIndex);
        String adType = item.getLeixing();
        if (adType.equals("1")) {
            playVideo(item);
        } else if (adType.equals("2")) {
            playImage(item);
        }

    }

    public void playVideo(GuangGaoBean item) {
        try {
            Log.e("广告", item.toString());
            String fileUrls = item.getNeirong();

            String source = HttpUtils.getLocalFileFromUrl(fileUrls);
            if (source != null) {
                start_time = "" + System.currentTimeMillis();
                mTextView.setVisibility(View.VISIBLE);
                videoView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                mediaPlayerSource = source;
                initMediaPlayer();
                startMediaPlay(mediaPlayerSource);
            } else {
                Log.e("广告", "next");
                //  next();
            }
        } catch (Exception e) {
        }
    }

    public void playImage(GuangGaoBean item) {

        String fileUrls = item.getNeirong();

        String source = HttpUtils.getLocalFileFromUrl(fileUrls);
        if (source != null) {
            videoView.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
            startImageDisplay(item);
            initVoicePlayer();
            startVoicePlay(source);
        } else {
            next();
        }

    }

    private void startImageDisplay(final GuangGaoBean item) {
        stopImageDisplay();
        Log.v("AdvertiseHandler", "------>start image display thread<-------" + new Date());
        imageDialpayThread = new ImageDisplayThread() {
            @Override
            public void run() {
                showImage(item);
                while (!isInterrupted() && isWorking) { //检查线程没有被停止
                    try {
                        sleep(imagePeroid); //等待指定的一个并行时间
                    } catch (InterruptedException e) {
                    }
                    if (isWorking) {
                        nextImage(item);
                    }
                }
                Log.v("AdvertiseHandler", "------>end image display thread<-------" + new Date());
                isWorking = false;
                imageDialpayThread = null;
            }
        };
        imageDialpayThread.start();
    }

    public void nextImage(GuangGaoBean item) {
        if (listIndex == list.size() - 1) {
            listIndex = 0;
        } else {
            listIndex++;
        }
        showImage(list.get(listIndex));
        Log.v("AdvertiseHandler", "------>showing image<-------" + new Date());
    }

    public void showImage(GuangGaoBean item) {

        //     JSONObject image = imageList.getJSONObject(imageListIndex);
        String imageFile = item.getNeirong();
        if (dialMessenger != null) {
            sendDialMessenger(MSG_ADVERTISE_IMAGE, imageFile);
        }

    }

    protected void sendDialMessenger(int code, Object object) {
        Message message = Message.obtain();
        message.what = code;
        message.obj = object;
        try {
            dialMessenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void stopImageDisplay() {
        if (imageDialpayThread != null) {
            imageDialpayThread.isWorking = false;
            imageDialpayThread.interrupt();
            imageDialpayThread = null;
        }
    }

    public void initMediaPlayer() {
        //必须在surface创建后才能初始化MediaPlayer,否则不会显示图像
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDisplay(surfaceHolder);
        //设置显示视频显示在SurfaceView上
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i("xiao_", "视频播放完成，继续播放下一个视频文件");
                onMediaPlayerComplete();
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                imageView.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }

    public void initVoicePlayer() {
        if (voicePlayer == null) {
            voicePlayer = new MediaPlayer();
        }
        voicePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onVoicePlayerComplete();
            }
        });
    }

    protected void onMediaPlayerComplete() {
        //mediaPlayer.release();
        endTime = System.currentTimeMillis() + "";
        mTongJiBeanList = new ArrayList<>();
        mAdTongJiBean = new AdTongJiBean();
        mAdTongJiBean.setStart_time(start_time);
        mAdTongJiBean.setEnd_time(endTime);
        mAdTongJiBean.setAdd_id(list.get(listIndex).getId());
        mAdTongJiBean.setMac(MacUtils.getMac());
        mTongJiBeanList.add(mAdTongJiBean);
        mAdverTongJiCallBack.sendTj(mTongJiBeanList);

        next();
    }

    protected void onVoicePlayerComplete() {
        voicePlayer.release();
        stopImageDisplay();
        next();
    }

    public void startMediaPlay(String source) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(source);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            Log.e("AdvertiseHandler", "UpdateAdvertise: startMediaPlay error");
        }
    }

    public void startVoicePlay(String source) {
        try {
            voicePlayer.reset();
            voicePlayer.setDataSource(source);
            voicePlayer.prepare();
            voicePlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        try {
            Log.d("AdvertiseHandler", "UpdateAdvertise: onDestroy");
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            }
            if (voicePlayer != null) {
                if (voicePlayer.isPlaying()) {
                    voicePlayer.stop();
                }
                voicePlayer.release();
            }
        } catch (IllegalStateException e) {
            Log.d("AdvertiseHandler", "UpdateAdvertise: onDestroy error");
        }
        Log.e("AdvertiseHandler", "停止播放");
        videoView.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        voicePlayer = null;
    }

    public void onStop() {
        try {
            if (mediaPlayer == null) {
                return;
            }
            if (mediaPlayer.isPlaying()) {
                position = mediaPlayer.getCurrentPosition();
                mediaPlayer.stop();
            }
        } catch (IllegalStateException e) {
            Log.d("AdvertiseHandler", "UpdateAdvertise: onStop error");
        }
    }

    public void onRestart() {
        if (position > 0) {
            try {
                play();
            } catch (IllegalStateException e) {
                Log.d("AdvertiseHandler", "UpdateAdvertise: onRestart error");
            }
            //mediaPlayer.seekTo(position);
            position = 0;
            Log.d("AdvertiseHandler", "UpdateAdvertise: onRestart done");
        }
    }

    public void start(AdverErrorCallBack errorCallBack) {
        sendHandlerMessage(0x01, errorCallBack, 0);
    }

    private void sendHandlerMessage(int what, Object msg, int delay) {
        Message message = new Message();
        message.what = what;
        if (msg != null) {
            message.obj = msg;
        }
        if (delay > 0) {
            mHandler.sendMessageDelayed(message, delay);
        } else {
            mHandler.sendMessage(message);
        }

    }

    private void handlerStart(AdverErrorCallBack errorCallBack) {
        try {
            Log.i("xiao_", "handlerStart->");
            if (mediaPlayer != null) {
                if (!mediaPlayer.isPlaying()) {
                    Log.i("xiao_", "handlerStart->setDisplay");
                    mediaPlayer.setDisplay(surfaceHolder);
                    Log.i("xiao_", "handlerStart->start");
                    int vis = videoView.getVisibility();
                    Log.i("xiao_", "SurfaceView is show = " + vis);
                    mediaPlayer.start();
                }
            } else {
                Log.i("xiao_", "mediaPlayer = null");
            }
            if (getCurrentAdType().equals("V")) {
            } else if (getCurrentAdType().equals("I")) {
                voicePlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("AdvertiseHandler", "UpdateAdvertise: start error");
            errorCallBack.ErrorAdver();
        }
    }

    public void pause(AdverErrorCallBack errorCallBack) {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }
            if (getCurrentAdType().equals("V")) {

            } else if (getCurrentAdType().equals("I")) {
                voicePlayer.pause();
            }
        } catch (IllegalStateException e) {
            errorCallBack.ErrorAdver();
        }
    }
}

class ImageDisplayThread extends Thread {
    public boolean isWorking = true;
}