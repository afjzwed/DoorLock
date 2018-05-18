package com.cxwl.hurry.doorlock.face;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facedetection.AFD_FSDKVersion;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.cxwl.hurry.doorlock.R;
import com.cxwl.hurry.doorlock.config.DeviceConfig;
import com.cxwl.hurry.doorlock.utils.BitmapUtils;
import com.guo.android_extend.image.ImageConverter;
import com.guo.android_extend.widget.ExtImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.cxwl.hurry.doorlock.config.Constant.arc_appid;
import static com.cxwl.hurry.doorlock.config.Constant.fd_key;
import static com.cxwl.hurry.doorlock.config.Constant.fr_key;

/**
 * Created by William on 2018/5/17.
 */

public class FaceRegisterActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private final String TAG = this.getClass().toString();
    private final static int MSG_CODE = 0x1000;
    private final static int MSG_EVENT_REG = 0x1001;
    private final static int MSG_EVENT_NO_FACE = 0x1002;
    private final static int MSG_EVENT_NO_FEATURE = 0x1003;
    private final static int MSG_EVENT_FD_ERROR = 0x1004;
    private final static int MSG_EVENT_FR_ERROR = 0x1005;
    private UIHandler mUIHandler;
    // Intent data.
    private String mFilePath;

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Bitmap mBitmap;
    private Rect src = new Rect();
    private Rect dst = new Rect();
    private Thread view;
    //    private HListView mHListView;
//    private RegisterViewAdapter mRegisterViewAdapter;
    private AFR_FSDKFace mAFR_FSDKFace;
    private String houseNumber, phoneNumber;
    private Bitmap faceBitmap;
    private boolean focus = true;

    private HandlerThread handlerThread;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_face_register);
        //initial data.
        if (!getIntentData(getIntent().getExtras())) {
            Log.e(TAG, "getIntentData fail!");
            this.finish();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setLogo(R.mipmap.ic_launcher);
//            actionBar.setLogo(R.mipmap.ic_launcher);
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
//            actionBar.setDisplayHomeAsUpEnabled(false);
        }

//        mRegisterViewAdapter = new RegisterViewAdapter(this);
//        mHListView = (HListView) findViewById(R.id.hlistView);
//        mHListView.setAdapter(mRegisterViewAdapter);
//        mHListView.setOnItemClickListener(mRegisterViewAdapter);

        ///storage/sdcard/Pictures/arcsoft_20180322094710.jpg
        ///data/data/com.tencent.devicedemo/files/photo.bmp
        Log.v("人脸识别", "onCreate-->" + mFilePath);
