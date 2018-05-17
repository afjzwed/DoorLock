package com.cxwl.hurry.doorlock.utils;

import com.cxwl.hurry.doorlock.db.KaDao;
import com.cxwl.hurry.doorlock.db.Log;

import com.cxwl.hurry.doorlock.MainApplication;
import com.cxwl.hurry.doorlock.db.DaoSession;
import com.cxwl.hurry.doorlock.db.Ka;
import com.cxwl.hurry.doorlock.db.Lian;
import com.cxwl.hurry.doorlock.db.LianDao;
import com.cxwl.hurry.doorlock.db.LogDao;

import java.util.List;

/**
 * @author xlei
 * @Date 2018/4/27.
 */

public class DbUtils {
    private DaoSession mDaoSession;
    private KaDao mKaDao;
    private LianDao mLianDao;
    private LogDao mLogDao;
    private final static String TAG = "DB";

    private DbUtils(DaoSession daoSession) {
        this.mDaoSession = daoSession;
        mKaDao = this.mDaoSession.getKaDao();
        mLianDao = this.mDaoSession.getLianDao();
        mLogDao = this.mDaoSession.getLogDao();
    }

    private static DbUtils mDbUtils;

    public static DbUtils getInstans() {
        if (mDbUtils == null) {
            mDbUtils = new DbUtils(MainApplication.getGreenDaoSession());
        }
        return mDbUtils;
    }

    /**
     * 增加一条卡信息
     */
    public void insertOneKa(Ka ka) {
        mKaDao.insert(ka);
    }

    /**
     * 删除一条卡信息
     */
    public void deleteOneKa(Ka ka) {
        mKaDao.delete(ka);
    }

    /**
     * 增加所有卡信息
     */
    public void addAllKa(List<Ka> ka) {
        //先删除所有卡信息
        deleteAllKa();
        for (int i = 0; i < ka.size(); i++) {
            mKaDao.insert(ka.get(i));
        }
        android.util.Log.i(TAG, "增加所有卡信息成功");
    }

    /**
     * 查询所有卡信息
     */
    public void quaryAllKa() {
        List<Ka> list = mKaDao.queryBuilder().list();
        if (list!=null){
            android.util.Log.i(TAG, "查询所有卡信息成功"+list.toString());
        }

    }

    /**
     * 更具卡id查询卡信息
     */
    public Ka getKaInfo(String ka_id) {
        Ka unique = mKaDao.queryBuilder().where(KaDao.Properties.Ka_id.eq(ka_id)).unique();
        if (unique != null) {
            return unique;
        }
        return null;
    }

    /**
     * 删除所有卡信息
     */
    public void deleteAllKa() {
        //先删除所有卡信息
        mKaDao.deleteAll();

    }

    /**
     * 增加一条脸信息
     */
    public void insertOneLian(Lian lian) {
        mLianDao.insert(lian);
    }

    /**
     * 删除一条脸信息
     */
    public void deleteOneLian(Lian lian) {
        mLianDao.delete(lian);
    }

    /**
     * 增加所有脸信息
     */
    public void addAllLian(List<Lian> lian) {
        //先删除所有脸信息
        deleteAllLian();
        for (int i = 0; i < lian.size(); i++) {
            mLianDao.insert(lian.get(i));
        }
    }

    /**
     * 删除所有脸信息
     */
    public void deleteAllLian() {
        mLianDao.deleteAll();

    }

    /**
     * 增加一条日志信息
     */
    public void insertOneLog(Log log) {
        mLogDao.insert(log);
    }

    /**
     * 删除一条日志信息
     */
    public void deleteOneLog(Log log) {
        mLogDao.delete(log);
    }

    /**
     * 增加所有日志信息
     */
    public void addAllLog(List<Log> log) {
        //先删除所有日志信息
        deleteAllLog();
        for (int i = 0; i < log.size(); i++) {
            mLogDao.insert(log.get(i));
        }
    }

    /**
     * 删除所有日志信息
     */
    public void deleteAllLog() {
        mLogDao.deleteAll();

    }
}
