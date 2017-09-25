package orgs.androidtown.androidmemoorm.util;

/**
 * Created by Jisang on 2017-09-25.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 9/25/2017.
 */

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