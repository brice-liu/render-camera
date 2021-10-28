package cn.tim.render_camera.filter;

import android.content.Context;
import android.opengl.GLES20;

import cn.tim.render_camera.utils.OpenGLUtils;

/**
 * FBO Filter
 */
public class AbstractFboFilter extends AbstractFilter{
    int[] frameBuffer;
    int[] frameTextures;

    public AbstractFboFilter(Context context, int vertexShaderId, int fragmentShaderId) {
        super(context, vertexShaderId, fragmentShaderId);
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        releaseFrame();

        // 1、创建FBO + FBO中的纹理
        frameBuffer = new int[1];
        frameTextures = new int[1];
        GLES20.glGenFramebuffers(1, frameBuffer, 0);
        OpenGLUtils.glGenTextures(frameTextures);

        // 2、FBO与纹理关联
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameTextures[0]);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height,
                0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);  // 绑定FBO
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D,
                frameTextures[0],
                0);

        // 3、解除绑定，记住：OpenGL是个状态机，面向过程编程
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    @Override
    public int onDraw(int texture) {
        // 指定其往FBO里画
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer[0]);  // 绑定FBO
        super.onDraw(texture);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0); // 解除绑定
        return frameTextures[0];
    }

    @Override
    public void release() {
        super.release();
        releaseFrame();
    }

    // 释放资源，避免内存泄露
    private void releaseFrame() {
        if(frameTextures != null) {
            GLES20.glDeleteTextures(1, frameTextures, 0);
            frameTextures = null;
        }

        if(frameBuffer != null) {
            GLES20.glDeleteBuffers(1, frameBuffer, 0);
        }
    }
}
