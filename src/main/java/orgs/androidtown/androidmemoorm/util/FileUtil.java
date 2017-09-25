package orgs.androidtown.androidmemoorm.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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