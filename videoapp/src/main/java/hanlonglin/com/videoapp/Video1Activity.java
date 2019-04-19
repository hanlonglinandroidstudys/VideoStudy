package hanlonglin.com.videoapp;

;
import android.hardware.Camera;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.IOException;

import hanlonglin.com.videoapp.view.CircleShape;

public class Video1Activity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private static final String TAG = "Video1Activity";
    SurfaceView surfaceView;
    RadioGroup radioGroup;
    CircleShape img_take;
    private boolean isStart=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video1);
        initView();
    }

    private void initView() {
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        img_take = (CircleShape) findViewById(R.id.img_take);
        initSurfaceView();

        img_take.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startREC();
                        break;
                    case MotionEvent.ACTION_UP:
                        stopREC();
                        break;
                }
                return false;
            }
        });
    }

    //结束录制
    private void stopREC() {
        Log.e(TAG,"结束录制");
        isStart = false;
    }

    //开始录制
    private void startREC() {
        Log.e(TAG,"开始录制");
        isStart = true;
    }

    private void initSurfaceView() {
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(this);
    }

    private Camera camera;


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG, "surfaceCreated()");
        try {
            camera = Camera.open();
            camera.setPreviewCallback(this);
            if (camera != null) {
                camera.setPreviewDisplay(surfaceView.getHolder());
                camera.startPreview();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "相机出错");
            Toast.makeText(this, "相机出错！" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e(TAG, "surfaceChanged()");
        //重启摄像头预览

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG, "surfaceDestroyed()");

        //释放资源
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.e(TAG, "onPreviewFrame()");
        if(isStart)
            Log.e(TAG, "正在录制。。。");
    }
}
