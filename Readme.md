# ADS04 Android

## 수업 내용
- recyclerView를 학습
- ORM library를 학습

## Code Review

### MainActivity

```Java

public class MainActivity extends AppCompatActivity {
    CustomAdapter adapter;

    // 0. 권한 요청코드
    private static final int REQ_CODE = 999;
    // 1. 권한 정의
    private String permissions[] = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
        if(adapter != null){
            PicNoteDAO dao = new PicNoteDAO(this);
            List<PicNote> data = dao.readAll();
            //* 3. 재정의한 아답터를 생성하면서 데이터를 담는다
            adapter.setData(data);
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "notifyDataSetChanged", Toast.LENGTH_SHORT).show();

        }
    }

    PermissionUtil pUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pUtil = new PermissionUtil(REQ_CODE, permissions);
        if(pUtil.checkPermission(this)){
            init();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(pUtil.afterPermissionResult(requestCode, grantResults)){
            init();
        }else{
            Toast.makeText(this, "승인 하셔야지만 앱을 실행할 수 있습니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
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
        PicNoteDAO dao = new PicNoteDAO(this);
        //* 1. 데이터를 정의
        // DB에 테스트 데이터 넣기
//        for(int i=0; i<1000 ; i++){
//            PicNote picNote = new PicNote();
//            picNote.setTitle("안녕하세요 "+i);
//            picNote.setDatetime(System.currentTimeMillis());
//            // db 에다가 넣은후
//            dao.create(picNote);
//        }
        // db에서 읽어온다.
        List<PicNote> data = dao.readAll();

        //* 3. 재정의한 아답터를 생성하면서 데이터를 담는다
        adapter = new CustomAdapter();
        adapter.setData(data);
        //* 4. 아답터와 RecyclerView 컨테이너를 연결
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recylerView);
        recyclerView.setAdapter(adapter);
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
```

### CustomAdapter 

```Java
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.Holder> {
    // 1. 데이터 저장소
    private List<PicNote> data;

    public void setData(List<PicNote> data) {
        this.data = data;
    } // 데이터를 set해준다.

    // 2. 개수
    @Override
    public int getItemCount() { // 목록의 전체 길이를 결정
        return data.size();
    }

    // 3. 홀더 생성
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) { // 리스트뷰와 달리 생성과 관리가 나뉘어져있고 이곳은 생성부분
        // 1. 만들어둔 layout 파일을 inflate 한다
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);
        // 2. inflate 된 View 를 Holder의 생성자에 담는다
        Holder holder = new Holder(view);
        // 3. 생성된 Holder를 리턴한다.
        return holder;
    }

    // 4. 홀더 사용
    @Override
    public void onBindViewHolder(Holder holder, int position) {
        // 1. 데이터저장소에 객체단위로 꺼내둔다
        PicNote picNote = data.get(position);
        // 2. 홀더에 있는 위젯에 값을 입력한다.
        holder.setTitle(picNote.getTitle());
        holder.setFilename(picNote.getBitmap()); // filename은  스트링값
    }

    // 0. 홀더 만들기
    public class Holder extends RecyclerView.ViewHolder {
        private String filename;
        private TextView textTitle;

        public Holder(View itemView) {
            super(itemView); // 아이템뷰를 상속받는다.
            textTitle = itemView.findViewById(R.id.textTitle);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), DetailActivity.class);
                    intent.putExtra("title", textTitle.getText());
                    intent.putExtra("filename", filename);

                    view.getContext().startActivity(intent);
                }
            });
        }

        public void setTitle(String title) {
            textTitle.setText(title);
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }
    }
}
```

### DrawActivity

