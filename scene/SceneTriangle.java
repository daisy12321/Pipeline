package pipeline.scene;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.vecmath.Color3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import pipeline.Pipeline;

/**
 * @author Beowulf
 */
public class SceneTriangle extends Scene {

    protected final Vector3f[] v = { new Vector3f(1, 0, 0), new Vector3f(0, 2, 0), new Vector3f(-1, 0, 0) };

    protected final Color3f[] c = { new Color3f(1, 0, 0), new Color3f(0, 1, 0), new Color3f(0, 0, 1) };

    protected final Vector2f[] t = { new Vector2f(1, 0), new Vector2f(0.5f, 1), new Vector2f(0, 0) };

    protected final Vector3f[] n = { new Vector3f(0, 0, 1), new Vector3f(0, 0, 1), new Vector3f(0, 0, 1) };

    protected final Vector3f[] v2 = { new Vector3f(0, 2, 0), new Vector3f(1, 0, 0), new Vector3f(-1, 0, 0) };

    protected final Color3f[] c2 = { new Color3f(0.25f, 0.75f, 0.25f), new Color3f(0.75f, 0.25f, 0.25f), new Color3f(0.25f, 0.25f, 0.75f) };

    protected final Vector2f[] t2 = { new Vector2f(0.5f, 1), new Vector2f(1, 0), new Vector2f(0, 0) };

    protected final Vector3f[] n2 = { new Vector3f(0, 0, -1), new Vector3f(0, 0, -1), new Vector3f(0, 0, -1) };

    /**
     * @see pipeline.solution.Scene#render(net.java.games.jogl.GLDrawable)
     */
    public void render(GLAutoDrawable d) {

        GL2 gl = d.getGL().getGL2();

        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, texture.nx, texture.ny, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, texture.cBuf);
        texture.cBuf.rewind();

        gl.glBegin(GL.GL_TRIANGLES);
        gl.glNormal3f(n[0].x, n[0].y, n[0].z);

        for (int ctr = 0; ctr < 3; ctr++) {
            gl.glTexCoord2f(t[ctr].x, t[ctr].y);
            gl.glColor3f(c[ctr].x, c[ctr].y, c[ctr].z);
            gl.glVertex3f(v[ctr].x, v[ctr].y, v[ctr].z);
        }

        gl.glNormal3f(n2[0].x, n2[0].y, n2[0].z);

        for (int ctr = 0; ctr < 3; ctr++) {
            gl.glTexCoord2f(t2[ctr].x, t2[ctr].y);
            gl.glColor3f(c2[ctr].x, c2[ctr].y, c2[ctr].z);
            gl.glVertex3f(v2[ctr].x, v2[ctr].y, v2[ctr].z);
        }
        gl.glEnd();
    }

    /**
     * @see pipeline.solution.Scene#render(pipeline.solution.Pipeline)
     */
    public void render(Pipeline pipeline) {

        pipeline.setTexture(texture);
        pipeline.begin(Pipeline.TRIANGLES);
        for (int k = 0; k < 3; k++)
            pipeline.vertex(v[k], c[k], n[k], t[k]);
        for (int k = 0; k < 3; k++)
            pipeline.vertex(v2[k], c2[k], n2[k], t2[k]);
        pipeline.end();
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {

        return "Simple Triangle";
    }

}
