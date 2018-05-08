package com.cxwl.hurry.doorlock.db;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.cxwl.hurry.doorlock.db.Ka;
import com.cxwl.hurry.doorlock.db.Lian;
import com.cxwl.hurry.doorlock.db.Log;

import com.cxwl.hurry.doorlock.db.KaDao;
import com.cxwl.hurry.doorlock.db.LianDao;
import com.cxwl.hurry.doorlock.db.LogDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig kaDaoConfig;
    private final DaoConfig lianDaoConfig;
    private final DaoConfig logDaoConfig;

    private final KaDao kaDao;
    private final LianDao lianDao;
    private final LogDao logDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        kaDaoConfig = daoConfigMap.get(KaDao.class).clone();
        kaDaoConfig.initIdentityScope(type);

        lianDaoConfig = daoConfigMap.get(LianDao.class).clone();
        lianDaoConfig.initIdentityScope(type);

        logDaoConfig = daoConfigMap.get(LogDao.class).clone();
        logDaoConfig.initIdentityScope(type);

        kaDao = new KaDao(kaDaoConfig, this);
        lianDao = new LianDao(lianDaoConfig, this);
        logDao = new LogDao(logDaoConfig, this);

        registerDao(Ka.class, kaDao);
        registerDao(Lian.class, lianDao);
        registerDao(Log.class, logDao);
    }
    
    public void clear() {
        kaDaoConfig.clearIdentityScope();
        lianDaoConfig.clearIdentityScope();
        logDaoConfig.clearIdentityScope();
    }

    public KaDao getKaDao() {
        return kaDao;
    }

    public LianDao getLianDao() {
        return lianDao;
    }

    public LogDao getLogDao() {
        return logDao;
    }

}