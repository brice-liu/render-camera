package cn.tim.rander_camera.filter;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import cn.tim.rander_camera.R;
import cn.tim.rander_camera.utils.OpenGLUtils;

/**
 * 绘制到屏幕上
 */
public class ScreenFilter extends AbstractFilter{

    public ScreenFilter(Context context) {
        super(context, R.raw.base_vert, R.raw.base_frag);
    }

}
