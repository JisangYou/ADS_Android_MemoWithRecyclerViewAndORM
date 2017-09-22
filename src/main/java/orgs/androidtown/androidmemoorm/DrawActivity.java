package orgs.androidtown.androidmemoorm;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import java.io.IOException;

import orgs.androidtown.androidmemoorm.dao.PicNoteDAO;
import orgs.androidtown.androidmemoorm.model.PicNote;
import orgs.androidtown.androidmemoorm.util.FileUtil;

public class DrawActivity extends AppCompatActivity {

    FrameLayout stage;
    RadioGroup radioColor;
    DrawView draw;
    PicNoteDAO dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);

        init();
        dbInit();

    }

    private void dbInit() { //dbInit을 호출하면
        dao = new PicNoteDAO(this);// PricNoteDAO클래스의 객체가 생성됨. this의 구체적인 의미?
    }

    private void init() {
        //라디오 버튼이 선택되면 draw의 paint 색상을 바꿔준다.
        radioColor = (RadioGroup) findViewById(R.id.radioColor);

        stage = (FrameLayout) findViewById(R.id.stage);
        draw = new DrawView(this);
        stage.addView(draw);


        radioColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) { // 그룹안에 있는 아이디를 찾아오게 하는 파라미터
                switch (checkedId) {// 인자값으로 넘어온 값을 통해 구분 가능
                    case R.id.radioButton_black:

                        draw.setColor(Color.BLACK);
                        break;
                    case R.id.radioButton_cyan:
                        draw.setColor(Color.CYAN);
                        break;
                    case R.id.radioButton_magenta:
                        draw.setColor(Color.MAGENTA);
                        break;
                    case R.id.radioButton_yellow:
                        draw.setColor(Color.YELLOW);
                        break;
                }
            }
        });
    }

    /**
     * 그림을 그린 stage를 캡쳐
     *
     * @param view
     */

    public void captureCanvas(View view) {

        //0. 드로잉 캐시를 먼저 지워준다.
        stage.destroyDrawingCache();
        // 1. 다시만든다.
        stage.buildDrawingCache();
        //2. 레이아웃에서 그려진 내용을 bitmap형태로 가져온다.
        Bitmap bitmap = stage.getDrawingCache();

        // 이미지 파일을 저장하고
        String filename = ""; // null로 초기화?
        try {
            // /data/data/패키지/files 밑에....
            FileUtil.write(this, filename, bitmap); //파일에 bitmap형식으로 들어온 것을 넣는다?
        } catch (IOException e) {
            e.printStackTrace();
        }
        //데이터베이스에 경로도 저장하고
        PicNote picNote = new PicNote(); // 왜 여기에는 this가 안들어가지?
        picNote.setBitmap(filename); //비트맵의 경로를 저장
        picNote.setTitle(filename);
        picNote.setDatetime(System.currentTimeMillis());
        dao.create(picNote);
        //제일 중요한 것.....
        bitmap.recycle(); //native에 다 썼다고 알려준다.

        finish();
    }
}
