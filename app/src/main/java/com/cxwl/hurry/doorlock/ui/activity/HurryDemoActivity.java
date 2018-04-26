package com.cxwl.hurry.doorlock.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cxwl.hurry.doorlock.R;
import com.hurray.plugins.RootCmd;
import com.hurray.plugins.rkctrl;
import com.hurray.plugins.serial;

/**
 * 昊睿的demo
 * Created by William on 2018/4/26.
 */

public class HurryDemoActivity extends AppCompatActivity implements DialogInterface
        .OnClickListener, TextToSpeech.OnInitListener {

    private static String TAG = "HurryDemoActivity";

    //GPIO output
    //GPIO1	 输出	控制IO1高低电平
    //GPIO4	 输出	输出LOCK电压信号
    //GPIO19 输出    控制继电器
    //GPIO21 输出    控制12V 双目摄像头供电

    //GPIO input
    //GPIO2	 输入	检测外部光线强弱       		    IO2
    //GPIO9	 输入	用来检测拆机报警信号            IO3
    //GPIO10 输入    用来检测出门按钮信号  		  	IO4
    //GPIO11 输入    用来检测电磁锁的状态          	IO5


    public ToggleButton button_gpiooutput_IO1;
    public ToggleButton button_gpiooutput_lock;
    public ToggleButton button_gpiooutput_relay;
    public ToggleButton button_gpiooutput_lightboard;

    public ToggleButton button_gpioinput_IO2;
    public ToggleButton button_gpioinput_IO3;
    public ToggleButton button_gpioinput_IO4;
    public ToggleButton button_gpioinput_IO5;


    public TextView textview_msg_output = null;

    public EditText edittext_keyoutput;
    public EditText edittext_rfidoutput;
    public EditText edittext_msgoutput;

    public boolean status = false;

    public rkctrl m_rkctrl = new rkctrl();
    public serial pSerialport = new serial();//读卡器相关类
    public String arg = "/dev/ttyS1,9600,N,1,8";

    public MyHandler myHandler = new MyHandler();

    public RootCmd rootCmd = new RootCmd();

    private Thread pthread = null;
    int iRead = 0;
    String strRfid = "";
    String strGpiostatus = "";

    boolean bFlag_input_IO2 = true;
    boolean bFlag_input_IO3 = true;
    boolean bFlag_input_IO4 = true;
    boolean bFlag_input_IO5 = true;
    boolean bRunReadSerial = true;


    private String strMsgOutput = "";

    public static final int RESULT_SPEAK = 1;
    private TextToSpeech ttsSpeak;
    private boolean ttsSetupflag = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hurrydemo);

        // TODO: 2018/4/24 "#"键是确认键,只响应获取焦点的控件


        initUi();

        onClickEvent();

        initSerial();
    }

    private void initSerial() {
        int iret = pSerialport.open(arg);
        if (iret > 0) {
            iRead = iret;
            log(String.format("打开串口成功 (port = %s,fd=%d)", arg, iret));
            runReadSerial(iRead);
        } else {
            log(String.format("打开串口失败 (fd=%d)", iret));
        }
    }

    private void initUi() {
        edittext_keyoutput = (EditText) findViewById(R.id.edittext_keyoutput);
        edittext_rfidoutput = (EditText) findViewById(R.id.edittext_rfidoutput);
        edittext_msgoutput = (EditText) findViewById(R.id.edittext_msgoutput);

        //GPIO output
        button_gpiooutput_IO1 = (ToggleButton) findViewById(R.id.button_gpiooutput_IO1);
        button_gpiooutput_lock = (ToggleButton) findViewById(R.id.button_gpiooutput_lock);
        button_gpiooutput_relay = (ToggleButton) findViewById(R.id.button_gpiooutput_relay);
        button_gpiooutput_lightboard = (ToggleButton) findViewById(R.id
                .button_gpiooutput_lightboard);

        //GPIO input
        button_gpioinput_IO2 = (ToggleButton) findViewById(R.id.button_gpioinput_IO2);
        button_gpioinput_IO3 = (ToggleButton) findViewById(R.id.button_gpioinput_IO3);
        button_gpioinput_IO4 = (ToggleButton) findViewById(R.id.button_gpioinput_IO4);
        button_gpioinput_IO5 = (ToggleButton) findViewById(R.id.button_gpioinput_IO5);

    }

    private void onClickEvent() {
        //GPIO 输出
        onClickGpioOutput();
        // GPIO 输入
        onClickGpioInput();
    }


    private void onClickGpioOutput() {
        // GPIO1 控制补光灯
        button_gpiooutput_IO1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button_gpiooutput_IO1.isChecked()) {
                    Log.e(TAG, "开");
                    m_rkctrl.exec_io_cmd(1, 1);
                } else {
                    m_rkctrl.exec_io_cmd(1, 0);
                    Log.e(TAG, "关");
                }
            }
        });

        // GPIO4 输出开锁电压信号
        button_gpiooutput_lock.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button_gpiooutput_lock.isChecked()) {
                    m_rkctrl.exec_io_cmd(4, 1);
                } else {
                    m_rkctrl.exec_io_cmd(4, 0);
                }
            }
        });

        // GPIO8 控制USB2供电来打开和关闭继电器
