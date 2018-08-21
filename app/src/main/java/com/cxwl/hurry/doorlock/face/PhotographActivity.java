package com.cxwl.hurry.doorlock.face;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.cxwl.hurry.doorlock.R;
import com.cxwl.hurry.doorlock.config.Constant;
import com.guo.android_extend.widget.CameraFrameData;
import com.guo.android_extend.widget.CameraGLSurfaceView;
import com.guo.android_extend.widget.CameraSurfaceView;

/**
 * Created by William on 2018/7/30.
 */

public class PhotographActivity extends AppCompatActivity implements CameraSurfaceView.OnCameraListener, View
        .OnClickListener {

    private CameraSurfaceView mSurfaceView;
    private CameraGLSurfaceView mGLSurfaceView;
    private ImageView mCaptureButton;
    private Camera mCamera;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photograph2);

        mGLSurfaceView = (CameraGLSurfaceView) findViewById(R.id.glsurfaceView);
        mSurfaceView = (CameraSurfaceView) findViewById(R.id.surfaceView);
        mSurfaceView.setOnCameraListener(this);
        mSurfaceView.setupGLSurafceView(mGLSurfaceView, true, true, 0);
        mSurfaceView.debug_print_fps(true, false);

        mCaptureButton = (ImageView) findViewById(R.id.capture);//拍照按钮
        mCaptureButton.setOnClickListener(this);

        Constant.RESTART_AUDIO = false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
            }
        }).start();
    }

    @Override
    public Camera setupCamera() {
        mCamera = Camera.open();//打开硬件摄像头，这里导包得时候一定要注意是android.hardware.Camera

        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(640, 480);
            parameters.setPreviewFormat(ImageFormat.NV21);
            mCamera.setParameters(parameters);
            mCamera.autoFocus(null);
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("人脸识别", "setupCamera-->" + e.getMessage());
        }
        return mCamera;
    }

    @Override
    public void setupChanged(int format, int width, int height) {

    }

    @Override
    public boolean startPreviewLater() {
        return false;
    }

    @Override
    public Object onPreview(byte[] data, int width, int height, int format, long timestamp) {
        return null;
    }

    @Override
    public void onBeforeRender(CameraFrameData data) {

    }

    @Override
    public void onAfterRender(CameraFrameData data) {

    }

    @Override
    public void onClick(View view) {

    }
}
