package cc.haoduoyu.umaru.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.apkfuns.logutils.LogUtils;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import cc.haoduoyu.umaru.model.City;

/**
 * 数据库帮助类
 * Created by XP on 2016/1/19.
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = "DBHelper";
    private static final String TABLE_NAME = "orm-one.db";
    private static final int TABLE_VERSION = 1;
    private static DBHelper instance;
    private Context mContext;

    private Map<String, Dao> daos = new HashMap<String, Dao>();
    private Dao<City, Integer> cityDao;

    private DBHelper(Context context) {
        super(context, TABLE_NAME, null, TABLE_VERSION);
        mContext = context;
    }


    /**
     * 获取helper
     * 整个DBHelper使用单例只对外公布出一个对象，保证app中只存在一个SQLite Connection
     *
     * @param context
     * @return
     */
    public static synchronized DBHelper getHelper(Context context) {
        if (instance == null) {
            synchronized (DBHelper.class) {
                if (instance == null)
                    instance = new DBHelper(context);
            }
        }

        return instance;
    }

    /**
     * 获取dao
     *
     * @return
     * @throws SQLException
     */
    @Override
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
     * 获得cityDao
     *
     * @return
     * @throws SQLException
     */
    public Dao<City, Integer> getUserDao() throws SQLException {
        if (cityDao == null) {
            cityDao = super.getDao(City.class);
        }
        return cityDao;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, City.class);
            //...others
//            executeAssetsSQL(sqLiteDatabase, "city.sql");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try {
            TableUtils.dropTable(connectionSource, City.class, true);
            //...others
            // after we drop the old databases, we create the new ones
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 直接执行sql脚本
     *
     * @param db
     * @param schemaName
     */
    private void executeAssetsSQL(SQLiteDatabase db, String schemaName) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(mContext.getAssets().open(schemaName)));
            LogUtils.d("path:" + schemaName);
            String line;
            String buffer = "";
            while ((line = in.readLine()) != null) {
                buffer += line;
                if (line.trim().endsWith(";")) {
                    db.execSQL(buffer.replace(";", ""));
                    buffer = "";
                }
            }
        } catch (IOException e) {
            LogUtils.e(e.toString());
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                LogUtils.e( e.toString());
            }
        }
    }

    /**
     * 清空表
     */
    public void clearTable(Class dataClass) {
        try {
            TableUtils.clearTable(connectionSource, dataClass);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();
        for (String key : daos.keySet()) {
            Dao dao = daos.get(key);
            dao = null;
        }
    }


}