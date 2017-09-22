package orgs.androidtown.androidmemoorm.dao;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;

import java.sql.SQLException;
import java.util.List;

import orgs.androidtown.androidmemoorm.DBHelper;
import orgs.androidtown.androidmemoorm.model.PicNote;

/**
 * Created by Jisang on 2017-09-22.
 */

public class PicNoteDAO {
    DBHelper helper;
    Dao<PicNote, Long> dao = null;

    public PicNoteDAO(Context context){ // 이 클래스가 실행이 되면,
        helper = new DBHelper(context); // 헬퍼객체가 생성됨.
        try {
            dao = helper.getDao(PicNote.class);  //PicNote클래스객체를 헬퍼가 불러와 dao에 담는다.
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // 생성
    public void create(PicNote picNote){
        try {
            dao.create(picNote);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PicNote> readAll(/*쿼리를 할 수 있는 조건*/){
        List<PicNote> result = null;
        try {
            result = dao.queryForAll(); // 쿼리에 있는 내용을 모두 불러온다
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public PicNote readOneById(long id){
        PicNote result = null;
        try {
            result = dao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<PicNote> search(String word){ // 그림

        String query = String.format("select * from picnote where title like '%%%s%%'",word);
        // select * from picnote where title like '%그림%'
        // 그림판입니다, 좋은그림입니다, 그림좋아요
        List<PicNote> result = null;
        try {
            GenericRawResults<PicNote> temp = dao.queryRaw(query, dao.getRawRowMapper());
            result = temp.getResults();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;

    }

    public void update(PicNote picNote){
        try {
            dao.update(picNote);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(PicNote picNote){
        try {
            dao.delete(picNote);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
