package cn.tim.rander_camera.filter;

import android.content.Context;
import android.opengl.GLES20;

import cn.tim.rander_camera.R;

/**
 * 采样器为ExternalOES，以前是绘制到屏幕，现在是绘制到FBO
 */
public class CameraFilter extends AbstractFboFilter{
    private float[] mtx;
    private int vMatrix;

    public CameraFilter(Context context) {
        super(context, R.raw.camera_vert, R.raw.camera_frag);
    }

    @Override
    public void initGL(Context context, int vertexShaderId, int fragmentShaderId) {
        super.initGL(context, vertexShaderId, fragmentShaderId);
        vMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
    }

    // 在后续处理时，使用的都是经过矩阵校正的图像
    @Override
    public void beforeDraw() {
        super.beforeDraw();
        GLES20.glUniformMatrix4fv(vMatrix, 1, false, mtx, 0);
    }

    // 设置校正矩阵
    public void setTransformMatrix(float[] mtx) {
        this.mtx = mtx;
    }
}