//        mFilePath = "/data/data/com.tencent.devicedemo/files/photo.bmp";

        mUIHandler = new UIHandler();
        mBitmap = BitmapUtils.decodeImage(mFilePath);
        src.set(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        mSurfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
        mSurfaceView.getHolder().addCallback(this);
        view = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mSurfaceHolder == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight() * 3 / 2];
                ImageConverter convert = new ImageConverter();
                convert.initial(mBitmap.getWidth(), mBitmap.getHeight(), ImageConverter
                        .CP_PAF_NV21);
                if (convert.convert(mBitmap, data)) {
                    Log.d(TAG, "convert ok!");
                }
                convert.destroy();

                AFD_FSDKEngine engine = new AFD_FSDKEngine();
                AFD_FSDKVersion version = new AFD_FSDKVersion();
                List<AFD_FSDKFace> result = new ArrayList<AFD_FSDKFace>();
                AFD_FSDKError err = engine.AFD_FSDK_InitialFaceEngine(arc_appid, fd_key, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 5);
                Log.d(TAG, "AFD_FSDK_InitialFaceEngine = " + err.getCode());
                if (err.getCode() != AFD_FSDKError.MOK) {//FD初始化失败
                    Message reg = Message.obtain();
                    reg.what = MSG_CODE;
                    reg.arg1 = MSG_EVENT_FD_ERROR;
                    reg.arg2 = err.getCode();
                    mUIHandler.sendMessage(reg);
                }
                err = engine.AFD_FSDK_GetVersion(version);
                Log.d(TAG, "AFD_FSDK_GetVersion =" + version.toString() + ", " + err.getCode());
                err = engine.AFD_FSDK_StillImageFaceDetection(data, mBitmap.getWidth(), mBitmap
                        .getHeight(), AFD_FSDKEngine.CP_PAF_NV21, result);
                Log.d(TAG, "AFD_FSDK_StillImageFaceDetection =" + err.getCode() + "<" + result
                        .size());
                while (mSurfaceHolder != null) {
                    Canvas canvas = mSurfaceHolder.lockCanvas();
                    if (canvas != null) {
                        Paint mPaint = new Paint();
                        boolean fit_horizontal = canvas.getWidth() / (float) src.width() < canvas
                                .getHeight() / (float) src.height() ? true : false;
                        float scale = 1.0f;
                        if (fit_horizontal) {
                            scale = canvas.getWidth() / (float) src.width();
                            dst.left = 0;
                            dst.top = (canvas.getHeight() - (int) (src.height() * scale)) / 2;
                            dst.right = dst.left + canvas.getWidth();
                            dst.bottom = dst.top + (int) (src.height() * scale);
                        } else {
                            scale = canvas.getHeight() / (float) src.height();
                            dst.left = (canvas.getWidth() - (int) (src.width() * scale)) / 2;
                            dst.top = 0;
                            dst.right = dst.left + (int) (src.width() * scale);
                            dst.bottom = dst.top + canvas.getHeight();
                        }
                        canvas.drawBitmap(mBitmap, src, dst, mPaint);
                        canvas.save();
                        canvas.scale((float) dst.width() / (float) src.width(), (float) dst
                                .height() / (float) src.height());
                        canvas.translate(dst.left / scale, dst.top / scale);
                        for (AFD_FSDKFace face : result) {
                            mPaint.setColor(Color.RED);
                            mPaint.setStrokeWidth(1.0f);
                            mPaint.setStyle(Paint.Style.STROKE);
                            canvas.drawRect(face.getRect(), mPaint);
                        }
                        canvas.restore();
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                        break;
                    }
                }

                if (!result.isEmpty()) {
                    AFR_FSDKVersion version1 = new AFR_FSDKVersion();
                    AFR_FSDKEngine engine1 = new AFR_FSDKEngine();
                    AFR_FSDKFace result1 = new AFR_FSDKFace();
                    AFR_FSDKError error1 = engine1.AFR_FSDK_InitialEngine(arc_appid,fr_key);
                    Log.d("com.arcsoft", "AFR_FSDK_InitialEngine = " + error1.getCode());
                    if (error1.getCode() != AFD_FSDKError.MOK) {
                        Message reg = Message.obtain();
                        reg.what = MSG_CODE;
                        reg.arg1 = MSG_EVENT_FR_ERROR;
                        reg.arg2 = error1.getCode();
                        mUIHandler.sendMessage(reg);
                    }
                    error1 = engine1.AFR_FSDK_GetVersion(version1);
                    Log.d("com.arcsoft", "FR=" + version.toString() + "," + error1.getCode()); //
                    // (210, 178 - 478, 446), degree = 1　780, 2208 - 1942, 3370
                    ////检测输入图像中的人脸特征信息，输出结果保存在 AFR_FSDKFace feature
                    error1 = engine1.AFR_FSDK_ExtractFRFeature(data, mBitmap.getWidth(), mBitmap
                            .getHeight(), AFR_FSDKEngine.CP_PAF_NV21, new Rect(result.get(0)
                            .getRect()), result.get(0).getDegree(), result1);
                    Log.d("com.arcsoft", "Face=" + result1.getFeatureData()[0] + "," +
                            result1.getFeatureData()[1] + "," + result1.getFeatureData()[2] + ","
                            + error1.getCode());
                    if (error1.getCode() == error1.MOK) {
                        mAFR_FSDKFace = result1.clone();
                        int width = result.get(0).getRect().width();
                        int height = result.get(0).getRect().height();
                        Bitmap face_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config
                                .RGB_565);
                        Canvas face_canvas = new Canvas(face_bitmap);
                        face_canvas.drawBitmap(mBitmap, result.get(0).getRect(), new Rect(0, 0,
                                width, height), null);
                        Message reg = Message.obtain();
                        reg.what = MSG_CODE;
                        reg.arg1 = MSG_EVENT_REG;
                        reg.obj = face_bitmap;
                        mUIHandler.sendMessage(reg);
                    } else {
                        Message reg = Message.obtain();
                        reg.what = MSG_CODE;
                        reg.arg1 = MSG_EVENT_NO_FEATURE;
                        mUIHandler.sendMessage(reg);
                    }
                    error1 = engine1.AFR_FSDK_UninitialEngine();
                    Log.d("com.arcsoft", "AFR_FSDK_UninitialEngine : " + error1.getCode());
                } else {
                    Message reg = Message.obtain();
                    reg.what = MSG_CODE;
                    reg.arg1 = MSG_EVENT_NO_FACE;
                    mUIHandler.sendMessage(reg);
                }
                err = engine.AFD_FSDK_UninitialFaceEngine();
                Log.d(TAG, "AFD_FSDK_UninitialFaceEngine =" + err.getCode());
            }
        });
        view.start();

        //创建一个线程,线程名字：pictureHandlerThread
        handlerThread = new HandlerThread("registerHandlerThread");
        //开启一个线程
        handlerThread.start();

        //在这个线程中创建一个handler对象
        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.v("人脸识别", "handleMessage1-->" + msg.what + "/" + Thread.currentThread()
                        .getName());
