package com.example.opengl_es;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class mGLRenderer implements GLSurfaceView.Renderer{

    private mGL mTri;

    private Context mContext;

    mGLRenderer(Context inputContext){
        mContext = inputContext;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // 배경 색깔 회색
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        mTri = new mGL(mContext);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES30.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        // 프레임을 그릴 때마다 컬러 버퍼를 Clear하고 draw
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
        mTri.draw();
    }



}
