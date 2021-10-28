package cn.tim.render_camera.record;

import android.content.Context;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.view.Surface;

import cn.tim.render_camera.filter.RecordFilter;

public class EGLEnv {
    private final EGLContext mEglContext;
    private final RecordFilter recordFilter;

    private final EGLSurface mEglSurface; // 画布
    private final EGLDisplay mEglDisplay; // 窗口

    /**
     * @param surface MediaRecorder的surface
     */
    public EGLEnv(Context context,EGLContext mGlContext, Surface surface,int width,int height) {
        // 1、创建Display，获得显示窗口，作为OpenGL的绘制目标
        mEglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        if (mEglDisplay == EGL14.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed");
        }

        // 初始化显示窗口
        int[] version = new int[2];
        if(!EGL14.eglInitialize(mEglDisplay, version,0,version,1)) {
            throw new RuntimeException("eglInitialize failed");
        }

        // 2、配置Display，属性选项（RGBA）
        int[] configAttributes = {
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT, // OpenGL ES 2.0
                EGL14.EGL_NONE // 参数占位符
        };
        int[] numConfigs = new int[1];
        EGLConfig[] configs = new EGLConfig[1];
        // EGL根据属性选择一个配置
        if (!EGL14.eglChooseConfig(mEglDisplay, configAttributes, 0, configs, 0, configs.length,
                numConfigs, 0)) {
            throw new RuntimeException("EGL error " + EGL14.eglGetError());
        }

        EGLConfig mEglConfig = configs[0];

        // EGL上下文参数
        int[] context_attribute_list = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
        };

        // 让编码的OpenGLContext与GLSurfaceView的OpenGLContext绑定，与GLSurfaceView中的EGLContext共享数据
        // 只有这样才能拿到处理完之后显示的图像纹理，拿到这个纹理才能进行编码
        mEglContext = EGL14.eglCreateContext(mEglDisplay, mEglConfig,
                mGlContext, context_attribute_list, 0);

        if (mEglContext == EGL14.EGL_NO_CONTEXT){
            throw new RuntimeException("EGL error " + EGL14.eglGetError());
        }

        // 创建EGLSurface (画布)
        int[] surface_attribute_list = {
                EGL14.EGL_NONE
        };
        mEglSurface = EGL14.eglCreateWindowSurface(mEglDisplay, mEglConfig, surface, surface_attribute_list, 0);
        if (mEglSurface == null){
            throw new RuntimeException("EGL error " + EGL14.eglGetError());
        }

        // 绑定当前线程的显示器（display）
        if (!EGL14.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface, mEglContext)){
            throw new RuntimeException("EGL error " + EGL14.eglGetError());
        }

        recordFilter = new RecordFilter(context);
        recordFilter.setSize(width,height);
    }

    public void draw(int textureId, long timestamp) {
        recordFilter.onDraw(textureId);
        EGLExt.eglPresentationTimeANDROID(mEglDisplay, mEglSurface, timestamp);
        // EGLSurface是双缓冲模式
        EGL14.eglSwapBuffers(mEglDisplay, mEglSurface);
    }

    public void release(){
        EGL14.eglDestroySurface(mEglDisplay,mEglSurface);
        EGL14.eglMakeCurrent(mEglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                EGL14.EGL_NO_CONTEXT);
        EGL14.eglDestroyContext(mEglDisplay, mEglContext);
        EGL14.eglReleaseThread();
        EGL14.eglTerminate(mEglDisplay);
        recordFilter.release();
    }
}
