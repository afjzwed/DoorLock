package com.cxwl.hurry.doorlock.face;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.cxwl.hurry.doorlock.entity.FaceRegist;
import com.cxwl.hurry.doorlock.utils.BitmapUtils;
import com.guo.android_extend.java.ExtInputStream;
import com.guo.android_extend.java.ExtOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.cxwl.hurry.doorlock.config.Constant.arc_appid;
import static com.cxwl.hurry.doorlock.config.Constant.fr_key;

/**
 * Created by William on 2018/5/17.
 */

public class FaceDB {
    private final String TAG = this.getClass().toString();


    String mDBPath;
    public List<FaceRegist> mRegister;//已注册的集合（用text文件保存）
    AFR_FSDKEngine mFREngine;
    AFR_FSDKVersion mFRVersion;
    boolean mUpgrade;

    public FaceDB(String path) {
        mDBPath = path;
        mRegister = new ArrayList<>();
        mFRVersion = new AFR_FSDKVersion();
        mUpgrade = false;
        mFREngine = new AFR_FSDKEngine();
        AFR_FSDKError error = mFREngine.AFR_FSDK_InitialEngine(arc_appid, fr_key);
        if (error.getCode() != AFR_FSDKError.MOK) {
            Log.e(TAG, "AFR_FSDK_InitialEngine fail! error code :" + error.getCode());
        } else {
            mFREngine.AFR_FSDK_GetVersion(mFRVersion);
            Log.d(TAG, "AFR_FSDK_GetVersion=" + mFRVersion.toString());
        }
    }

    public void destroy() {
        if (mFREngine != null) {
            mFREngine.AFR_FSDK_UninitialEngine();
        }
    }

    private boolean saveInfo() {
        try {
            FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt");
            ExtOutputStream bos = new ExtOutputStream(fs);
            bos.writeString(mFRVersion.toString() + "," + mFRVersion.getFeatureLevel());
            bos.close();
            fs.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean loadInfo() {
        if (!mRegister.isEmpty()) {
            return false;
        }
        try {
            FileInputStream fs = new FileInputStream(mDBPath + "/face.txt");
            ExtInputStream bos = new ExtInputStream(fs);
            //load version
            String version_saved = bos.readString();
            if (version_saved.equals(mFRVersion.toString() + "," + mFRVersion.getFeatureLevel())) {
                mUpgrade = true;
            }
            //load all regist name.
            if (version_saved != null) {
                for (String name = bos.readString(); name != null; name = bos.readString()) {
                    if (new File(mDBPath + "/" + name + ".data").exists()) {
                        Log.v("人脸识别", "loadInfo-->" + name);
                        mRegister.add(new FaceRegist(new String(name)));
                    }
                }
            }
            bos.close();
            fs.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.v("人脸识别", "loadInfo/FileNotFoundException-->" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.v("人脸识别", "loadInfo/IOException-->" + e.getMessage());
        }
        return false;
    }

    public boolean saveBitmap(String name, Bitmap bitmap) {
        boolean bool = false;
        File file = new File(mDBPath + "/" + name + ".png");
        Log.v("人脸识别", "saveBitmap1-->" + file.getPath());
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            Bitmap bmp = BitmapUtils.zoomImg(bitmap, 80, 80);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            bool = true;
            Log.v("人脸识别", "saveBitmap2-->" + file.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.v("人脸识别", "saveBitmap3-->" + e.getMessage());
            bool = false;
        } catch (IOException e) {
            e.printStackTrace();
            Log.v("人脸识别", "saveBitmap4-->" + e.getMessage());
            bool = false;
        }
        return bool;
    }

    public Bitmap getFaceBitmap(String name) {
        Bitmap bitmap = null;
        try {
            File file = new File(mDBPath + "/" + name + ".png");
            if (file.exists()) {
                bitmap = BitmapFactory.decodeFile(file.getPath());
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bitmap;
    }

    public boolean loadFaces() {
        if (loadInfo()) {
            try {
                for (FaceRegist face : mRegister) {
                    Log.v("人脸识别", "load name:" + face.mName + "'s face feature data.");
                    FileInputStream fs = new FileInputStream(mDBPath + "/" + face.mName + ".data");
                    ExtInputStream bos = new ExtInputStream(fs);
                    AFR_FSDKFace afr = null;
                    do {
                        if (afr != null) {
                            if (mUpgrade) {
                                //upgrade data.
                            }
                            if (face.mName.length() > 11) {
                                face.mIDFaceList.add(afr);
                            } else {
                                face.mFaceList.add(afr);
                            }
                        }
                        afr = new AFR_FSDKFace();
                    } while (bos.readBytes(afr.getFeatureData()));
                    bos.close();
                    fs.close();
                }
                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.v("人脸识别", "loadFaces/FileNotFoundException-->" + e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Log.v("人脸识别", "loadFaces/IOException-->" + e.getMessage());
            }
        }
        return false;
    }

    public boolean addFace(String name, AFR_FSDKFace face) {
        // TODO: 2018/5/25 后期用于物业存AFR_FSDKFace人脸信息数据
//        String s = new Gson().toJson(face);
//        AFR_FSDKFace afr_fsdkFace = new Gson().fromJson(s, AFR_FSDKFace.class);

        boolean bool = false;
        try {
            //check if already registered.
            boolean add = true;
            for (FaceRegist frface : mRegister) {//遍历已有数据库，检测是否注册过手机号
                if (frface.mName.equals(name)) {//数据库中已有对应手机号，直接添加数据至对应集合中
                    if (name.length() > 11) {
                        frface.mIDFaceList.add(face);//大于11位录入身份证号
                    } else {
                        frface.mFaceList.add(face);//11位以下录入手机号
                    }
                    add = false;
                    break;
                }
            }
            if (add) { // not registered.手机号未注册
                FaceRegist frface = new FaceRegist(name);
                if (name.length() > 11) {
                    frface.mIDFaceList.add(face);
                } else {
                    frface.mFaceList.add(face);
                }
                mRegister.add(frface);
                Log.v("人脸识别", "addFace-->" + 333333);
            }

            Log.v("人脸识别", "addFace-->" + 111);
            bool = saveInfo();//保存数据
            if (bool) {
                //update all names
                FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt", true);
                ExtOutputStream bos = new ExtOutputStream(fs);
                for (FaceRegist frface : mRegister) {
                    bos.writeString(frface.mName);
                }
                bos.close();
                fs.close();

                //save new feature 单独保存人脸数据
                fs = new FileOutputStream(mDBPath + "/" + name + ".data", true);
                bos = new ExtOutputStream(fs);
                bos.writeBytes(face.getFeatureData());
                bos.close();
                fs.close();

                bool = true;
                Log.v("人脸识别", "addFace-->" + 222);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            bool = false;
        } catch (IOException e) {
            e.printStackTrace();
            bool = false;
        }
        return bool;
    }

    public boolean delete(String name) {
        try {
            //check if already registered.
            boolean find = false;
            for (FaceRegist frface : mRegister) {
                if (frface.mName.equals(name)) {
                    File delfile = new File(mDBPath + "/" + name + ".data");
                    if (delfile.exists()) {
                        delfile.delete();
                    }
                    mRegister.remove(frface);
                    find = true;
                    break;
                }
            }

            if (find) {
                if (saveInfo()) {
                    //update all names
                    FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt", true);
                    ExtOutputStream bos = new ExtOutputStream(fs);
                    for (FaceRegist frface : mRegister) {
                        bos.writeString(frface.mName);
                    }
                    bos.close();
                    fs.close();
                }
            }
            return find;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean upgrade() {
        return false;
    }
}