//        m_rkctrl.exec_io_cmd(7, 1);
//        m_rkctrl.exec_io_cmd(7,0);


        //GPIO19 输出 控制 ttyS0供电
        button_gpiooutput_relay.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button_gpiooutput_relay.isChecked()) {
                    m_rkctrl.exec_io_cmd(19, 1);
                } else {
                    m_rkctrl.exec_io_cmd(19, 0);
                }
            }
        });


        //GPIO21 输出 控制补光灯板12V供电
        button_gpiooutput_lightboard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button_gpiooutput_lightboard.isChecked()) {
                    m_rkctrl.exec_io_cmd(21, 1);
                } else {
                    m_rkctrl.exec_io_cmd(21, 0);
                }
            }
        });
    }


    private void onClickGpioInput() {
        // 检测外部光线/人体感应
        button_gpioinput_IO2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button_gpioinput_IO2.isChecked()) {
                    bFlag_input_IO2 = true;
                    runReadInputIO2();

                } else {
                    bFlag_input_IO2 = false;
                }
            }
        });

        // 检测拆机报警信号
        button_gpioinput_IO3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button_gpioinput_IO3.isChecked()) {
                    bFlag_input_IO3 = true;
                    runReadInputIO3();

                } else {
                    bFlag_input_IO3 = false;
                }
            }
        });

        // 检测出门按钮信号
        button_gpioinput_IO4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button_gpioinput_IO4.isChecked()) {
                    bFlag_input_IO4 = true;
                    runReadInputIO4();

                } else {
                    bFlag_input_IO4 = false;
                }
            }
        });


        // 检测电磁锁状态
        button_gpioinput_IO5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (button_gpioinput_IO5.isChecked()) {
                    bFlag_input_IO5 = true;
                    runReadInputIO5();

                } else {
                    bFlag_input_IO5 = false;
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (null != event) {
            Log.e(TAG, "KeyEvent" + event.toString());
        }
        Log.e(TAG, "keyCode " + keyCode);

        if (keyCode == KeyEvent.KEYCODE_0) {
            edittext_keyoutput.setText("0");
            button_gpiooutput_IO1.performClick();
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_1) {
            edittext_keyoutput.setText("1");
            button_gpiooutput_lock.performClick();
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_2) {
            edittext_keyoutput.setText("2");
            button_gpiooutput_relay.performClick();
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_3) {
            edittext_keyoutput.setText("3");
            button_gpiooutput_lightboard.performClick();
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_4) {
            edittext_keyoutput.setText("4");
            button_gpioinput_IO5.performClick();
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_5) {
            edittext_keyoutput.setText("5");
            button_gpioinput_IO3.performClick();
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_6) {
            edittext_keyoutput.setText("6");
            button_gpioinput_IO4.performClick();
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_7) {
            edittext_keyoutput.setText("7");
            button_gpioinput_IO2.performClick();
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_8) {
            edittext_keyoutput.setText("8" + " 按键2获取焦点");
            getFocus(button_gpiooutput_lock);
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_9) {
            edittext_keyoutput.setText("9" + " 按键2失去焦点");
            delFocus(button_gpiooutput_lock);
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_STAR) {
            edittext_keyoutput.setText("*");
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_POUND) {
            edittext_keyoutput.setText("");
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_F4) {
            edittext_keyoutput.setText("️➡️");
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_F3) {
            edittext_keyoutput.setText("⬅️️");
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_F2) {
            edittext_keyoutput.setText("管理处");
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_F1) {
            edittext_keyoutput.setText("帮助");
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_A) {
            edittext_keyoutput.setText("A" + "管理处");
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_B) {
            edittext_keyoutput.setText("B" + "拨号");
            String wifiMac = getWifiMac();
            Log.e(TAG, "mac " + wifiMac);
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_C) {
            edittext_keyoutput.setText("C" + "帮助");
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_D) {
            edittext_keyoutput.setText("D" + "返回");
            finish();
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_DEL) {
            edittext_keyoutput.setText("*");
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void log(String msg) {
        Log.e(TAG, msg);
        strMsgOutput += msg;
        strMsgOutput += "\r\n";
        edittext_msgoutput.setText(strMsgOutput);


        //方便测试用 如果超过显示区域就清空
        if (edittext_msgoutput.getLineCount() > 10) {
            strMsgOutput = "";
            edittext_msgoutput.setText("");
        }
    }

    /**
     * 将日志滚动至底部
     */
    public void LogScrollBottom() {
        int line = textview_msg_output.getLineCount();
        int linehigh = textview_msg_output.getLineHeight();
        int high = textview_msg_output.getHeight();
        if (linehigh * line > high) {
            int scrollY = line * linehigh - high;
            if (scrollY > 0) {
                textview_msg_output.scrollTo(0, scrollY);
            }
        }
    }

    public void runReadInputIO2() {
        Runnable run = new Runnable() {
            public void run() {
                while (bFlag_input_IO2) {

                    //延迟读取
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    int gpioid = 2;
                    //gpio检测 没作用
                    int r = m_rkctrl.get_io_status(gpioid);

                    String msg = null;
                    if (r == 0) {
                        msg = String.format("检测到白天");
                    } else if (r == 1) {
                        msg = String.format("检测到黑夜");
                    }

                    Message msgpwd = new Message();
                    msgpwd.what = 0;
                    Bundle data = new Bundle();
                    data.putString("data", msg);
                    msgpwd.setData(data);
                    myHandler.sendMessage(msgpwd);

                }
            }
        };
        pthread = new Thread(run);
        pthread.start();
    }

    public void runReadInputIO3() {
        Runnable run = new Runnable() {
            public void run() {
                while (bFlag_input_IO3) {

                    //延迟读取
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    int gpioid = 9;
                    int r = m_rkctrl.get_io_status(gpioid);

                    String msg = null;
                    if (r == 0) {
                        msg = String.format("未检测到拆机报警信号");
                    } else if (r == 1) {
                        msg = String.format("检测到拆机报警信号");
                    }

                    Message msgpwd = new Message();
                    msgpwd.what = 0;
                    Bundle data = new Bundle();
                    data.putString("data", msg);
                    msgpwd.setData(data);
                    myHandler.sendMessage(msgpwd);

                }
            }
        };
        pthread = new Thread(run);
        pthread.start();
    }

    public void runReadInputIO4() {
        Runnable run = new Runnable() {
            public void run() {
                while (bFlag_input_IO4) {

                    //延迟读取
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    int gpioid = 10;
                    int r = m_rkctrl.get_io_status(gpioid);

                    String msg = null;
                    if (r == 0) {
                        msg = String.format("未检测出门按钮信号");
                    } else if (r == 1) {
                        msg = String.format("检测到出门按钮信号");
                    }

                    Message msgpwd = new Message();
                    msgpwd.what = 4;
                    Bundle data = new Bundle();
                    data.putString("data", msg);
                    msgpwd.setData(data);
                    myHandler.sendMessage(msgpwd);

                }
            }
        };
        pthread = new Thread(run);
        pthread.start();
    }

    public void runReadInputIO5() {
        Runnable run = new Runnable() {
            public void run() {
                while (bFlag_input_IO5) {

                    //延迟读取
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    int gpioid = 11;
                    int r = m_rkctrl.get_io_status(gpioid);

                    String msg = null;
                    if (r == 0) {
                        msg = String.format("未检测到电磁锁状态");
                    } else if (r == 1) {
                        msg = String.format("检测到电磁锁状态");
                    }

                    Message msgpwd = new Message();
                    msgpwd.what = 4;
                    Bundle data = new Bundle();
                    data.putString("data", msg);
                    msgpwd.setData(data);
                    myHandler.sendMessage(msgpwd);

                }
            }
        };
        pthread = new Thread(run);
        pthread.start();
    }

    // 读取串口数据线程
    public void runReadSerial(final int fd) {
        Runnable run = new Runnable() {
            public void run() {
                while (bRunReadSerial) {
                    int r = pSerialport.select(fd, 1, 0);
                    if (r == 1) {
                        //测试 普通读串口数据
                        byte[] buf = new byte[50];
                        buf = pSerialport.read(fd, 100);
                        String str = "";

                        if (buf == null) break;
                        if (buf.length <= 0) break;

                        str = byte2HexString(buf);
                        Message msgpwd = new Message();
                        msgpwd.what = 1;
                        Bundle data = new Bundle();
                        data.putString("data", str);
                        msgpwd.setData(data);
                        myHandler.sendMessage(msgpwd);

                    }
                }
                onThreadEnd();
            }
        };
        pthread = new Thread(run);
        pthread.start();
    }

    private String substring = "";

    public class MyHandler extends Handler {
        public MyHandler() {
        }

        public MyHandler(Looper L) {
            super(L);
        }

        // 子类必须重写此方法,接受数据
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String strData = "";
            // 此处可以更新UI
            switch (msg.what) {
                case 1:
                    strData = msg.getData().getString("data");

                    Log.e(TAG, "strData " + strData);

                    strRfid += strData;
                    if (strRfid.length() > 27) {
                        substring = strRfid.substring(16, 24).toUpperCase();
                        Log.e(TAG, "卡号231321 " + substring);
                        strRfid = "";
                    }
                    edittext_rfidoutput.setText(substring);
                    log(strRfid);
                    Log.v("test", strRfid);

                    //方便测试用 如果超过显示区域就清空
                    if (edittext_rfidoutput.getLineCount() > 10) {
                        strRfid = "";
                        edittext_rfidoutput.setText("");
                    }
                    break;
                default:
                    strData = msg.getData().getString("data");
                    Log.v("recv", strData);
                    log(strData);
                    break;
            }
        }
    }


    public void onThreadEnd() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                log(String.format("%s", "监听串口线程结束"));
            }
        });
    }

    /**
     * byte[]转换成字符串
     *
     * @param b
     * @return
     */
    public static String byte2HexString(byte[] b) {
//        57 43 44 41 00 00 00 00 35109108 00 23
//        57 43 44 41 00 00 00 00 0c7cc580 00 23

        StringBuffer sb = new StringBuffer();
        int length = b.length;
        for (int i = 0; i < b.length; i++) {
            String stmp = Integer.toHexString(b[i] & 0xff);
//            Log.e(TAG,"字节数组 " +  b.length + " " + b[i]);
            if (stmp.length() == 1) {
                sb.append("0" + stmp);
            } else {
                sb.append(stmp);
            }
        }
        return sb.toString();
    }

    //===[语音控件安装--开始]===================================================
    //讯飞语音播放
    public void OnSpeak(String speakStr) {
        ttsSpeak.speak(speakStr, TextToSpeech.QUEUE_ADD, null);
    }

    //讯飞语音状态反馈
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
        } else if (status == TextToSpeech.ERROR) {
        }
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        // TODO Auto-generated method stub

    }

    /**
     * 强制让控件获取焦点
     *
     * @param view
     */
    private void getFocus(View view) {
        view.setFocusable(true);//普通物理方式获取焦点
        view.setFocusableInTouchMode(true);//触摸模式获取焦点,不是触摸屏啊
        view.requestFocus();//要求获取焦点

        boolean focusable = view.isFocusable();
        Log.e(TAG, "获取焦点 " + focusable);
    }

    private void delFocus(View view) {
        view.setFocusable(false);//普通物理方式获取焦点
        view.setFocusableInTouchMode(false);//触摸模式获取焦点,不是触摸屏啊

        boolean focusable = view.isFocusable();
        Log.e(TAG, "失去焦点 " + focusable);
    }

    /**
     * 获取Mac地址
     */
    String mac;

    @SuppressLint("WifiManagerLeak")
    protected String getWifiMac() {
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        mac = info.getMacAddress();
        if (mac != null) {
            return mac;
        } else {
            return "";
        }
    }


    @Override
    protected void onDestroy() {

        bFlag_input_IO2 = false;
        bFlag_input_IO3 = false;
        bFlag_input_IO4 = false;
        bFlag_input_IO5 = false;
        bRunReadSerial = false;

//        pthread.interrupt();
//        pthread = null;
        myHandler = null;
        if (iRead != 0) {
            pSerialport.close(iRead);
        }
        pSerialport = null;
        m_rkctrl = null;
        super.onDestroy();
    }
}
