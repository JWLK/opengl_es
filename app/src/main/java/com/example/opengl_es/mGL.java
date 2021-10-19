package com.example.opengl_es;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES30;
import android.opengl.GLUtils;
import android.os.SystemClock;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class mGL {

    // Texture를 적용할 사각형 좌표 정의
    private float[] mVertices = {
            1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,     // Top Right
            1.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,    // Bottom Right
            -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f,   // Bottom Left
            -1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f     // Top Left
    };

    // 포인터 좌표 정의
    private float[] mPointVertices = {
        0.01f + (4f/9f), 2.045f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,    // 0
        0.01f, 0.045f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,              // 1
        -0.015f, 0.045f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,            // 2
        -0.035f, -0.045f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,           // 3
        -0.01f, -0.045f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,            // 4
        -0.01f - (4f/9f), -2.045f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,  // 5
        0.015f, -0.045f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,            // 6
        0.035f, 0.045f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,             // 7
    };

    // 사각형 그리는 순서 정의
    private int[] mIndices = { 0, 1, 3, // First Triangle
            1, 2, 3 // Second Triangle
    };

    // 포인터 그리는 순서 정의
    private int[] mPointIndices = { 0, 1, 2, 3, 4, 5, 4, 6, 7, 1 };

    private int[] mVAO;
    private int[] mVBO;
    private int[] mEBO;

    private mShader mTriShader;
    private mShader mPointShader;

    public int textureID[];

    // Texture로 사용할 비트맵 생성
    public static Bitmap mBitmap = Bitmap.createBitmap(32,1024, Bitmap.Config.ARGB_8888);

    public mGL(Context inputContext){

        // 앱이 처음 시작됐을 때(아직 터치이벤트가 발생하지 않았을 때) 포인터가 GLSurfaceView의 중앙에 오도록 설정
        MainActivity.Touch_x = 16.0f;
        MainActivity.Touch_y = 512.0f;

        // 정점의 좌표들을 Shader를 통해 화면에 보여질 수 있는 정보로 변환
        mTriShader = new mShader(inputContext, "mVertexCode", "mFragmentCode");
        mPointShader = new mShader(inputContext, "mVertexCode", "mFragmentCode2");

        // 사각형과 포인터를 처리하기 위한 변수 생성
        // Make VAO, VBO
        mVAO = new int[2];
        GLES30.glGenVertexArrays(2, mVAO, 0);

        mVBO = new int[2];
        GLES30.glGenBuffers(2, mVBO, 0);

        // Make Element Buffer Object (EBO)
        mEBO = new int[2];
        GLES30.glGenBuffers(2, mEBO, 0);

        // ---------------------- Texture Box 생성 ----------------------

        GLES30.glBindVertexArray(mVAO[0]);

        // VBO Bind
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBO[0]);

        // Vertex 좌표를 mVBO로 복사하는 과정
        ByteBuffer mByteBuffer = ByteBuffer.allocateDirect(mVertices.length * 4);
        mByteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer mVertexBuffer = mByteBuffer.asFloatBuffer();
        mVertexBuffer.put(mVertices);
        mVertexBuffer.position(0);

        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, mVertices.length*4, mVertexBuffer, GLES30.GL_DYNAMIC_DRAW );

        // mVBO의 Offset, Vec사이즈 등 설정
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 8 * 4, 0);
        GLES30.glEnableVertexAttribArray(0);

        // mVBO의 Offset, Vec사이즈 등 설정
        GLES30.glVertexAttribPointer(1, 3, GLES30.GL_FLOAT, false, 8 * 4, 3 * 4);
        GLES30.glEnableVertexAttribArray(1);

        // Texture Coordinate 좌표
        GLES30.glVertexAttribPointer(2, 2, GLES30.GL_FLOAT, false, 8 * 4, 6 * 4);
        GLES30.glEnableVertexAttribArray(2);

        // EBO Bind
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, mEBO[0]);

        // Indice 값을 mEBO로 복사
        IntBuffer mIndexBuffer = mByteBuffer.asIntBuffer();
        mIndexBuffer.put(mIndices);
        mIndexBuffer.position(0);

        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, mIndices.length*4, mIndexBuffer, GLES30.GL_DYNAMIC_DRAW);

        // ---------------------- Pointer 생성 ----------------------

        GLES30.glBindVertexArray(mVAO[1]);

        // VBO Bind
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, mVBO[1]);

        // Vertex 좌표를 mVBO로 복사하는 과정
        mByteBuffer = ByteBuffer.allocateDirect(mPointVertices.length * 4);
        mByteBuffer.order(ByteOrder.nativeOrder());
        mVertexBuffer = mByteBuffer.asFloatBuffer();
        mVertexBuffer.put(mPointVertices);
        mVertexBuffer.position(0);

        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER, mPointVertices.length*4, mVertexBuffer, GLES30.GL_DYNAMIC_DRAW );

        // mVBO의 Offset, Vec사이즈 등을 설정하는 부분
        GLES30.glVertexAttribPointer(0, 3, GLES30.GL_FLOAT, false, 8 * 4, 0);
        GLES30.glEnableVertexAttribArray(0);

        // EBO Bind
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, mEBO[1]);

        // Indice 값을 mEBO로 복사하는 과정
        mIndexBuffer = mByteBuffer.asIntBuffer();
        mIndexBuffer.put(mPointIndices);
        mIndexBuffer.position(0);

        GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER, mPointIndices.length*4, mIndexBuffer, GLES30.GL_DYNAMIC_DRAW);

        // --------------------------------------------

        mTriShader.use();
        mTriShader.setUniformInt("inputTexture", 0);

        mPointShader.use();

        // texture init
        textureID = new int[1];
        GLES30.glGenTextures(1, textureID, 0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureID[0]);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);

        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, mBitmap, 0);
    }

    // 임시 데이터 선언
    int[] Data = new int[32*1024];

    // GLSurfaceView 터치 좌표를 저장할 변수 선언
    public float x = 0.0f;
    public float y = 0.0f;

    // 렌더러 모드가 RENDERMODE_CONTINUOUSLY이면 CPU가 여유가 될 때마다 draw를 계속해서 호출하여 화면을 그린다.
    public void draw(){

        // 프레임마다 터치 좌표 새로고침
        // 터치 이벤트로 반환된 좌표를 GL의 좌표에 맞게 변환
        x = (MainActivity.Touch_x - 16)/16;
        y = -(MainActivity.Touch_y - 512)/512;

        // -- 임시 데이터 (그라데이션) 생성 --
        long time = SystemClock.uptimeMillis() / 5 % 255L;

        for (int x = 0; x < 32; x++) {
            for (int y = 0; y < 1024; y++) {
                //색상 데이터
//                Data[(x*1024)+y] = (int) time+(x*255/32);

                Data[(x*1024)+y] = (int) time + (255 / 32 * x * y / 1024) ;;
            }
        }
        // ---------------------------------

        // Pause_flag가 false일 때만 데이터를 업데이트하여 표시
        if ( MainActivity.Pause_flag == false ) {
            // Bitmap 색깔을 Data대로 설정
            for (int x = 0; x < 32; x++) {
                for (int y = 0; y < 1024; y++) {
                    mGL.mBitmap.setPixel( x, 1023-y, Color.rgb( Data[(1024*x)+y], Data[(1024*x)+y], Data[(1024*x)+y] ) );
                }
            }
        }

        // Texture 새로고침
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureID[0]);
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, mBitmap, 0);

        // Draw Triangles
        mTriShader.use();
        GLES30.glBindVertexArray(mVAO[0]);
        GLES30.glDrawElements(GLES30.GL_TRIANGLES, 6, GLES30.GL_UNSIGNED_INT, 0);

        // Draw Point
        mPointShader.use();
        mPointShader.setUniformFloat("movingXOffset", x);
        mPointShader.setUniformFloat("movingYOffset", y);
        GLES30.glBindVertexArray(mVAO[1]);

        GLES30.glDrawElements(GLES30.GL_LINE_STRIP, 10, GLES30.GL_UNSIGNED_INT, 0);

        FPS();
    }

    long fpsStartTime = 0L;             // Frame 시작 시간

    int frameCnt = 0;                   // 돌아간 Frame 갯수
    double timeElapsed = 0.0f;          // 그 동안 쌓인 시간 차이

    private void FPS() {

        //시간 차이 구하기
        long fpsEndTime = System.currentTimeMillis();
        float timeDelta = (fpsEndTime - fpsStartTime) * 0.001f;

        // Frame 증가 셋팅
        frameCnt++;
        timeElapsed += timeDelta;

        // FPS를 구해서 로그로 표시
        if(timeElapsed >= 1.0f){
            float fps = (float)(frameCnt/timeElapsed);
            Log.d("fps","fps : "+fps);
            frameCnt = 0;
            timeElapsed = 0.0f;

        }

        // Frame 시작 시간 다시 셋팅
        fpsStartTime = System.currentTimeMillis();

    }

}
