package cn.tim.rander_camera.widget;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

public class CameraView extends GLSurfaceView {
    private  CameraRender renderer;

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
}
