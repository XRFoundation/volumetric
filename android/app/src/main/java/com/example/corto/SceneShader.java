package com.example.corto;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

public final class SceneShader extends Shader {

    private float[] mMatrix;
    private float[] mvpMatrix;
    private final int aPosition;
    private final int aNormal;
    private final int aTexCoords;
    private final int uMMatrix;
    private final int uMVPMatrix;
    int uSTMMatrixHandle;
    private Mesh mesh;
    private Vector3f viewPos;
    private final int uViewPos;

    public SceneShader(Context context) {
        super(context,"vs_scene.glsl", "fs_scene.glsl");
        GLES20.glUseProgram(getProgram());
        checkGlError("glUseProgram");

        aPosition = getAttrib("aPosition");
        aNormal = getAttrib("aNormal");
        aTexCoords = getAttrib("aTexCoords");
        uMMatrix = getUniform("uMMatrix");
        uMVPMatrix = getUniform("uMVPMatrix");
        uViewPos = getUniform("uViewPos");
        uSTMMatrixHandle = getUniform("uSTMatrix");
    }

    protected void EnableVertexAttribArray(int attrib, String name)
    {
        GLES20.glEnableVertexAttribArray(attrib);
        checkGlError("glEnableVertexAttribArray "+name);
    }

    @Override
    public void bindData(float[] stMmatrix) {
        GLES20.glUseProgram(getProgram());
        checkGlError("glUseProgram");

        EnableVertexAttribArray(aPosition,"aPosition");
        //EnableVertexAttribArray(aNormal,"aNormal");
        EnableVertexAttribArray(aTexCoords, "aTexCoords");

        long meshSize = mesh.getSizeVertex();
        FloatBuffer buffer = mesh.getVertexBuffer();
        GLES20.glVertexAttribPointer(aPosition, mesh.getSizeVertex(), GLES20.GL_FLOAT, false, 0, mesh.getVertexBuffer());
        checkGlError("glVertexAttribPointer aPosition");
        //GLES20.glVertexAttribPointer(aNormal, 3, GLES20.GL_FLOAT, false, 0, mesh.getNormalBuffer());
        //checkGlError("glVertexAttribPointer aNormal");
        GLES20.glVertexAttribPointer(aTexCoords, 2, GLES20.GL_FLOAT, false, 0, mesh.getTexCoordsBuffer());
        checkGlError("glVertexAttribPointer aTexCoords");

        GLES20.glUniformMatrix4fv(uMMatrix, 1, false, mMatrix, 0);
        GLES20.glUniformMatrix4fv(uMVPMatrix, 1, false, mvpMatrix, 0);
        GLES20.glUniform3f(uViewPos, viewPos.x, viewPos.y, viewPos.z);

        GLES20.glUniformMatrix4fv(uSTMMatrixHandle, 1, false, stMmatrix, 0);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mesh.getIndicesBuffer().capacity(), GLES20.GL_UNSIGNED_INT, mesh.getIndicesBuffer());
        checkGlError("glDraw");
    }

    @Override
    public void unbindData() {
        GLES20.glDisableVertexAttribArray(aPosition);
        //GLES20.glDisableVertexAttribArray(aNormal);
    }

    public SceneShader setMMatrix(float[] mMatrix){
        this.mMatrix = mMatrix;
        return this;
    }

    public SceneShader setMvpMatrix(float[] mvpMatrix){
        this.mvpMatrix = mvpMatrix;
        return this;
    }

    public SceneShader setMesh(Mesh mesh){
        this.mesh = mesh;
        return this;
    }

    public SceneShader setViewPos(Vector3f viewPos){
        this.viewPos = viewPos;
        return this;
    }

    public void draw(Mesh mesh, float[] stMmatrix)
    {
        setMesh(mesh);
        bindData(stMmatrix);
        unbindData();
    }

}
