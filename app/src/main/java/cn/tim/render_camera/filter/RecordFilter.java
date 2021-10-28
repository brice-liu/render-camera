package cn.tim.render_camera.filter;

import android.content.Context;

import cn.tim.render_camera.R;

public class RecordFilter extends AbstractFilter{

    public RecordFilter(Context context) {
        super(context, R.raw.base_vert, R.raw.base_frag);
    }
}
