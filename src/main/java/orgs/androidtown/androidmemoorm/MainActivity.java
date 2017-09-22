package orgs.androidtown.androidmemoorm;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import orgs.androidtown.androidmemoorm.dao.PicNoteDAO;
import orgs.androidtown.androidmemoorm.model.PicNote;

public class MainActivity extends AppCompatActivity {

    /**
     * RecyclerView 를 사용한 목록 만들기
     *
     * 0. 화면만들기
     *
     * 1. 데이터를 정의
     *
     * 2. 아답터를 재정의
     *
     * 3. 재정의한 아답터를 생성하면서 데이터를 담는다
     *
     * 4. 아답터와 RecyclerView 컨테이너를 연결
     *
     * 5. RecyclerView 에 레이아웃매니저를 성정
     *
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    /**
     * 레이아웃에서 함수를 직접 호출한다
     * @param view
     */
    public void openDraw(View view){
        Intent intent = new Intent(this, DrawActivity.class);
        startActivity(intent);
    }

    private void init(){
        PicNoteDAO dao = new PicNoteDAO(this); // this? 의미는? 여기 액티비티의 콘텍스틑 사용한다는 것인가?

        List<PicNote> data = dao.readAll();  // dao클래스의 readAll()메소드를 PicNote타입의 List에 data변수에 답는다.

        //* 3. 재정의한 아답터를 생성하면서 데이터를 담는다
        CustomAdapter adapter = new CustomAdapter(); // 아답터 생성
        adapter.setData(data); // 아답터에 데이터를 뿌려준다.
        //* 4. 아답터와 RecyclerView 컨테이너를 연결
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recylerView);
        recyclerView.setAdapter(adapter); // 아답터 장착

        //* 5. RecyclerView 에 레이아웃매니저를 설정
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 레이아웃 매니저 종류
        /*
        1. LinearLayoutManager
           - 리사이클러 뷰에서 가장 많이 쓰이는 레이아웃으로 수평, 수직 스크롤을 제공하는 리스트를 만들 수 있다.
        2. StaggeredGridLayoutManager
           - 이 레이아웃을 통해 뷰마다 크기가 다른 레이아웃을 만들 수 있다. 마치 Pinterest 같은 레이아웃 구성가능.
        3. GridLayoutManager
           - 갤러리(GridView) 같은 격자형 리스트를 만들 수 있습니다.
        - 사용예시// StaggeredGrid 레이아웃을 사용한다
            RecyclerView.LayoutManager lm
                = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
            lm = new LinearLayoutManager(this);
            lm = new GridLayoutManager(this,3);
        */
    }
    }