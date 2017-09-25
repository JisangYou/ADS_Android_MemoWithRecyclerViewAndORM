package orgs.androidtown.androidmemoorm;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import java.io.File;
import java.io.IOException;

import orgs.androidtown.androidmemoorm.dao.PicNoteDAO;
import orgs.androidtown.androidmemoorm.model.PicNote;
import orgs.androidtown.androidmemoorm.util.FileUtil;

public class DrawActivity extends AppCompatActivity {

    FrameLayout stage;
    RadioGroup radioColor;
    DrawView draw;
    PicNoteDAO dao;
    EditText editTitle ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        dbInit();
        init();


    }

    private void dbInit() { //dbInit을 호출하면
        dao = new PicNoteDAO(this);// PricNoteDAO클래스의 객체가 생성됨. this의 구체적인 의미?
    }

    private void init() {
        //라디오 버튼이 선택되면 draw의 paint 색상을 바꿔준다.
        radioColor = (RadioGroup) findViewById(R.id.radioColor);
        editTitle = (EditText) findViewById(R.id.editTitle);
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

    public void captureCanvas(View view) { // 자체 온클릭인가?

        //0. 드로잉 캐시를 먼저 지워준다.
        stage.destroyDrawingCache();
        // 1. 다시만든다.
        stage.buildDrawingCache();
        //2. 레이아웃에서 그려진 내용을 bitmap형태로 가져온다.
        Bitmap bitmap = stage.getDrawingCache();
        //이미지 이름을 생성

        // 이미지 파일을 저장하고
        String filename = System.currentTimeMillis()+ ".jpg"; // null로 초기화?
        // -----------------파일명 중복검사------------------------
        // 1. 현재 파일명을 풀 경로로 File 객체로 변환
        String dir = getFilesDir().getAbsolutePath();
        File file = new File(dir + "/" +filename);
        int count = 0;
        while(file.exists()){
            count++;
            filename= System.currentTimeMillis()+"("+count+").jpg";
            file = new File(dir + "/" +filename);
        }
        // ------------------------------------------------------------

        try {
            // /data/data/패키지/files 밑에....
            FileUtil.write(this, filename, bitmap); //FileUtil에 있는 파라이터가 3개이고 해당하는 타입에 맞게 넣어준것!
        } catch (IOException e) {
            e.printStackTrace();
        }
        //데이터베이스에 경로도 저장하고
        PicNote picNote = new PicNote(); // 왜 여기에는 this가 안들어가지?
        picNote.setBitmap(filename); //비트맵의 경로를 저장
       picNote.setTitle(editTitle.getText().toString());

        picNote.setDatetime(System.currentTimeMillis());
        dao.create(picNote); //이렇게 저장되어 있는 것을 dao라는 데이터설계 클래스로 통째로 보내준다.
        //제일 중요한 것.....
        bitmap.recycle(); //native에 다 썼다고 알려준다.

        finish();
    }
}
