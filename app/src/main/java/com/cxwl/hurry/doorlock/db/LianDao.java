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
 * DAO for table "LIAN".
*/
public class LianDao extends AbstractDao<Lian, Long> {

    public static final String TABLENAME = "LIAN";

    /**
     * Properties of entity Lian.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Lian_id = new Property(1, String.class, "lian_id", false, "lian_id");
        public final static Property Lian_info = new Property(2, String.class, "lian_info", false, "lian_info");
    }


    public LianDao(DaoConfig config) {
        super(config);
    }
    
    public LianDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"LIAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"lian_id\" TEXT," + // 1: lian_id
                "\"lian_info\" TEXT);"); // 2: lian_info
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"LIAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Lian entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String lian_id = entity.getLian_id();
        if (lian_id != null) {
            stmt.bindString(2, lian_id);
        }
 
        String lian_info = entity.getLian_info();
        if (lian_info != null) {
            stmt.bindString(3, lian_info);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Lian entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String lian_id = entity.getLian_id();
        if (lian_id != null) {
            stmt.bindString(2, lian_id);
        }
 
        String lian_info = entity.getLian_info();
        if (lian_info != null) {
            stmt.bindString(3, lian_info);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Lian readEntity(Cursor cursor, int offset) {
        Lian entity = new Lian( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // lian_id
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2) // lian_info
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Lian entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setLian_id(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setLian_info(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Lian entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Lian entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Lian entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
