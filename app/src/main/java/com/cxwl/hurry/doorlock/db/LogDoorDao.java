package com.cxwl.hurry.doorlock.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "LOG_DOOR".
*/
public class LogDoorDao extends AbstractDao<LogDoor, Long> {

    public static final String TABLENAME = "LOG_DOOR";

    /**
     * Properties of entity LogDoor.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Ka_id = new Property(1, String.class, "ka_id", false, "ka_id");
        public final static Property Kaimenfangshi = new Property(2, String.class, "kaimenfangshi", false, "kaimenfangshi");
        public final static Property Mac = new Property(3, String.class, "mac", false, "mac");
        public final static Property Kaimenshijian = new Property(4, String.class, "kaimenshijian", false, "kaimenshijian");
        public final static Property Phone = new Property(5, String.class, "phone", false, "phone");
        public final static Property Uuid = new Property(6, String.class, "uuid", false, "uuid");
        public final static Property Kaimenjietu = new Property(7, String.class, "kaimenjietu", false, "kaimenjietu");
    }


    public LogDoorDao(DaoConfig config) {
        super(config);
    }
    
    public LogDoorDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"LOG_DOOR\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"ka_id\" TEXT," + // 1: ka_id
                "\"kaimenfangshi\" TEXT," + // 2: kaimenfangshi
                "\"mac\" TEXT," + // 3: mac
                "\"kaimenshijian\" TEXT," + // 4: kaimenshijian
                "\"phone\" TEXT," + // 5: phone
                "\"uuid\" TEXT," + // 6: uuid
                "\"kaimenjietu\" TEXT);"); // 7: kaimenjietu
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"LOG_DOOR\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, LogDoor entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String ka_id = entity.getKa_id();
        if (ka_id != null) {
            stmt.bindString(2, ka_id);
        }
 
        String kaimenfangshi = entity.getKaimenfangshi();
        if (kaimenfangshi != null) {
            stmt.bindString(3, kaimenfangshi);
        }
 
        String mac = entity.getMac();
        if (mac != null) {
            stmt.bindString(4, mac);
        }
 
        String kaimenshijian = entity.getKaimenshijian();
        if (kaimenshijian != null) {
            stmt.bindString(5, kaimenshijian);
        }
 
        String phone = entity.getPhone();
        if (phone != null) {
            stmt.bindString(6, phone);
        }
 
        String uuid = entity.getUuid();
        if (uuid != null) {
            stmt.bindString(7, uuid);
        }
 
        String kaimenjietu = entity.getKaimenjietu();
        if (kaimenjietu != null) {
            stmt.bindString(8, kaimenjietu);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, LogDoor entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String ka_id = entity.getKa_id();
        if (ka_id != null) {
            stmt.bindString(2, ka_id);
        }
 
        String kaimenfangshi = entity.getKaimenfangshi();
        if (kaimenfangshi != null) {
            stmt.bindString(3, kaimenfangshi);
        }
 
        String mac = entity.getMac();
        if (mac != null) {
            stmt.bindString(4, mac);
        }
 
        String kaimenshijian = entity.getKaimenshijian();
        if (kaimenshijian != null) {
            stmt.bindString(5, kaimenshijian);
        }
 
        String phone = entity.getPhone();
        if (phone != null) {
            stmt.bindString(6, phone);
        }
 
        String uuid = entity.getUuid();
        if (uuid != null) {
            stmt.bindString(7, uuid);
        }
 
        String kaimenjietu = entity.getKaimenjietu();
        if (kaimenjietu != null) {
            stmt.bindString(8, kaimenjietu);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public LogDoor readEntity(Cursor cursor, int offset) {
        LogDoor entity = new LogDoor( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // ka_id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // kaimenfangshi
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // mac
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // kaimenshijian
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // phone
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // uuid
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7) // kaimenjietu
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, LogDoor entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setKa_id(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setKaimenfangshi(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setMac(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setKaimenshijian(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setPhone(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setUuid(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setKaimenjietu(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(LogDoor entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(LogDoor entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(LogDoor entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}