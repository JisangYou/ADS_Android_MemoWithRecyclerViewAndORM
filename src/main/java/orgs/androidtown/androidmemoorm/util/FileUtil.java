package orgs.androidtown.androidmemoorm.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by pc on 9/20/2017.
 */

public class FileUtil {

    public static Bitmap read(Context context, String filename) throws IOException {
        Bitmap bitmap = null;
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(filename);
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
        try {
            fos = context.openFileOutput(filename, MODE_PRIVATE);
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
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
}
