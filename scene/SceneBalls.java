package pipeline.scene;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLDrawable;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

import pipeline.Pipeline;
import pipeline.misc.Geometry;

/**
 * This scene subclass renders two simple spheres floating in space. They can be
 * colored, textured, etc.
 * 
 * @author ags
 */
public class SceneBalls extends Scene {

    /** The triangulation depth of the spheres */
    protected static final int DEPTH = 3;

    /** The color of the first sphere. */
    protected Color3f colorA = new Color3f(0.4f, 0.5f, 0.8f);

    /** The color of the second sphere. */
    protected Color3f colorB = new Color3f(0.8f, 0.5f, 0.4f);

    /** The amount to translate the center of the first sphere. */
    protected Vector3f locationA = new Vector3f(1.2f, 0.0f, 0.0f);

    /** The amount to translate the center of the second sphere. */
    protected Vector3f locationB = new Vector3f(-2.4f, 0.0f, 0.0f);

    /**
     * @see Scene#render(GLDrawable)
     */
    public void render(GLAutoDrawable d) {

        GL2 gl = d.getGL().getGL2();

        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, texture.nx, texture.ny, 0, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, texture.cBuf);
        texture.cBuf.rewind();

        gl.glTranslatef(1.2f, 0.0f, 0.0f);
        Geometry.sphere(DEPTH, colorA, d);

        gl.glTranslatef(-2.4f, 0.0f, 0.0f);
        Geometry.sphere(DEPTH, colorB, d);
    }

    /**
     * @see Scene#render(Pipeline)
     */
    public void render(Pipeline pipe) {

        pipe.setTexture(texture);

        pipe.translate(locationA);
        Geometry.sphere(DEPTH, colorA);

        pipe.translate(locationB);
        Geometry.sphere(DEPTH, colorB);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {

        return "Balls";
    }

}
