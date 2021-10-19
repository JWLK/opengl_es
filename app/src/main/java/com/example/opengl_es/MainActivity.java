package com.example.opengl_es;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public GLSurfaceView mSurfaceView;
    Button Button_Start;
    Button Button_Pause;

    // Start 버튼을 눌러야만 실행되도록 Pause_flag의 초기 상태는 true
    // (Pause_flag가 false일 때만 Data를 프레임마다 업데이트함)
    public static Boolean Pause_flag = true;
    public static float Touch_x;
    public static float Touch_y;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        TextView txt = (TextView) findViewById(R.id.textView);
        mSurfaceView = (GLSurfaceView)findViewById(R.id.mSurfaceView);
        mSurfaceView.setEGLContextClientVersion(3);
        mSurfaceView.setRenderer(new mGLRenderer(this));
        mSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        Button_Pause = (Button)findViewById(R.id.Button_Pause);
        Button_Pause.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Pause 버튼을 클릭하면 Pause_flag를 true로 설정
                // Data를 프레임마다 업데이트하지 않고 버튼을 누른 시점의 데이터를 계속해서 표시함
                Pause_flag = true;
            }
        });

        Button_Start = (Button)findViewById(R.id.Button_Start);
        Button_Start.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Reset 버튼을 클릭하면 Pause_flag를 false로 설정
                // (Data를 프레임마다 업데이트하여 표시함)
                Pause_flag = false;
            }
        });

        mSurfaceView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // 터치 이벤트가 발생하면 뷰의 좌측 상단을 기준(0,0)으로 몇번째 픽셀인지 x, y에 저장
                float x = event.getX();
                float y = event.getY();

                // 뷰의 너비, 높이가 해당 기기에서 몇 픽셀인지 저장
                float m_Width = mSurfaceView.getWidth();
                float m_Height = mSurfaceView.getHeight();

                Log.d( "Touch", "onTouch : (" + x +", " + y + ")" );

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        // 뷰의 너비를 32로 나누어, 터치한 좌표가 32픽셀 기준 몇번째 픽셀에 위치하는지 저장
                        Touch_x = x / ( m_Width / 32 );
                        // 뷰의 높이를 1024로 나누어, 터치한 좌표가 1024픽셀 기준 몇번째 픽셀에 위치하는지 저장
                        Touch_y = y / ( m_Height / 1024 );
                        // 이후 mGL에서 이 변수들을 프레임마다 가져가서 포인터 위치를 새로고침함

                        // ACTION_MOVE하여 mSurfaceView 밖을 터치하게 되면 좌표가 마이너스로 잘못 표기되는 경우가 있어 if문 사용
                        if ( 0 < Touch_x & Touch_x < 32 & 0 < Touch_y & Touch_y < 1024 ) {
                            // 화면 하단에 터치 좌표 print
                            txt.setText((int)Touch_x + ", " + (int)Touch_y);
                        } else {
                            txt.setText("out");
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        //손가락을 화면에서 뗄 때 할 일
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        // 터치가 취소될 때 할 일
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }
}