//                boolean bool1 = ArcsoftManager.getInstance().mFaceDB.saveBitmap(phoneNumber,
// faceBitmap);
                // TODO: 2018/4/21 此处为人脸识别信息存入本地人脸数据库
                boolean bool2 = ArcsoftManager.getInstance().mFaceDB.addFace(phoneNumber,
                        mAFR_FSDKFace);
                Log.v("人脸识别", "handleMessage2-->" + true + "/" + bool2);
                deletePicture();

                Message message = new Message();
                message.what = -1;
                message.obj = new Object[]{msg.what, bool2};
                mUIHandler.sendMessageDelayed(message, 100);
            }
        };
    }

    /**
     * @param bundle
     * @note bundle data :
     * String imagePath
     */
    private boolean getIntentData(Bundle bundle) {
        try {
            mFilePath = bundle.getString("imagePath");
            if (mFilePath == null || mFilePath.isEmpty()) {
                return false;
            }
            ///storage/sdcard/Pictures/arcsoft_20180315154351.jpg
            ///storage/sdcard/Pictures/1521093628457.jpg
            Log.v("人脸识别", "getIntentData:" + mFilePath);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void deletePicture() {
        File file = new File(mFilePath);
        if (file != null && !file.exists()) {
            return;
        }
        if (file.delete()) {
            Log.v("人脸识别", "deletePicture2-->");
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v("人脸识别", "surfaceChanged-->" + width + "/" + height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceHolder = null;
        try {
            view.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class UIHandler extends android.os.Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_CODE) {

                if (msg.arg1 == MSG_EVENT_REG) {
                    LayoutInflater inflater = LayoutInflater.from(FaceRegisterActivity.this);
                    View layout = inflater.inflate(R.layout.dialog_register, null);
                    final ExtImageView mExtImageView = (ExtImageView) layout.findViewById(R.id
                            .extimageview);
                    final EditText mEditText1 = (EditText) layout.findViewById(R.id
                            .number_editview);
                    final EditText mEditText2 = (EditText) layout.findViewById(R.id.phone_editview);
//                    mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});

                    mEditText1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            focus = hasFocus;
                        }
                    });

                    mEditText1.setInputType(InputType.TYPE_NULL);//设置有光标不显示输入法
                    mEditText2.setInputType(InputType.TYPE_NULL);

                    faceBitmap = (Bitmap) msg.obj;
                    mExtImageView.setImageBitmap(faceBitmap);

                    new AlertDialog.Builder(FaceRegisterActivity.this).setTitle("注册人脸信息").setIcon
                            (R.mipmap.ic_launcher)
//                            .setIcon(R.mipmap.ic_launcher)
                            .setView(layout)
//                            .setPositiveButton("确定", null)
//                            .setNegativeButton("取消", null)
                            .setOnKeyListener(new DialogInterface.OnKeyListener() {
                                @Override
                                public boolean onKey(DialogInterface dialog, int keyCode,
                                                     KeyEvent event) {
                                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                                        houseNumber = mEditText1.getText().toString().trim();
                                        phoneNumber = mEditText2.getText().toString().trim();
                                        int key = convertKeyCode(keyCode);
                                        Log.v("人脸识别", "onKey1-->" + keyCode + "/" + houseNumber
                                                + "/" + phoneNumber + "/" + key + "/" + focus);
                                        if (key >= 0) {
                                            if (focus) {//输入账号
                                                callInput(key, mEditText1);
                                            } else {//输入密码
                                                callInput(key, mEditText2);
                                            }
                                        } else if (keyCode == DeviceConfig.DEVICE_KEYCODE_STAR) {
                                            Log.v("人脸识别", "onKey7-->" + keyCode + "/" +
                                                    houseNumber + "/" + phoneNumber + "/" + key +
                                                    "/" + focus);
                                            if (TextUtils.isEmpty(houseNumber) && TextUtils
                                                    .isEmpty(phoneNumber)) {
                                                dialog.dismiss();
                                                FaceRegisterActivity.this.finish();
                                                return true;
                                            }
                                            if (focus) {
                                                if (!TextUtils.isEmpty(houseNumber)) {
                                                    setTextValue(mEditText1, backKey
                                                            (mEditText1.getText().toString().trim
                                                                    ()));
                                                }
                                            } else {
                                                if (!TextUtils.isEmpty(phoneNumber)) {
                                                    setTextValue(mEditText2, backKey
                                                            (mEditText2.getText().toString().trim
                                                                    ()));
                                                    return true;
                                                }
                                                mEditText1.setFocusable(true);
                                                mEditText1.setFocusableInTouchMode(true);
                                                mEditText1.requestFocus();
                                                mEditText1.requestFocusFromTouch();
                                            }
                                        } else if (keyCode == DeviceConfig.DEVICE_KEYCODE_POUND) {//确认键
                                            if (focus) {
                                                if (TextUtils.isEmpty(houseNumber)) {
                                                    Toast.makeText(FaceRegisterActivity.this,
                                                            "请输入房屋编号", Toast.LENGTH_SHORT).show();
                                                } else if (houseNumber.length() < 4) {
                                                    Toast.makeText(FaceRegisterActivity.this,
                                                            "请输入4位房屋编号", Toast.LENGTH_SHORT).show();
                                                }
                                                mEditText1.setFocusable(true);
                                                mEditText1.setFocusableInTouchMode(true);
                                                mEditText1.requestFocus();
                                                mEditText1.requestFocusFromTouch();
                                            }
                                            if (!TextUtils.isEmpty(houseNumber) && houseNumber
                                                    .length() == 4) {
                                                if (!focus && TextUtils.isEmpty(phoneNumber)) {
                                                    Toast.makeText(FaceRegisterActivity.this,
                                                            "请输入手机号码", Toast.LENGTH_SHORT).show();
                                                } else if (!focus && phoneNumber.length() < 11) {
                                                    Toast.makeText(FaceRegisterActivity.this,
                                                            "请输入11位手机号码", Toast.LENGTH_SHORT)
                                                            .show();
                                                }
                                                mEditText2.setFocusable(true);
                                                mEditText2.setFocusableInTouchMode(true);
                                                mEditText2.requestFocus();
                                                mEditText2.requestFocusFromTouch();
                                            }
                                            if (!TextUtils.isEmpty(houseNumber) && houseNumber
                                                    .length() == 4 && !TextUtils.isEmpty
                                                    (phoneNumber) && phoneNumber.length() == 11) {
                                                Log.v("人脸识别", "onKey3-->" + keyCode + "/" +
                                                        houseNumber + "/" + phoneNumber + "/" +
                                                        key + "/" + focus);
                                                if (inputFaceInfo()) {
                                                    dialog.dismiss();
                                                }
                                            }
                                        }
                                    }
                                    return true;
                                }
                            }).show();
                } else if (msg.arg1 == MSG_EVENT_NO_FEATURE) {
                    Toast.makeText(FaceRegisterActivity.this, "人脸特征无法检测，请换一张图片", Toast
                            .LENGTH_SHORT).show();
                } else if (msg.arg1 == MSG_EVENT_NO_FACE) {
                    Toast.makeText(FaceRegisterActivity.this, "没有检测到人脸，请换一张图片", Toast
                            .LENGTH_SHORT).show();
                } else if (msg.arg1 == MSG_EVENT_FD_ERROR) {
                    Toast.makeText(FaceRegisterActivity.this, "FD初始化失败，错误码：" + msg.arg2, Toast
                            .LENGTH_SHORT).show();
                } else if (msg.arg1 == MSG_EVENT_FR_ERROR) {
                    Toast.makeText(FaceRegisterActivity.this, "FR初始化失败，错误码：" + msg.arg2, Toast
                            .LENGTH_SHORT).show();
                }
            } else if (msg.what == -1) {
                Object[] objects = (Object[]) msg.obj;
                boolean bool = (boolean) objects[1];
                if ((int) objects[0] == 0) {
                    Toast.makeText(FaceRegisterActivity.this, bool ? "注册成功" : "注册失败", Toast
                            .LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FaceRegisterActivity.this, bool ? "更新成功" : "更新失败", Toast
                            .LENGTH_SHORT).show();
                }
                if (bool) {
                    FaceRegisterActivity.this.finish();
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == DeviceConfig.DEVICE_KEYCODE_STAR) {
            this.finish(); // back button
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (faceBitmap != null) {
            faceBitmap.recycle();
            faceBitmap = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (handlerThread != null) {
            handlerThread.quit();
        }
    }

    private boolean inputFaceInfo() {
        if (fileOperation(phoneNumber)) {
            new AlertDialog.Builder(this).setMessage("手机号码(" + phoneNumber + ")的人脸信息已注册，是否更新?")
//                    .setPositiveButton("确定", null)
//                    .setNegativeButton("取消", null)
                    .setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                                    handler.sendEmptyMessageDelayed(1, 100);
//                                    mRegisterViewAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                } else if (keyCode == KeyEvent.KEYCODE_DEL) {
                                    dialog.dismiss();
                                }
                            }
                            return false;
                        }
                    }).show();
        } else {
            handler.sendEmptyMessageDelayed(0, 100);
//            mRegisterViewAdapter.notifyDataSetChanged();
        }
        return true;
    }

    private boolean fileOperation(String name) {
        boolean bool = false;
        String path = getExternalCacheDir().getPath();
//        LogDoor.v("人脸识别", "fileOperation-->" + path);
        File file = new File(path);
        if (file != null && file.exists()) {
            File[] files = file.listFiles();// 读取文件夹下文件
            if (files != null) {
                for (File file1 : files) {
                    if (file1.isDirectory()) {//检查此路径名的文件是否是一个目录(文件夹)
                        continue;
                    }
                    String fileName = file1.getName();
                    Log.v("人脸识别", "fileOperation1-->" + fileName + "/" + file1.getPath());
                    if (fileName.endsWith(".data")) {
                        bool = fileName.contains(name);
                        if (fileName.contains(name)) {
                            bool = true;
                            break;
                        }
                    }
                }
            }
        }
        return bool;
    }

    private int convertKeyCode(int keyCode) {
        int value = -1;
        if ((keyCode == KeyEvent.KEYCODE_0)) {
            value = 0;
        } else if ((keyCode == KeyEvent.KEYCODE_1)) {
            value = 1;
        } else if ((keyCode == KeyEvent.KEYCODE_2)) {
            value = 2;
        } else if ((keyCode == KeyEvent.KEYCODE_3)) {
            value = 3;
        } else if ((keyCode == KeyEvent.KEYCODE_4)) {
            value = 4;
        } else if ((keyCode == KeyEvent.KEYCODE_5)) {
            value = 5;
        } else if ((keyCode == KeyEvent.KEYCODE_6)) {
            value = 6;
        } else if ((keyCode == KeyEvent.KEYCODE_7)) {
            value = 7;
        } else if ((keyCode == KeyEvent.KEYCODE_8)) {
            value = 8;
        } else if ((keyCode == KeyEvent.KEYCODE_9)) {
            value = 9;
        }
        return value;
    }

    private void callInput(int key, EditText editText) {
        setTextValue(editText, editText.getText().toString().trim() + key);
    }

    private void setTextValue(final EditText editText, String value) {
        final String thisValue = value;
        editText.post(new Runnable() {
            @Override
            public void run() {
                setTextView(editText, thisValue);
            }
        });
    }

    private void setTextView(EditText editText, String txt) {
        editText.setText(txt);
    }

    private String backKey(String code) {
        if (code != null && code != "") {
            int length = code.length();
            if (length == 1) {
                code = "";
            } else {
                code = code.substring(0, (length - 1));
            }
        }
        return code;
    }
}
