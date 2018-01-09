package orgs.androidtown.androidmemoorm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jisang on 2017-09-18.
 */

public class DrawView extends View {
    Paint paint;
    PathTool currentPath;

    List<PathTool> paths = new ArrayList<>();

    // 그림이 그려지는 좌표

//    // 원의 크기
//    float r = 10;
//
//    // 좌표 값을 저장하는 저장소
//    ArrayList<Float> xs = new ArrayList<>();
//    ArrayList<Float> ys = new ArrayList<>();

    // 소스코드에서 사용하기 때문에 생성자 파라미터는 context만 필요
    public DrawView(Context context) {
        super(context);
        paint = new Paint();
        init();


    }

    private void init() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5F);
        setColor(Color.BLACK,1);
    }

    public void setColor(int color, int size) {

        PathTool tool = new PathTool(color, size);
        currentPath = tool;
        paths.add(tool);
    }

    public void setSize(int size){
        setColor(currentPath.getColor(), size);
    }


    //화면을 그려주는 onDraw 오버라이드



    @Override
    public boolean onTouchEvent(MotionEvent event) {


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 이전점과 현재점 사이를 그리지 않는다.
                currentPath.moveTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                //이전점과 현재점 사이를 그린다.
                currentPath.lineTo(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                //nothing to do
                break;
        }

        //터치가 일어나면 좌표값을 세팅해준다.

        //터치가 일어나면 패스를 해당 좌표로 이동한다.
        currentPath.lineTo(event.getX(), event.getY());

        // onDraw를 호출하는 메서드를 호출
        invalidate(); // <- 다른 언어에서는 대부분 그림을 그려주는 함수를 호출하는 메서드는
        // 기존 그림을 유지하는데, 안드로이드는 지운다.

        // 리턴이 false일 경우는 touch 이벤트를 연속해서 발생시키지 않는다.
        // 즉, 드래그를 하면, ontouchEvent가 재 호출되지 않는다.
        return true;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        // 1. 화면에 터치가 되면.
//        // 2.연속해서 그림을 그려준다.
//        //2.1. 터치된 좌표에 작은 동그라미를 그려준다.
//        if (xs.size()>0 ) {
//
//            for (int i = 0; i < xs.size(); i++) {
//                canvas.drawCircle(xs.get(i), ys.get(i), r, paint);
//            }
//        }
        for (PathTool tool : paths) {
            paint.setColor(tool.getColor());
            paint.setStrokeWidth(tool.getSize());
            canvas.drawPath(tool, paint);
        }
    }


}

class PathTool extends Path {
    private int color;
    private float size;

    public PathTool(int color, float size) {
        this.color = color;
        this.size = size;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public void setColor(int color) {
        this.color = color;

    }

    public int getColor() {
        return this.color;
    }
}