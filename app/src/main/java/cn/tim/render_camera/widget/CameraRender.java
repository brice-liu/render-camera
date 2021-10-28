package cn.tim.render_camera.widget;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;

import androidx.camera.core.Preview;
import androidx.lifecycle.LifecycleOwner;


import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cn.tim.render_camera.filter.CameraFilter;
import cn.tim.render_camera.filter.ScreenFilter;
import cn.tim.render_camera.record.MediaRecorder;
import cn.tim.render_camera.utils.CameraHelper;


public class CameraRender implements GLSurfaceView.Renderer,
        Preview.OnPreviewOutputUpdateListener,
        SurfaceTexture.OnFrameAvailableListener {

    private CameraView cameraView;
    private CameraHelper cameraHelper;

    // 摄像头的图像，用OpenGL ES画出来
    private SurfaceTexture mCameraTexture;

    private  int[] textures;
    private ScreenFilter screenFilter;

    float[] mtx = new float[16];
    private CameraFilter cameraFilter;
    private MediaRecorder mediaRecorder;

    public CameraRender(CameraView cameraView) {
        this.cameraView = cameraView;
        LifecycleOwner lifecycleOwner = (LifecycleOwner) cameraView.getContext();
        cameraHelper = new CameraHelper(lifecycleOwner, this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 创建OpenGL纹理 ,把摄像头的数据与这个纹理关联
        textures = new int[1];  // 当做能在OpenGL用的一个图片的ID
        mCameraTexture.attachToGLContext(textures[0]);
        // 当摄像头数据有更新回调 onFrameAvailable
        mCameraTexture.setOnFrameAvailableListener(this);
        Context context = cameraView.getContext();
        cameraFilter = new CameraFilter(context);
        screenFilter = new ScreenFilter(context);

        // 录制视频的宽高
        mediaRecorder = new MediaRecorder(cameraView.getContext(), "/sdcard/demo.mp4",
                EGL14.eglGetCurrentContext(), 480, 640);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        cameraFilter.setSize(width,height);
        screenFilter.setSize(width,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 更新纹理
        mCameraTexture.updateTexImage();
        // 获取CameraX中给定的校准参考矩阵
        mCameraTexture.getTransformMatrix(mtx);
        cameraFilter.setTransformMatrix(mtx);
        int id = cameraFilter.onDraw(textures[0]);
        screenFilter.onDraw(id);

        mediaRecorder.fireFrame(id, mCameraTexture.getTimestamp());
    }

    public void onSurfaceDestroyed() {
        cameraFilter.release();
        screenFilter.release();
    }

    /**
     * 更新
     * @param output 预览输出
     */
    @Override
    public void onUpdated(Preview.PreviewOutput output) {
        mCameraTexture = output.getSurfaceTexture();
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        // 请求执行一次 onDrawFrame
        cameraView.requestRender();
    }

    public void startRecord(float speed) {
        try {
            mediaRecorder.start(speed);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecord() {
        mediaRecorder.stop();
    }
}
