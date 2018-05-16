package com.cxwl.hurry.doorlock.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.cxwl.hurry.doorlock.R;

/**
 * 播放数字音频工具
 * Created by William on 2018/5/16.
 */

public class SoundPoolUtil {
    private Context mContext;

    private static SoundPoolUtil soundPoolUtil = null;
    private SoundPool soundPool;
    private int streamID;//暂停/恢复播放时需要用到
    private int outgoing;//声音ID，有load方法生成

    public static SoundPoolUtil getSoundPoolUtil() {
        if (soundPoolUtil == null) {
            soundPoolUtil = new SoundPoolUtil();
        }
        return soundPoolUtil;
    }

    /**
     * 播放本地音频资源
     *
     * @param mContext
     * @param num
     */
    public void loadVoice(Context mContext, int num) {
        this.mContext = mContext;

        if (soundPool != null) {
            soundPool.pause(streamID);
            soundPool.stop(streamID);
            soundPool.release();
            soundPool = null;
        }

        //第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
        soundPool = new SoundPool(100, AudioManager.STREAM_RING, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                //声音资源加载完成后播放
                playVoice(sampleId);
            }
        });

        switch (num) {
            case 0:
                //把声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级（这个实际上没用）
                outgoing = soundPool.load(mContext, R.raw.dw0, 1);
                break;
            case 1:
                outgoing = soundPool.load(mContext, R.raw.dw1, 1);
                break;
            case 2:
                outgoing = soundPool.load(mContext, R.raw.dw2, 1);
                break;
            case 3:
                outgoing = soundPool.load(mContext, R.raw.dw3, 1);
                break;
            case 4:
                outgoing = soundPool.load(mContext, R.raw.dw4, 1);
                break;
            case 5:
                outgoing = soundPool.load(mContext, R.raw.dw5, 1);
                break;
            case 6:
                outgoing = soundPool.load(mContext, R.raw.dw6, 1);
                break;
            case 7:
                outgoing = soundPool.load(mContext, R.raw.dw7, 1);
                break;
            case 8:
                outgoing = soundPool.load(mContext, R.raw.dw8, 1);
                break;
            case 9:
                outgoing = soundPool.load(mContext, R.raw.dw9, 1);
                break;
            case 011111:// 门开了语音
                outgoing = soundPool.load(mContext, R.raw.menjinkaimen, 1);
                break;
        }
    }

    public void playVoice(int outgoing) {
        //参数：声音ID，左声道音量大小，右声道音量大小，优先级（值越大优先级越高，0的优先级最低，这个有用），是否需要循环播放（-1表示无穷循环），播放速率
        streamID = soundPool.play(outgoing, 1, 1, 1, 0, 1);
    }
}
