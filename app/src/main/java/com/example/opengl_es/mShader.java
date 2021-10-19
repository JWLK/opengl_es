package com.example.opengl_es;

import android.content.Context;
import android.opengl.GLES30;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class mShader {
    public int mProgramID;

    public mShader(Context inputContext, String vertexName, String fragmentName) {
        String vertexCode = readCodeFromFile(inputContext, vertexName);
        String fragmentCode = readCodeFromFile(inputContext, fragmentName);

        // Vertex Compile
        int mVertexShader = compileShader(GLES30.GL_VERTEX_SHADER, vertexCode);

        // Fragment Compile
        int mFragShader = compileShader(GLES30.GL_FRAGMENT_SHADER, fragmentCode);

        mProgramID = GLES30.glCreateProgram();
        GLES30.glAttachShader(mProgramID, mVertexShader);
        GLES30.glAttachShader(mProgramID, mFragShader);
        GLES30.glLinkProgram(mProgramID);

        int[] mLinkedStatus = new int[1];
        GLES30.glGetProgramiv(mProgramID, GLES30.GL_LINK_STATUS, mLinkedStatus, 0);
        if(mLinkedStatus[0] == 0){
            Log.d("(m) mShader File:", GLES30.glGetProgramInfoLog(mProgramID));
            Log.d("(m) mShader File:", "(Error) Failed Linking");
            GLES30.glDeleteProgram(mProgramID);
        }
        else{
            Log.d("(m) mShader File:", "Linked Vertex & Fragment Code");
        }

        GLES30.glDeleteShader(mVertexShader);
        GLES30.glDeleteShader(mFragShader);

    }

    public void use(){
        GLES30.glUseProgram(mProgramID);
    }

    // Uniform Float 값을 설정하기 위해 추가
    public void setUniformFloat(String uniformName, float inputValue){
        int uniformLocation = GLES30.glGetUniformLocation(this.mProgramID, uniformName);
        GLES30.glUniform1f(uniformLocation, inputValue);
    }

    // Uniform Int 값을 설정하기 위해 추가 // 보통 Texture에서 많이 사용됨
    public void setUniformInt(String uniformName, int inputValue){
        int uniformLocation = GLES30.glGetUniformLocation(this.mProgramID, uniformName);
        GLES30.glUniform1i(uniformLocation, inputValue);
    }

    public void setUniformMatrix4(String uniformName, float[] matrix){
        int uniformLocation = GLES30.glGetUniformLocation(this.mProgramID, uniformName);
        GLES30.glUniformMatrix4fv(uniformLocation, 1, false, matrix, 0);
    }

    private String readCodeFromFile(Context inputContext, String fileName){
        BufferedReader reader = null;
        String mCode = "";
        try {
            InputStreamReader in = new InputStreamReader(inputContext.getAssets().open(fileName));
            reader = new BufferedReader(in);
            // read file until EOF
            String line;
            while ((line = reader.readLine()) != null) {
                mCode += line;
                mCode += "\n";
            }
        } catch (IOException e) {
            Log.d("(m) mShader File:", "Cannot read file");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.d("(m) mShader File:", "Cannot close file");
                }
            }
        }
        return mCode;
    }

    private static int compileShader(int type, String ShaderCode){
        int mShaderId = GLES30.glCreateShader(type);
        GLES30.glShaderSource(mShaderId, ShaderCode);
        GLES30.glCompileShader(mShaderId);
        int[] mCompileStatue = new int[1];
        GLES30.glGetShaderiv(mShaderId, GLES30.GL_COMPILE_STATUS, mCompileStatue, 0);
        if(mCompileStatue[0] == 0){
            Log.d("(m) mShader File:", GLES30.glGetShaderInfoLog(mShaderId));
            Log.d("(m) mShader File:", "(Code) " + ShaderCode + "\n Compiled Error");
            mShaderId = 0;
        }
        else{
            Log.d("(m) mShader File:", "(Code) " + ShaderCode + "\nCompiled");
        }
        return mShaderId;
    }
}
