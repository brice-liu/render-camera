attribute vec4 vPosition; // 变量float[4]一个顶点 Java传过来的

attribute vec2 vCoord;  // 纹理坐标

varying vec2 aCoord;

void main(){
    // 内置变量：把坐标点赋值给gl_position
    gl_Position = vPosition;
    // 在CameraFilter中已经处理过了，所以现在是直接从FBO中拿，无需旋转校准矩阵了
    aCoord = vCoord;
}