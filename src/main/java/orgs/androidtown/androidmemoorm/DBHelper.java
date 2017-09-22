package orgs.androidtown.androidmemoorm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import orgs.androidtown.androidmemoorm.model.PicNote;

/**
 * Created by Jisang on 2017-09-22.
 */

public class DBHelper extends OrmLiteSqliteOpenHelper {// Library에서 받아온 것을 상속받았음.
    private static final String DB_NAME = "ormlite.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            // PicNote.class를 참조해서 테이블을 생성해준다
            TableUtils.createTable(connectionSource, PicNote.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }
}