```Java
public class DrawActivity extends AppCompatActivity {

    FrameLayout stage;
    RadioGroup radioColor;
    DrawView draw;
    PicNoteDAO dao;
    EditText editTitle;
    SeekBar seekBarSize;
    int size;
    int color;

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
        seekBarSize = (SeekBar) findViewById(R.id.seekBar_size);

        draw = new DrawView(this);
        stage.addView(draw);


        radioColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) { // 그룹안에 있는 아이디를 찾아오게 하는 파라미터
                size = seekBarSize.getProgress();
                color = Color.BLACK;
                Log.e("size", "size" + size);

                switch (checkedId) {// 인자값으로 넘어온 값을 통해 구분 가능
                    case R.id.radioButton_black:

                        color = Color.BLACK;
                        break;
                    case R.id.radioButton_cyan:
                        color = Color.CYAN;
                        break;
                    case R.id.radioButton_magenta:
                        color = Color.MAGENTA;
                        break;
                    case R.id.radioButton_yellow:
                        color = Color.YELLOW;
                        break;
                }
                draw.setColor(color, size);
            }

        });
        seekBarSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                draw.setSize(progress);
                Log.e("seekBarsize1", "progress" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
        String filename = System.currentTimeMillis() + ".jpg"; // null로 초기화?
        // -----------------파일명 중복검사------------------------
        // 1. 현재 파일명을 풀 경로로 File 객체로 변환
        String dir = getFilesDir().getAbsolutePath();
        File file = new File(dir + "/" + filename);
        int count = 0;
        while (file.exists()) {
            count++;
            filename = System.currentTimeMillis() + "(" + count + ").jpg";
            file = new File(dir + "/" + filename);
        }
        // ------------------------------------------------------------

        try {
            // /data/data/패키지/files 밑에....
            FileUtil.write(this, filename, bitmap); //FileUtil에 있는 파라이터가 3개이고 해당하는 타입에 맞게 넣어준것!
        } catch (IOException e) {
            e.printStackTrace();
        }
        //데이터베이스에 경로도 저장하고
        PicNote picNote = new PicNote();
        picNote.setBitmap(filename); //비트맵의 경로를 저장
        picNote.setTitle(editTitle.getText().toString());

        picNote.setDatetime(System.currentTimeMillis());
        dao.create(picNote); //이렇게 저장되어 있는 것을 dao라는 데이터설계 클래스로 통째로 보내준다.
        //제일 중요한 것.....
        bitmap.recycle(); //native에 다 썼다고 알려준다.

        finish();
    }
}
```

### DetailActivity

```Java
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
```

### DrawView

```Java
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
```

### Dbhelper

```Java
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
```

### Util성 Class

- FileUtil

```java
public class FileUtil {

    // 일반적으로는 파일을 저장할 경로는 설정파일에 입력해둔다.
    private static final String DIR = "/temp/picnote";

    public static Bitmap read(Context context, String filename) throws IOException {
        Bitmap bitmap = null;
        FileInputStream fis = null;
        try {
            // 1. 파일 저장을 위한 디렉토리를 정한다
            // 2. 체크해서 없으면 생성
            File file = new File(DIR);
            if(!file.exists()){
                file.mkdirs();
            }
            // 3. 해당 디렉토리에 파일 쓰기
            String ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
            fis = new FileInputStream(new File(ROOT+DIR+"/"+filename));
//            fis = context.openFileInput(DIR + "/" + filename); //           /aaa.jpg
            // 스트림을 Bitmap으로 변환
            bitmap = BitmapFactory.decodeStream(fis);

        } catch (IOException e) {
            throw e;
        } finally {
            if(fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    throw e;
                }
            }
        }
        return bitmap;
    }


    /**
     * 파일 쓰기 함수
     * @param context 컨텍스트
     * @param filename 파일이름
     * @param content 내용
     * @throws IOException
     */
    public static void write(Context context, String filename, Bitmap content)
            throws IOException {
        FileOutputStream fos = null;
        //파일 읽고쓰기가 가능한 외부저장소의 root 경로
        String ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
//        Log.e("FileUtil", "ROOT:"+ROOT);
//        String realRoot = Environment.getRootDirectory().getAbsolutePath();
//        Log.e("FileUtil", "realRoot:"+realRoot);
//        String data = Environment.getDataDirectory().getAbsolutePath();
//        Log.e("FileUtil", "data:"+data);
        try {
            // 1. 파일 저장을 위한 디렉토리를 정한다
            // 2. 체크해서 없으면 생성
            File dir = new File(ROOT+"/"+DIR);
            if(!dir.exists()){
                dir.mkdirs();
            }
            // 3. 해당 디렉토리에 파일 쓰기
            //    파일이 있는지 검사
            File file = new File(ROOT+"/"+ DIR +"/"+ filename);
            if(!file.exists()){
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            fos.write(bitmapToByteArray(content));
        }catch(Exception e){
            throw e;
        }finally {
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    throw e;
                }
            }
        }
    }

    private static byte[] bitmapToByteArray( Bitmap bitmap ) {
        byte[] byteArray = null;
        try {
            if(bitmap != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byteArray = stream.toByteArray();
                stream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }
}
```

