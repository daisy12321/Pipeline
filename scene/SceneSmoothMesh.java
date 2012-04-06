package pipeline.scene;

import java.io.File;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLDrawable;
import javax.vecmath.Color3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import pipeline.Pipeline;

/**
 * Same as SceneMesh, except this will compute averaged normals.
 * This results in the mesh looking smoother if normals are interpolated.
 * Good for reflection map testing.
 * @author stevenan
 *
 */
public class SceneSmoothMesh extends SceneMesh {
    public SceneSmoothMesh(File meshFile) {
        super(meshFile);
    }

    @Override
    protected void computeNormals(int nPolys) {
        // compute normals
        // one averaged normal per vertex, for smooth-able rendering
        int nVerts = this.vertices.length;
        normals = new float[nVerts * 3];

        // Go through every triangle and add its normal to all incident vertices
        Vector3f v0 = new Vector3f();
        Vector3f v1 = new Vector3f();
        Vector3f v2 = new Vector3f();
        Vector3f normal = new Vector3f();
        int v0i, v1i, v2i;
        // zero out accumulators
        for( int i = 0; i < normals.length; i++ ) normals[i] = 0f;
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

            // Do NOT normalize, to get area-weighted averages
            // normal.normalize();

            normals[v0i + 0] += normal.x; normals[v0i + 1] += normal.y;	normals[v0i + 2] += normal.z;
            normals[v1i + 0] += normal.x; normals[v1i + 1] += normal.y;	normals[v1i + 2] += normal.z;
            normals[v2i + 0] += normal.x; normals[v2i + 1] += normal.y;	normals[v2i + 2] += normal.z;
        }

        // now go through and average
        for( int i = 0; i < nVerts; i++ ) {
            normal.set(normals[3*i + 0], normals[3*i + 1], normals[3*i + 2]);
            normal.normalize();
            normals[3*i + 0] = normal.x;
            normals[3*i + 1] = normal.y;
            normals[3*i + 2] = normal.z;
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

            gl.glNormal3f(normals[v0], normals[v0 + 1], normals[v0 + 2]);
            gl.glTexCoord2f(texcoords[triangles[i] * 2], texcoords[triangles[i] * 2 + 1]);
            gl.glVertex3f(vertices[v0], vertices[v0 + 1], vertices[v0 + 2]);

            gl.glNormal3f(normals[v1], normals[v1 + 1], normals[v1 + 2]);
            gl.glTexCoord2f(texcoords[triangles[i + 1] * 2], texcoords[triangles[i + 1] * 2 + 1]);
            gl.glVertex3f(vertices[v1], vertices[v1 + 1], vertices[v1 + 2]);

            gl.glNormal3f(normals[v2], normals[v2 + 1], normals[v2 + 2]);
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

            n0.set(normals[iv0], normals[iv0 + 1], normals[iv0 + 2]);
            n1.set(normals[iv1], normals[iv1 + 1], normals[iv1 + 2]);
            n2.set(normals[iv2], normals[iv2 + 1], normals[iv2 + 2]);

            t0.set(texcoords[triangles[i + 0] * 2], texcoords[triangles[i + 0] * 2 + 1]);
            t1.set(texcoords[triangles[i + 1] * 2], texcoords[triangles[i + 1] * 2 + 1]);
            t2.set(texcoords[triangles[i + 2] * 2], texcoords[triangles[i + 2] * 2 + 1]);

            pipe.renderTriangle(v, c, n, t);
        }
    }

    public String toString() {

        return filename + " (Smooth)";
    }
}
