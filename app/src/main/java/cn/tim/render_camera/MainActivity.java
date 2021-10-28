package cn.tim.render_camera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.RadioGroup;

import cn.tim.render_camera.ui.RoundProgressBar;
import cn.tim.render_camera.widget.CameraView;

public class MainActivity extends AppCompatActivity {
    // 申请权限请求码
    private static final int REQUEST_CAMERA = 1001;
    private CameraView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(this);

        cameraView = findViewById(R.id.camera_view);
        RadioGroup radioGroup = findViewById(R.id.rg_speed);

        RoundProgressBar roundProgressBar = findViewById(R.id.btn_start_record);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.btn_extra_slow:
                    cameraView.setSpeed(CameraView.Speed.MODE_EXTRA_SLOW);
                    break;
                case R.id.btn_slow:
                    cameraView.setSpeed(CameraView.Speed.MODE_SLOW);
                    break;
                case R.id.btn_normal:
                    cameraView.setSpeed(CameraView.Speed.MODE_NORMAL);
                    break;
                case R.id.btn_fast:
                    cameraView.setSpeed(CameraView.Speed.MODE_FAST);
                    break;
                case R.id.btn_extra_fast:
                    cameraView.setSpeed(CameraView.Speed.MODE_EXTRA_FAST);
                    break;
            }
        });

        roundProgressBar.setProgress(100);
        roundProgressBar.setCameraView(cameraView);

        roundProgressBar.setRecordListener(() -> {
            startActivity(new Intent(this, PlayActivity.class));
        });
    }







    // 检查摄像头权限
    public static void verifyStoragePermissions(Activity activity) {
        int cameraPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int storagePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (cameraPermission != PackageManager.PERMISSION_GRANTED ||
                storagePermission != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限需要动态地去申请权限
            ActivityCompat.requestPermissions(
                    activity,
                    // 权限数组
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    // 权限请求码
                    REQUEST_CAMERA
            );
        }
    }
}