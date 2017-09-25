package orgs.androidtown.androidmemoorm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import orgs.androidtown.androidmemoorm.util.FileUtil;

/**
 * 상세보기 처리
 */
public class DetailActivity extends AppCompatActivity {

    private String filename;
    private String title;

    private ImageView imageView;
    private TextView textTitle;

    Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageView = (ImageView) findViewById(R.id.imageView);
        textTitle = (TextView) findViewById(R.id.textTitle);

        // 1. 리스트에서 넘어온 인텐트를 꺼내고
        Intent intent = getIntent();
        // 2. 인텐트에서 값을 꺼내서 담는다
        filename = intent.getStringExtra("filename");
        title = intent.getStringExtra("title");

        // 3. 값을 위젯에 담는다
        textTitle.setText(title);
        // 이미지를 화면에 뿌리기 위해서 파일명으로 Bitmap을 읽어온다.
        try {
            bitmap = FileUtil.read(this, filename);
            imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        if(bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
        super.onDestroy();
    }
}
