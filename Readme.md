# RecyclerView와 ORM
## RecyclerView란?

RecyclerView는 커다란 data set을 제한된 window 공간에서 유연하게 표현할 수 있는 뷰

### 주요클래스

- Adapter – 기존의 ListView에서 사용하는 Adapter와 같은 개념으로 데이터와 아이템에 대한 View생성
- ViewHolder – 재활용 View에 대한 모든 서브 뷰를 보유
- LayoutManager – 아이템의 항목을 배치
- ItemDecoration – 아이템 항목에서 서브뷰에 대한 처리
- ItemAnimation – 아이템 항목이 추가, 제거되거나 정렬될때 애니메이션 처리

### 특징

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

. UI 를 수정할 때 마다 부르는 findViewById( ) 를 뷰홀더 패턴을 이용해 한번만 함으로서 리스트 뷰의 지연을 초래하는 무거운 연산을 줄여줌, 그러나 그 차이는 미세!

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

# ORM이란?

객체와 관계형 table 사이의 mapping을 처리해 주는 것을 Object Relational Mapping ( ORM )

## 기능
 - ORM 은 객체를 table에 저장해 주고, table에 저장된 data로 부터 객체를 생성해 주는 기능을 제공
 - mapping 정보를 변경할 수 있도록 해야 함
- class 상속, 객체 사이의 연관 등을 객체 modeling을 지원
- class 와 class의 property를 사용하여 객체를 조회
- 객체 값 변경을 자동으로 database에 반영

## 장점
- 생산성 향상
SQL query를 작성하고 그 query 실행 결과로 부터 객체를 생성하는 코드를 작성하는 시간 감소.
- 유지보수 용이
- 코드가 business logic 위주로 작성되기 때문에 이해도 상승
- 리펙토링 용이
- 특정 DBMS에 종속적이지 않음

## 단점
- DAO 패턴에 익숙한 개발자에게는 초반 접금이 어려움
- 객체 지향적으로 class 설계하는 것은 쉽지 않음.
- ORM 을 잘못 사용할 경우 성능을 저하.
