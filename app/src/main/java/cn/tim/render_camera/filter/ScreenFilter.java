package cn.tim.render_camera.filter;

import android.content.Context;

import cn.tim.render_camera.R;

/**
 * 绘制到屏幕上
 */
public class ScreenFilter extends AbstractFilter{

    public ScreenFilter(Context context) {
        super(context, R.raw.base_vert, R.raw.base_frag);
    }

}
