package hanlonglin.com.videoapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_PERMISSIONS = 1010;
    String[] pers = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("TAG","MainActivity进入");
        requestPers();
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_video1:
                go(Video1Activity.class);
                break;
        }
    }

    private void go(Class clazz) {
        startActivity(new Intent(MainActivity.this, clazz));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_PERMISSIONS){
           for(int ret:grantResults){
               if(ret!=PackageManager.PERMISSION_GRANTED){
                   Log.e("TAG","MainActivity退出");
                   finish();
               }
           }
        }
    }

    private boolean requestPers() {
        List<String> perList = new ArrayList<>();
        for (String per : pers) {
            if (ContextCompat.checkSelfPermission(this, per) != PackageManager.PERMISSION_GRANTED) {
                perList.add(per);
            }
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (perList.size() > 0) {
                requestPermissions(perList.toArray(new String[perList.size()]), REQUEST_PERMISSIONS);
                return false; //需要请求动态权限
            } else {
                return true;  //无需请求
            }
        } else {
            return true; //低于5.0 无需请求
        }
    }


}