- PermissionUtil

```java
public class PermissionUtil {

    private int req_code;
    private String permissions[];

    public PermissionUtil(int req_code, String permissions[]){
        this.req_code = req_code;
        this.permissions = permissions;
    }

    public boolean checkPermission(Activity activity){
        // 2. 버전 체크 후 권한 요청
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return requestPermission(activity);
        }else{
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean requestPermission(Activity activity){
        // 3. 권한에 대한 승인 여부
        List<String> requires = new ArrayList<>();
        for(String permission : permissions){
            if(activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED){
                requires.add(permission);
            }
        }
        // 4.승인이 안된 권한이 있을 경우만 승인 요청
        if(requires.size() > 0){
            String perms[] = requires.toArray(new String[requires.size()]);
            activity.requestPermissions(perms, req_code);
            return false;
        }else {
            return true;
        }
    }

    public boolean afterPermissionResult(int requestCode, int grantResults[]){
        if(requestCode == req_code){
            boolean granted = true;
            for(int grant : grantResults){
                if(grant != PackageManager.PERMISSION_GRANTED){
                    granted = false;
                    break;
                }
            }
            if(granted){
                return true;
            }
        }
        return false;
    }
}
```

### model 

```Java
/**
 * 데이터 모델링 - 도메인 추출 - 개념모델링
 */

@DatabaseTable(tableName = "picnote") //대소문자 지정
public class PicNote { // 이 클래스의 목적은 데이터를 데이터 베이스와 연결하는 목적!?!
    // 식별자
    @DatabaseField(generatedId = true)
    private long id;
    // 제목
    @DatabaseField
    private String title;
    // 그림
    @DatabaseField
    private String bitmap_path;
    // 날짜
    @DatabaseField
    private long datetime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBitmap() {
        return bitmap_path;
    }

    public void setBitmap(String bitmap_path) {
        this.bitmap_path = bitmap_path;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }
}

```

### dao


```Java
public class PicNoteDAO { // 이 클래스의 목적은 데이터베이스를 설계!
    DBHelper helper;
    Dao<PicNote, Long> dao = null;

    public PicNoteDAO(Context context){ // 이 클래스가 실행이 되면,
        helper = new DBHelper(context); // 헬퍼객체가 생성됨.
        try {
            dao = helper.getDao(PicNote.class);  //dao가 PicNote.class를 분석하는 역할
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
```

## 보충설명

