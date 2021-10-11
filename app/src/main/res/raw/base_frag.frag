precision mediump float; // 数据精度
varying vec2 aCoord;

uniform sampler2D vTexture;
void main(){
    //  texture2D: vTexture采样器，采样  aCoord 这个像素点的RGBA值
    vec4 rgba = texture2D(vTexture, aCoord);  //RGBA
    gl_FragColor = vec4(rgba.r, rgba.g, rgba.b, rgba.a); // 原画
    //gl_FragColor = vec4(1.-rgba.r,1.-rgba.g,1.-rgba.b,rgba.a); // 底片效果

    // 公式灰度化
    //float r = rgba.r * 0.299 + rgba.g * 0.587 + rgba.b * 0.114; // 灰度效果
    //gl_FragColor = vec4(r, r, r, rgba.a);
}
