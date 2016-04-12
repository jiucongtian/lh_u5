package com.longhuapuxin.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.longhuapuxin.common.Logger;
import com.longhuapuxin.db.bean.ChatMessage;
import com.longhuapuxin.db.bean.ChatSession;
import com.longhuapuxin.db.bean.MyAddress;
import com.longhuapuxin.db.bean.User;

/**
 * @author zh
 * @date 2015-8-28
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private final static String DB_NAME = "lilydb.sqlite";
    private Map<String, Dao> daos = new HashMap<String, Dao>();
    public static final int DB_VERSION = 12;   //更新时候只需要修改这里就可以了
    private static DatabaseHelper instance;


    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Logger.debug("DatabaseHelper->onUpgrade");
    }

    @Override
    public void onCreate(SQLiteDatabase database,
                         ConnectionSource connectionSource) {
        Logger.debug("DatabaseHelper->onCreate");
        try {
            TableUtils.createTable(connectionSource, User.class);
            TableUtils.createTable(connectionSource, ChatMessage.class);
            TableUtils.createTable(connectionSource, ChatSession.class);
            TableUtils.createTable(connectionSource, MyAddress.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          ConnectionSource connectionSource, int oldVersion, int newVersion) {
        Logger.debug("DatabaseHelper->onUpgrade");
        try {
            TableUtils.dropTable(connectionSource, User.class, true);
            TableUtils.dropTable(connectionSource, ChatMessage.class, true);
            TableUtils.dropTable(connectionSource, ChatSession.class, true);
            TableUtils.dropTable(connectionSource, MyAddress.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Singleton to get helper instance.
     */
    public static synchronized DatabaseHelper getHelper(Context context) {
        context = context.getApplicationContext();
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null)
                    instance = new DatabaseHelper(context);
            }
        }

        return instance;
    }

    public synchronized Dao getDao(Class clazz) throws SQLException {
        Dao dao = null;
        String className = clazz.getSimpleName();

        if (daos.containsKey(className)) {
            dao = daos.get(className);
        }
        if (dao == null) {
            dao = super.getDao(clazz);
            daos.put(className, dao);
        }
        return dao;
    }

    /**
     * Release resource.
     */
    @Override
    public void close() {
        super.close();
        Logger.debug("DatabaseHelper->close");

        for (String key : daos.keySet()) {
            Dao dao = daos.get(key);
            dao = null;
        }
    }
}