- 커스텀 뷰는 [Android_CustomView](https://github.com/youjisang/ADS_Android_CustomView.git) 참고

### RecyclerView와 ORM

### RecyclerView란?

RecyclerView는 커다란 data set을 제한된 window 공간에서 유연하게 표현할 수 있는 뷰

#### 주요클래스

- Adapter – 기존의 ListView에서 사용하는 Adapter와 같은 개념으로 데이터와 아이템에 대한 View생성
- ViewHolder – 재활용 View에 대한 모든 서브 뷰를 보유
- LayoutManager – 아이템의 항목을 배치
- ItemDecoration – 아이템 항목에서 서브뷰에 대한 처리
- ItemAnimation – 아이템 항목이 추가, 제거되거나 정렬될때 애니메이션 처리

#### 특징

#### Adapter클래스 내 메소드
- onCreateViewHolder(ViewGroup parent, int viewType)  : 뷰 홀더를 생성하고 뷰를 붙여주는 부분
- onBindViewHolder(ListItemViewHolder holder, int position) : 재활용 되는 뷰가 호출하여 실행되는 메소드, 뷰 홀더를 전달하고 어댑터는 position 의 데이터를 결합
- getItemCount( ) : 데이터의 개수 반환

```Java
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.Holder>{
    // 1. 데이터 저장소
    private List<PicNote> data;

    public void setData(List<PicNote> data){ //만약에 setData메소드가 호출되고,List<PicNote> data로 들어온 것을
        this.data = data;// 여기 데이터를 사용해라~라는 의미
    }

    // 2. 개수
    @Override
    public int getItemCount() { // 목록의 전체 길이를 결정
        return data.size();
    }
    // 3. 홀더 생성
    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 1. 만들어둔 layout 파일을 inflate 한다
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);
        // 2. inflate 된 View 를 Holder의 생성자에 담는다
        Holder holder = new Holder(view);
        // 3. 생성된 Holder를 리턴한다.
        return holder;
    }
    // 4. 홀더 사용
    @Override
    public void onBindViewHolder(Holder holder, int position) {
        // 1. 데이터저장소에 객체단위로 꺼내둔다
        PicNote picNote = data.get(position); // 포지션으로 꺼냄
        // 2. 홀더에 있는 위젯에 값을 입력한다.
        holder.setTitle(picNote.getTitle());
    }
```


#### LayoutManager
- LinearLayoutManager : 리사이클러 뷰에서 가장 많이 쓰이는 레이아웃으로 수평, 수직 스크롤을 제공하는 리스트를 만들 수 있음.
- StaggeredGridLayouManager : 이 레이아웃을 통해 뷰마다 크기가 다른 레이아웃을 만들 수 있음(ex)Pinterest
- GridLayoutManager : 여러분의 사진첩 같은 격자형 리스트를 만들 수 있음.

 ```Java
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
 ```

#### ViewHolder 패턴

- UI 를 수정할 때 마다 부르는 findViewById( ) 를 뷰홀더 패턴을 이용해 한번만 함으로서 리스트 뷰의 지연을 초래하는 무거운 연산을 줄여줌, 그러나 그 차이는 미세!

```Java
public class Holder extends RecyclerView.ViewHolder{
        private TextView textTitle;
        public Holder(View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
        }
        public void setTitle(String title){
            textTitle.setText(title);
        }
    }
```

### ORM이란?

객체와 관계형 table 사이의 mapping을 처리해 주는 것을 Object Relational Mapping ( ORM )

#### 기능
 - ORM 은 객체를 table에 저장해 주고, table에 저장된 data로 부터 객체를 생성해 주는 기능을 제공
 - mapping 정보를 변경할 수 있도록 해야 함
- class 상속, 객체 사이의 연관 등을 객체 modeling을 지원
- class 와 class의 property를 사용하여 객체를 조회
- 객체 값 변경을 자동으로 database에 반영

#### 장점
- 생산성 향상
SQL query를 작성하고 그 query 실행 결과로 부터 객체를 생성하는 코드를 작성하는 시간 감소.
- 유지보수 용이
- 코드가 business logic 위주로 작성되기 때문에 이해도 상승
- 리펙토링 용이
- 특정 DBMS에 종속적이지 않음

#### 단점
- DAO 패턴에 익숙한 개발자에게는 초반 접금이 어려움
- 객체 지향적으로 class 설계하는 것은 쉽지 않음.
- ORM 을 잘못 사용할 경우 성능을 저하.

### 출처

- 출처: https://github.com/mnisdh/Android/tree/master/android/AndroidMemoORM/app/src/main/java/android/daehoshin/com/androidmemoorm

## TODO

- Library 내부적으로 어떻게 작동하는지 연구
- model, Dao 등 원리 이해하기
- 객체 생성, 생성자, 메소드, 위젯 등 상호간에 로직 지속적인 테스트

## Retrospect

- 커스터마이징 필요함.


## Output

![memoorm](https://user-images.githubusercontent.com/31605792/34721575-896acf5e-f586-11e7-8e7c-dd5f0daeda7a.gif)


