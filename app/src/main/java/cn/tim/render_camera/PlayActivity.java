package cn.tim.render_camera;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class PlayActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        videoView = findViewById(R.id.vv_play);
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoPath("/sdcard/demo.mp4");
        videoView.start();
    }
}