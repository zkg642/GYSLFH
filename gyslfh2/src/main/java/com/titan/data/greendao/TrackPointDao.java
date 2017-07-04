package com.titan.data.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.titan.model.TrackPoint;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TRACK_POINT".
*/
public class TrackPointDao extends AbstractDao<TrackPoint, Long> {

    public static final String TABLENAME = "TRACK_POINT";

    /**
     * Properties of entity TrackPoint.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Time = new Property(1, String.class, "time", false, "TIME");
        public final static Property Lon = new Property(2, double.class, "lon", false, "LON");
        public final static Property Lat = new Property(3, double.class, "lat", false, "LAT");
        public final static Property Userid = new Property(4, String.class, "userid", false, "USERID");
        public final static Property Tag = new Property(5, int.class, "tag", false, "TAG");
        public final static Property Wkid = new Property(6, String.class, "wkid", false, "WKID");
        public final static Property Sbh = new Property(7, String.class, "sbh", false, "SBH");
        public final static Property Status = new Property(8, int.class, "status", false, "STATUS");
        public final static Property REMARK = new Property(9, String.class, "REMARK", false, "REMARK");
    }


    public TrackPointDao(DaoConfig config) {
        super(config);
    }
    
    public TrackPointDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TRACK_POINT\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"TIME\" TEXT NOT NULL ," + // 1: time
                "\"LON\" REAL NOT NULL ," + // 2: lon
                "\"LAT\" REAL NOT NULL ," + // 3: lat
                "\"USERID\" TEXT," + // 4: userid
                "\"TAG\" INTEGER NOT NULL ," + // 5: tag
                "\"WKID\" TEXT," + // 6: wkid
                "\"SBH\" TEXT," + // 7: sbh
                "\"STATUS\" INTEGER NOT NULL ," + // 8: status
                "\"REMARK\" TEXT);"); // 9: REMARK
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TRACK_POINT\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TrackPoint entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getTime());
        stmt.bindDouble(3, entity.getLon());
        stmt.bindDouble(4, entity.getLat());
 
        String userid = entity.getUserid();
        if (userid != null) {
            stmt.bindString(5, userid);
        }
        stmt.bindLong(6, entity.getTag());
 
        String wkid = entity.getWkid();
        if (wkid != null) {
            stmt.bindString(7, wkid);
        }
 
        String sbh = entity.getSbh();
        if (sbh != null) {
            stmt.bindString(8, sbh);
        }
        stmt.bindLong(9, entity.getStatus());
 
        String REMARK = entity.getREMARK();
        if (REMARK != null) {
            stmt.bindString(10, REMARK);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TrackPoint entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getTime());
        stmt.bindDouble(3, entity.getLon());
        stmt.bindDouble(4, entity.getLat());
 
        String userid = entity.getUserid();
        if (userid != null) {
            stmt.bindString(5, userid);
        }
        stmt.bindLong(6, entity.getTag());
 
        String wkid = entity.getWkid();
        if (wkid != null) {
            stmt.bindString(7, wkid);
        }
 
        String sbh = entity.getSbh();
        if (sbh != null) {
            stmt.bindString(8, sbh);
        }
        stmt.bindLong(9, entity.getStatus());
 
        String REMARK = entity.getREMARK();
        if (REMARK != null) {
            stmt.bindString(10, REMARK);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public TrackPoint readEntity(Cursor cursor, int offset) {
        TrackPoint entity = new TrackPoint( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // time
            cursor.getDouble(offset + 2), // lon
            cursor.getDouble(offset + 3), // lat
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // userid
            cursor.getInt(offset + 5), // tag
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // wkid
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // sbh
            cursor.getInt(offset + 8), // status
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9) // REMARK
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TrackPoint entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTime(cursor.getString(offset + 1));
        entity.setLon(cursor.getDouble(offset + 2));
        entity.setLat(cursor.getDouble(offset + 3));
        entity.setUserid(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setTag(cursor.getInt(offset + 5));
        entity.setWkid(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setSbh(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setStatus(cursor.getInt(offset + 8));
        entity.setREMARK(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(TrackPoint entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(TrackPoint entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TrackPoint entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
