package pipeline.scene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLDrawable;
import javax.vecmath.Color3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import pipeline.Pipeline;

/**
 * This class represents a scene containing a single triangle mesh object.
 * 
 * @author ags
 */
public class SceneMesh extends Scene {

    /** A list of vertices (each 3 values makes a single vertex) */
    float[] vertices;

    /** The normals that go with vertices of the same index */
    float[] normals;

    /** 2 floats per vertex that make up a texture coordinate */
    float[] texcoords;

    /** The triangle index list */
    int[] triangles;

    /** The name of the file containing the mesh data */
    String filename;

    /**
     * Default constructor for a scene containing a mesh. The given parameter must
     * contain all the mesh data necessary.
     * 
     * @param meshFile The file containing the mesh data.
     */
    public SceneMesh(File meshFile) {

        try {
            filename = meshFile.getName();
            BufferedReader fr = new BufferedReader(new FileReader(meshFile));
            int nPoints = Integer.parseInt(fr.readLine());
            int nPolys = Integer.parseInt(fr.readLine());

            vertices = new float[nPoints * 3];
            texcoords = new float[nPoints * 2];
            triangles = new int[nPolys * 3];

            boolean vertsRead = false;
            boolean trisRead = false;

            String line = fr.readLine();
            while(line != null) {
                if(line.equals("vertices")) {
                    for (int i = 0; i < vertices.length; ++i) {
                        vertices[i] = Float.parseFloat(fr.readLine());
                    }
                    vertsRead = true;
                }
                else if( line.equals("texcoords") ) {
                    for (int i = 0; i < texcoords.length; ++i) {
                        texcoords[i] = Float.parseFloat(fr.readLine());
                    }
                }
                else if( line.equals("triangles")) {
                    for (int i = 0; i < triangles.length; ++i) {
                        triangles[i] = Integer.parseInt(fr.readLine());
                    }
                    trisRead = true;
                }
                line = fr.readLine();
            }

            // sanity checks
            if( !vertsRead )
                throw new Exception("Broken file - vertices expected");

            if( !trisRead )
                throw new Exception("Broken file - triangles expected.");

            computeNormals(nPolys);

        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    protected void computeNormals(int nPolys) {
        // compute normals

        normals = new float[nPolys * 3]; // one normal per triangle. To heck with
        // smoothed meshes

        Vector3f v0 = new Vector3f();
        Vector3f v1 = new Vector3f();
        Vector3f v2 = new Vector3f();
        Vector3f normal = new Vector3f();
        int v0i, v1i, v2i;
        for (int i = 0; i < nPolys; ++i) {
            v0i = 3 * triangles[3 * i + 0];
            v1i = 3 * triangles[3 * i + 1];
            v2i = 3 * triangles[3 * i + 2];

            v0.set(vertices[v0i], vertices[v0i + 1], vertices[v0i + 2]);
            v1.set(vertices[v1i], vertices[v1i + 1], vertices[v1i + 2]);
            v2.set(vertices[v2i], vertices[v2i + 1], vertices[v2i + 2]);

            v1.sub(v1, v0);
            v2.sub(v2, v0);

            normal.cross(v1, v2);
            normal.normalize();

            normals[3 * i + 0] = normal.x;
            normals[3 * i + 1] = normal.y;
            normals[3 * i + 2] = normal.z;
        }
    }

    /**
     * @see Scene#render(GLDrawable)
     */
    public void render(GLAutoDrawable d) {

        GL2 gl = d.getGL().getGL2();


        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, texture.nx, texture.ny, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, texture.cBuf);
        texture.cBuf.rewind();

        gl.glBegin(GL.GL_TRIANGLES);
        for (int i = 0; i < triangles.length; i += 3) {
            gl.glColor3f(1f, 1f, 1f);

            int v0 = 3 * triangles[i + 0];
            int v1 = 3 * triangles[i + 1];
            int v2 = 3 * triangles[i + 2];

            gl.glNormal3f(normals[i], normals[i + 1], normals[i + 2]);

            gl.glTexCoord2f(texcoords[triangles[i] * 2], texcoords[triangles[i] * 2 + 1]);
            gl.glVertex3f(vertices[v0], vertices[v0 + 1], vertices[v0 + 2]);

            gl.glTexCoord2f(texcoords[triangles[i + 1] * 2], texcoords[triangles[i + 1] * 2 + 1]);
            gl.glVertex3f(vertices[v1], vertices[v1 + 1], vertices[v1 + 2]);

            gl.glTexCoord2f(texcoords[triangles[i + 2] * 2], texcoords[triangles[i + 2] * 2 + 1]);
            gl.glVertex3f(vertices[v2], vertices[v2 + 1], vertices[v2 + 2]);
        }
        gl.glEnd();

    }

    private static final Vector3f v0 = new Vector3f();

    private static final Vector3f n0 = new Vector3f();

    private static final Vector2f t0 = new Vector2f();

    private static final Vector3f v1 = new Vector3f();

    private static final Vector3f n1 = new Vector3f();

    private static final Vector2f t1 = new Vector2f();

    private static final Vector3f v2 = new Vector3f();

    private static final Vector3f n2 = new Vector3f();

    private static final Vector2f t2 = new Vector2f();

    private static final Color3f c0 = new Color3f(1, 1, 1);

    private static final Vector3f[] v = new Vector3f[] { v0, v1, v2 };

    private static final Vector3f[] n = new Vector3f[] { n0, n1, n2 };

    private static final Vector2f[] t = new Vector2f[] { t0, t1, t2 };

    private static final Color3f[] c = new Color3f[] { c0, c0, c0 };

    /**
     * @see Scene#render(Pipeline)
     */
    public void render(Pipeline pipe) {

        pipe.setTexture(texture);

        for (int i = 0; i < triangles.length; i += 3) {
            int iv0 = 3 * triangles[i + 0];
            int iv1 = 3 * triangles[i + 1];
            int iv2 = 3 * triangles[i + 2];
            v0.set(vertices[iv0], vertices[iv0 + 1], vertices[iv0 + 2]);
            v1.set(vertices[iv1], vertices[iv1 + 1], vertices[iv1 + 2]);
            v2.set(vertices[iv2], vertices[iv2 + 1], vertices[iv2 + 2]);

            n0.set(normals[i], normals[i + 1], normals[i + 2]);
            n1.set(n0);
            n2.set(n0); // would have to change this to do smoothed normals

            t0.set(texcoords[triangles[i + 0] * 2], texcoords[triangles[i + 0] * 2 + 1]);
            t1.set(texcoords[triangles[i + 1] * 2], texcoords[triangles[i + 1] * 2 + 1]);
            t2.set(texcoords[triangles[i + 2] * 2], texcoords[triangles[i + 2] * 2 + 1]);

            pipe.renderTriangle(v, c, n, t);
        }
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {

        return filename;
    }

}
