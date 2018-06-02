package com.cxwl.hurry.doorlock.entity;

import com.arcsoft.facerecognition.AFR_FSDKFace;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by William on 2018/6/2.
 */

public class FaceRegist {
    public String mName;
    public List<AFR_FSDKFace> mFaceList, mIDFaceList;

    public FaceRegist(String name) {
        mName = name;
        mFaceList = new ArrayList<>();
        mIDFaceList = new ArrayList<>();
    }
}
