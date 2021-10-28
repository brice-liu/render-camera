package cn.tim.render_camera.widget;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

public class CameraView extends GLSurfaceView {
    private  CameraRender renderer;

    private Speed mSpeed = Speed.MODE_NORMAL;

    public enum Speed {
        MODE_EXTRA_SLOW, MODE_SLOW, MODE_NORMAL, MODE_FAST, MODE_EXTRA_FAST
    }

    public void setSpeed(Speed speed) {
        this.mSpeed = speed;
    }

    public CameraView(Context context) {
        this(context,null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //使用OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);
        //设置渲染回调接口
        renderer = new CameraRender(this);
        setRenderer(renderer);

        // 设置刷新方式
        // RENDERMODE_WHEN_DIRTY 手动刷新，调用requestRender();
        // RENDERMODE_CONTINUOUSLY 自动刷新，大概16ms自动回调一次onDraw方法
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        renderer.onSurfaceDestroyed();
    }

    public void startRecord(){
        //速度  时间/速度 speed小于就是放慢 大于1就是加快
        float speed = 1.f;
        switch (mSpeed) {
            case MODE_EXTRA_SLOW:
                speed = 0.3f;
                break;
            case MODE_SLOW:
                speed = 0.5f;
                break;
            case MODE_NORMAL:
                speed = 1.f;
                break;
            case MODE_FAST:
                speed = 1.5f;
                break;
            case MODE_EXTRA_FAST:
                speed = 2.f;
                break;
        }
        renderer.startRecord(speed);
    }

    public void stopRecord(){
        renderer.stopRecord();
    }
}
