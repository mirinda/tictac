package kz.mirinda.tictac.gl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.FloatBuffer;

import kz.mirinda.tictac.helper.RawResourceReader;
import kz.mirinda.tictac.renderer.examples.BrushShtampExample;
import kz.mirinda.tictac.renderer.engine.GLTexture;

/**
 * Created with IntelliJ IDEA.
 * User: asus
 * Date: 22.07.13
 * Time: 23:13
 * To change this template use File | Settings | File Templates.
 */
public class /**/GH {
    private static final String TAG = "GH";
    public static final int VERTEX_SHADER = GLES20.GL_VERTEX_SHADER ;
    public static final int FRAGMENT_SHADER = GLES20.GL_FRAGMENT_SHADER;

    public static void clearColor(int color){
        GLES20.glClearColor(Color.red(color)/255.0f,Color.green(color)/255.0f,Color.blue(color)/255.0f,Color.alpha(color)/255.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }

    public static int genTexture() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1,texture,0);
        Log.d(TAG,"texture id="+ texture[0]);
        return texture[0];
    }

    public static void /**/bindTexture(int texture) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture);
    }

    public static void texImage2D(Bitmap bmp, int texture) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
        //GLES20.glTexImage2D();
        //GLES20.glGenerateMipmap();
    }

    public static void texImage2D(int texture, int textureSize){
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture);

        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D,0,GLES20.GL_RGBA,textureSize,textureSize,0,GLES20.GL_RGBA,GLES20.GL_UNSIGNED_BYTE,null);


        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_NEAREST);

    }

    public static void viewPort(int screenWidth, int screenHeight) {
        GLES20.glViewport(0,0,screenWidth,screenHeight);
    }

    public static int compileShader(final int shaderType, final String shaderSource)
    {
        int shaderHandle = GLES20.glCreateShader(shaderType);

        if (shaderHandle != 0)
        {
            // Pass in the shader source.
            GLES20.glShaderSource(shaderHandle, shaderSource);

            // Compile the shader.
            GLES20.glCompileShader(shaderHandle);

            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shaderHandle));
                GLES20.glDeleteShader(shaderHandle);
                shaderHandle = 0;
            }
        }

        if (shaderHandle == 0)
        {
            throw new RuntimeException("Error creating shader.");
        }

        return shaderHandle;
    }

    /**
     * Helper function to compile and link a program.
     *
     * @param vertexShaderHandle An OpenGL handle to an already-compiled vertex shader.
     * @param fragmentShaderHandle An OpenGL handle to an already-compiled fragment shader.
     * @param attributes Attributes that need to be brush_stamp to the program.
     * @return An OpenGL handle to the program.
     */
    public static int createAndLinkProgram(final int vertexShaderHandle, final int fragmentShaderHandle, final String[] attributes)
    {
        int programHandle = GLES20.glCreateProgram();

        if (programHandle != 0)
        {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            // Bind attributes
            if (attributes != null)
            {
                final int size = attributes.length;
                for (int i = 0; i < size; i++)
                {
                    GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
                }
            }

            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0)
            {
                Log.e(TAG, "Error compiling program: " + GLES20.glGetProgramInfoLog(programHandle));
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0)
        {
            throw new RuntimeException("Error creating program.");
        }

        return programHandle;
    }

    public static void projectionMatrix(float[] matrix, float width_2, float height_2) {
        Matrix.orthoM(matrix,0,-width_2,width_2,-height_2,height_2,-100f,100f);
    }

    public static void useProgram(int mProgramHandle) {
        GLES20.glUseProgram(mProgramHandle);
    }

    public static void lookAt(float[] viewMatrix) {
        Matrix.setLookAtM(viewMatrix,0,
                                     0f,0f,0f, //eye
                                     0f,0f,-0.5f, //look
                                     0f,1f,0f);   //up
    }

    public static void lookAt(float[] viewMatrix, float x1,float x2) {
        Matrix.setLookAtM(viewMatrix,0,
                x1,x2,0f, //eye
                x1,x2,-0.5f, //look
                0f,1f,0f);   //up
    }

    public static int genFrameBuffer() {
        int[] fb = new int[1];
        GLES20.glGenFramebuffers(1,fb, 0);

        if(GLES20.glGetError() !=0){
            Log.e(TAG,"genFrameBufferError: GLERRORR"+ GLES20.glGetError());
            throw new RuntimeException("genFrameBufferError: GLERRORR"+ GLES20.glGetError());
        }
        return fb[0];
    }

    public static void VertexAttrib(int programHandle, GLTexture.GLPosition glPosition) {
        vertexPosition(programHandle, glPosition.getVertecies());

        FloatBuffer textures = glPosition.getTextures();
        textures.position(0);
        int textureLocation = GLES20.glGetAttribLocation(programHandle,"a_TexCoordinate");
        GLES20.glVertexAttribPointer(textureLocation,
                BrushShtampExample.TEXTURE_POINT_DIMENSION, GLES20.GL_FLOAT,
                false, 0, textures);

        GLES20.glEnableVertexAttribArray(textureLocation);
    }

    public static void vertexPosition(int programHandle, FloatBuffer verticies) {
        verticies.position(0);
        int vertexLocation = GLES20.glGetAttribLocation(programHandle, "a_Position");
        GLES20.glVertexAttribPointer(vertexLocation,
                BrushShtampExample.VERTEX_POINT_DIMENSION, GLES20.GL_FLOAT,
                false, 0, verticies);
        GLES20.glEnableVertexAttribArray(vertexLocation);
    }


    public static void VertexAttrib(int programHandle, GLTexture.GLPosition glPosition,int vertexFrameBuffer) {

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,vertexFrameBuffer);
        int vertexLocation = GLES20.glGetAttribLocation(programHandle,"a_Position");
        GLES20.glVertexAttribPointer(vertexLocation,
                BrushShtampExample.VERTEX_POINT_DIMENSION, GLES20.GL_FLOAT,
                false, 0, 0);
        GLES20.glEnableVertexAttribArray(vertexLocation);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);


        FloatBuffer textures = glPosition.getTextures();
        textures.position(0);
        int textureLocation = GLES20.glGetAttribLocation(programHandle,"a_TexCoordinate");
        GLES20.glVertexAttribPointer(textureLocation,
                BrushShtampExample.TEXTURE_POINT_DIMENSION, GLES20.GL_FLOAT,
                false, 0, textures);

        GLES20.glEnableVertexAttribArray(textureLocation);
    }

    public static void VertexAttrib(int programHandle, int vertexFrameBuffer, int texturePointsBuffer) {

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,vertexFrameBuffer);
        int vertexLocation = GLES20.glGetAttribLocation(programHandle,"a_Position");
        GLES20.glVertexAttribPointer(vertexLocation,
                BrushShtampExample.VERTEX_POINT_DIMENSION, GLES20.GL_FLOAT,
                false, 0, 0);
        GLES20.glEnableVertexAttribArray(vertexLocation);


        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,texturePointsBuffer);
        int textureLocation = GLES20.glGetAttribLocation(programHandle,"a_TexCoordinate");
        GLES20.glVertexAttribPointer(textureLocation,
                BrushShtampExample.TEXTURE_POINT_DIMENSION, GLES20.GL_FLOAT,
                false, 0, 0);
        GLES20.glEnableVertexAttribArray(textureLocation);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,0);
    }

    public static void textureVertexAttrib(int programHandle, FloatBuffer textures ){
        textures.position(0);
        int textureLocation = GLES20.glGetAttribLocation(programHandle,"a_TexCoordinate");
        GLES20.glVertexAttribPointer(textureLocation,
                BrushShtampExample.TEXTURE_POINT_DIMENSION, GLES20.GL_FLOAT,
                false, 0, textures);
        GLES20.glEnableVertexAttribArray(textureLocation);
    }

    public static void textureVertexAttrib(FloatBuffer textures, int textureAttribHandle ){
        textures.position(0);
        GLES20.glVertexAttribPointer(textureAttribHandle,
                BrushShtampExample.TEXTURE_POINT_DIMENSION, GLES20.GL_FLOAT,
                false, 0, textures);
        GLES20.glEnableVertexAttribArray(textureAttribHandle);
    }

    public static void positionVertexAttrib(int programHandle, int positionVertexBufferIndex){
        int positionLocation = GLES20.glGetAttribLocation(programHandle,"a_Position");
        GLES20.glVertexAttribPointer(positionVertexBufferIndex,
                BrushShtampExample.VERTEX_POINT_DIMENSION, GLES20.GL_FLOAT,
                false, 0, null);
        GLES20.glEnableVertexAttribArray(positionLocation);
    }

    public static int getCurrentBuffer() {
        int[] s = new int[1];
         GLES20.glGetIntegerv(GLES20.GL_FRAMEBUFFER_BINDING, s,0);
        return s[0];
    }

    public static void uniformMatrix(int mProgramHandle, GLTexture.Scene scene) {
        int mvpLocation = GLES20.glGetUniformLocation(mProgramHandle, GLTexture.Scene.PROJECTION_MATRIX);

        //scene.setModelMatrixIdentity();
        scene.calculateMVPMatrix();

        GLES20.glUniformMatrix4fv(mvpLocation,1,false,scene.getMVPMatrix(),0);

        int mvLocation = GLES20.glGetUniformLocation(mProgramHandle, GLTexture.Scene.VIEW_MATRIX);
        GLES20.glUniformMatrix4fv(mvLocation,1,false,scene.getMVMMatrix(),0);

    }

    public static void draw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0,6);
    }

    public static void bindFrameBuffer(int frameBuffer, int texture) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,frameBuffer);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,GLES20.GL_COLOR_ATTACHMENT0,GLES20.GL_TEXTURE_2D,texture,0);
    }

    public static void bindSuperBuffer(int superBuffer) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER,superBuffer);
    }

    public static void activeTexture0(int programHandle,int texId, String textureUniformName) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texId);
        int texUniformHandle = GLES20.glGetUniformLocation(programHandle, textureUniformName);
        GLES20.glUniform1i(texUniformHandle, 0);
    }

    public static void activeTexture0(int texId, int texUniformHandle) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texId);
        GLES20.glUniform1i(texUniformHandle, 0);
    }

    public static void uniformVec2(int programHandle, String uniformName, float v1, float v2) {
        int uniformHandle = GLES20.glGetUniformLocation(programHandle, uniformName);
        GLES20.glUniform2f(uniformHandle, v1,v2);
    }

    /**
     * create program with attributes  "a_Position","a_TexCoordinate"
     * @param context
     * @param vertexShaderRawId
     * @param fragmentShaderRawId
     * @return
     */
    public static int createProgram(Context context, int vertexShaderRawId, int fragmentShaderRawId) {
        String vertexShader = RawResourceReader.readTextFileFromRawResource(context, vertexShaderRawId);
        String fragmentShader = RawResourceReader.readTextFileFromRawResource(context, fragmentShaderRawId);
        int vertexShaderHandle = GH.compileShader(GH.VERTEX_SHADER, vertexShader);
        int fragmentShaderHandle = GH.compileShader(GH.FRAGMENT_SHADER,fragmentShader);
        return GH.createAndLinkProgram(vertexShaderHandle,fragmentShaderHandle,new String[]{"a_Position","a_TexCoordinate"});
    }

    public static void uniformArray(int programHandle, String locationString, float[] array) {
        int location = GLES20.glGetUniformLocation(programHandle,locationString);
        GLES20.glUniform1fv(location,array.length,array,0);
    }

    public static void uniformColor(int programHandle, String locationString, int color) {
        int location = GLES20.glGetUniformLocation(programHandle,locationString);
        GLES20.glUniform4f(location,Color.red(color)/255.0f,Color.green(color)/255.0f,Color.blue(color)/255.0f,Color.alpha(color)/255.0f);
    }

    public static void uniform1f(int programHandle, String locationString, float val) {
        int location = GLES20.glGetUniformLocation(programHandle,locationString);
        GLES20.glUniform1f(location,val);
    }

    public static void viewPort(GLTexture.Scene scene) {
        GH.viewPort(scene.getWidth(),scene.getHeight());
    }
}
